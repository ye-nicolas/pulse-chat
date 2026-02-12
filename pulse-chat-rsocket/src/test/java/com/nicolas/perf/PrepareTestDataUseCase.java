package com.nicolas.perf;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.account.AccountData;
import com.nicolas.pulse.adapter.repository.chat.message.ChatMessageData;
import com.nicolas.pulse.adapter.repository.chat.room.ChatRoomData;
import com.nicolas.pulse.adapter.repository.chat.room.member.ChatRoomMemberData;
import com.nicolas.pulse.entity.enumerate.ChatMessageType;
import com.nicolas.util.*;
import lombok.*;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class PrepareTestDataUseCase {
    private static final String encodePwd = "$2a$10$yjj8bwj1RPn146if3K9SeerD8o/NReFTBokcpWVRbYNzkYZDr7GOm";
    private static final AccountData ROOT = AccountData.builder()
            .id("01KDFWP8CSRTWJ04A6WS7CDNWM")
            .name("root")
            .showName("root")
            .password(encodePwd)
            .isActive(true)
            .updatedAt(Instant.now())
            .createdAt(Instant.now())
            .build();

    public static void main(String[] args) {
        PrepareTestDataUseCase testDataUseCase = new PrepareTestDataUseCase();
        Input build = Input.builder()
                .accountSize(50)
                .roomSize(20)
                .url("jdbc:postgresql://localhost:5433/pulse_chat?currentSchema=pulse_chat")
                .user("nicolas")
                .pwd("123456789")
                .build();
        Output output = new Output();
        testDataUseCase.execute(build, output);
        output.getMemberVo().forEach(System.out::println);
    }

    public void execute(Input input, Output output) {
        try {
            Connection connection = DriverManager.getConnection(input.getUrl(), input.getUser(), input.getPwd());
            connection.setAutoCommit(false);

            Map<String, String> accountData = getAccountData(input.getAccountSize(), (batch) -> {
                String sql = InsertUtil.getInsert(DbMeta.AccountData.TABLE_NAME, batch, AccountDataMapping.MAPPING);
                insert(sql, connection);
            });
            List<String> chatRoomIdList = getChatRoomData(input.getRoomSize(), (batch) -> {
                String sql = InsertUtil.getInsert(DbMeta.ChatRoomData.TABLE_NAME, batch, ChatRoomDataMapping.MAPPING);
                insert(sql, connection);
            });

            Map<String, List<TempVo>> chatRoomMemberDistribution = getChatRoomMemberDistribution(new ArrayList<>(accountData.keySet()), chatRoomIdList, (batch) -> {
                String sql = InsertUtil.getInsert(DbMeta.ChatRoomMemberData.TABLE_NAME, batch, ChatRoomMemberDataMapping.MAPPING);
                insert(sql, connection);
            });

            processChatMessageInBatches(chatRoomMemberDistribution, (batch) -> {
                String sql = InsertUtil.getInsert(DbMeta.ChatMessageData.TABLE_NAME, batch, ChatMessageDataMapping.MAPPING);
                insert(sql, connection);
            });
            output.setMemberVo(aggregate(chatRoomMemberDistribution, accountData));
            insert("ANALYZE \"%s\";".formatted(DbMeta.AccountData.TABLE_NAME), connection);
            insert("ANALYZE \"%s\";".formatted(DbMeta.ChatRoomData.TABLE_NAME), connection);
            insert("ANALYZE \"%s\";".formatted(DbMeta.ChatRoomMemberData.TABLE_NAME), connection);
            insert("ANALYZE \"%s\";".formatted(DbMeta.ChatMessageData.TABLE_NAME), connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insert(String sql, Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("資料庫寫入失敗", e);
        }
    }

    private List<MemberVo> aggregate(Map<String, List<TempVo>> chatRoomMemberDistribution, Map<String, String> accountData) {
        Map<String, MemberVo> tempMap = new HashMap<>();
        for (Map.Entry<String, List<TempVo>> stringListEntry : chatRoomMemberDistribution.entrySet()) {
            String roomId = stringListEntry.getKey();
            for (TempVo tempVo : stringListEntry.getValue()) {
                MemberVo memberVo = tempMap.computeIfAbsent(tempVo.accountId, accountId -> MemberVo.builder()
                        .accountId(accountId)
                        .name(accountData.get(accountId))
                        .build());
                memberVo.getChatRoomIdSet().add(roomId);
            }
        }
        return tempMap.values().stream().toList();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Input {
        private int accountSize;
        private int roomSize;
        private String url;
        private String user;
        private String pwd;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Output {
        private List<MemberVo> memberVo;
    }

    private Map<String, String> getAccountData(int size, Consumer<List<AccountData>> consumer) {
        Map<String, String> map = new HashMap<>(size);
        List<AccountData> batchBuffer = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            AccountData accountData = AccountData.builder()
                    .id(UlidCreator.getMonotonicUlid().toString())
                    .name("user_" + i)
                    .showName("show_" + i)
                    .password(encodePwd)
                    .isActive(true)
                    .updatedAt(Instant.now())
                    .createdAt(Instant.now())
                    .updatedBy(ROOT.getId())
                    .createdBy(ROOT.getId())
                    .build();
            batchBuffer.add(accountData);
            map.put(accountData.getId(), accountData.getName());
            if (batchBuffer.size() > 1000) {
                consumer.accept(new ArrayList<>(batchBuffer));
                batchBuffer.clear();
            }
        }

        if (!batchBuffer.isEmpty()) {
            consumer.accept(new ArrayList<>(batchBuffer));
            batchBuffer.clear();
        }
        return map;
    }

    private ChatRoomData getChatRoom(int i) {
        return ChatRoomData.builder()
                .id(UlidCreator.getMonotonicUlid().toString())
                .name("room_" + i)
                .updatedAt(Instant.now())
                .createdAt(Instant.now())
                .updatedBy(ROOT.getId())
                .createdBy(ROOT.getId())
                .build();
    }

    private List<String> getChatRoomData(int size, Consumer<List<ChatRoomData>> consumer) {
        List<String> list = new ArrayList<>(size);
        List<ChatRoomData> batchBuffer = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ChatRoomData chatRoom = getChatRoom(i);
            batchBuffer.add(chatRoom);
            list.add(chatRoom.getId());
            if (batchBuffer.size() > 2000) {
                consumer.accept(new ArrayList<>(batchBuffer));
                batchBuffer.clear();
            }
        }
        if (!batchBuffer.isEmpty()) {
            consumer.accept(new ArrayList<>(batchBuffer));
            batchBuffer.clear();
        }
        return list;
    }

    private Map<String, List<TempVo>> getChatRoomMemberDistribution(List<String> accountIdList, List<String> roomIdList, Consumer<List<ChatRoomMemberData>> consumer) {
        List<ChatRoomMemberData> batchBuffer = new ArrayList<>();
        Map<String, List<TempVo>> roomMap = new HashMap<>();
        int roomCount = roomIdList.size();
        for (int i = 0; i < roomCount; i++) {
            String roomId = roomIdList.get(i);
            double ratio = (double) i / roomCount;
            int memberSize;
            if (ratio < 0.20) {
                memberSize = 2;
            } else if (ratio < 0.90) {
                memberSize = ThreadLocalRandom.current().nextInt(2, 6);
            } else if (ratio < 0.99) {
                memberSize = ThreadLocalRandom.current().nextInt(10, 51);
            } else {
                memberSize = ThreadLocalRandom.current().nextInt(100, 501);
            }

            Set<String> selectedAccIds = randomSet(accountIdList, memberSize);
            for (String accId : selectedAccIds) {
                ChatRoomMemberData chatRoomMemberData = ChatRoomMemberData.builder()
                        .id(UlidCreator.getMonotonicUlid().toString())
                        .accountId(accId)
                        .roomId(roomId)
                        .isMuted(false)
                        .isPinned(false)
                        .updatedAt(Instant.now())
                        .createdAt(Instant.now())
                        .updatedBy(ROOT.getId())
                        .createdBy(ROOT.getId())
                        .build();
                batchBuffer.add(chatRoomMemberData);
                roomMap.computeIfAbsent(roomId, k -> new ArrayList<>()).add(new TempVo(chatRoomMemberData.getAccountId(), chatRoomMemberData.getId()));
                if (batchBuffer.size() > 1000) {
                    consumer.accept(new ArrayList<>(batchBuffer));
                    batchBuffer.clear();
                }
            }
        }
        if (!batchBuffer.isEmpty()) {
            consumer.accept(new ArrayList<>(batchBuffer));
            batchBuffer.clear();
        }
        return roomMap;
    }

    private void processChatMessageInBatches(Map<String, List<TempVo>> roomMap, Consumer<List<ChatMessageData>> batchProcessor) {
        List<ChatMessageData> roomMessages = new ArrayList<>(1010);
        for (String roomId : roomMap.keySet()) {
            List<TempVo> roomMembers = roomMap.get(roomId);
            int memberSize = roomMembers.size();
            int msgCount = calculateMsgCountByMemberSize(memberSize);
            for (int j = 0; j < msgCount; j++) {
                TempVo chatRoomMemberData = roomMembers.get(ThreadLocalRandom.current().nextInt(roomMembers.size()));
                roomMessages.add(ChatMessageData.builder()
                        .id(UlidCreator.getMonotonicUlid().toString())
                        .roomId(roomId)
                        .memberId(chatRoomMemberData.memberId())
                        .createdBy(chatRoomMemberData.accountId())
                        .type(ChatMessageType.TEXT)
                        .content(generateRandomContent(ThreadLocalRandom.current().nextInt(10, 200)))
                        .isDelete(false)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build());
            }
            if (roomMessages.size() >= 1000) {
                batchProcessor.accept(roomMessages);
                roomMessages.clear();
            }
        }
        if (!roomMessages.isEmpty()) {
            batchProcessor.accept(roomMessages);
        }
    }

    private Set<String> randomSet(List<String> list, int size) {
        int total = list.size();
        Set<String> resultSet = new HashSet<>();
        while (resultSet.size() < Math.min(size, total)) {
            resultSet.add(list.get(ThreadLocalRandom.current().nextInt(total)));
        }
        return resultSet;
    }

    private record TempVo(String accountId, String memberId) {

    }

    private String generateRandomContent(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
        }
        return sb.toString();
    }

    private int calculateMsgCountByMemberSize(int memberSize) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (memberSize <= 2) {
            return random.nextInt(0, 10);
        } else if (memberSize < 10) {
            return random.nextInt(20, 100);
        } else if (memberSize < 100) {
            return random.nextInt(500, 1500);
        } else {
            return random.nextInt(5000, 10001);
        }
    }
}

