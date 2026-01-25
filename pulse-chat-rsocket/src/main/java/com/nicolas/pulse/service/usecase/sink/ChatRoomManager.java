package com.nicolas.pulse.service.usecase.sink;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatRoomManager {
    private final ConcurrentMap<RSocketRequester, String> sessionToAccount = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Sinks.Many<ChatMessage>> roomBroadcasters = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<String>> userSubscriptions = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, LongAdder> roomMemberCounters = new ConcurrentHashMap<>();
    private final Sinks.Many<RoomControlSignal> kickSink = Sinks.many().multicast().directBestEffort();

    public record RoomControlSignal(String accountId, String roomId, Type type) {
        public enum Type {KICK_MEMBER, ROOM_DELETED, USER_OFFLINE}
    }

    public ChatRoomManager(ChatEventBus eventBus) {
        eventBus.onRoomDelete()
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(event -> this.kickOutRoom(event.roomId()))
                .onErrorContinue((throwable, obj) -> log.error("Process Delete Room Error. Error: {}, Object: {}.", throwable.getMessage(), obj))
                .subscribe();
        eventBus.onMemberDelete()
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(deleteMemberEvent -> deleteMemberEvent.accountIdSet().forEach(a -> this.kickMemberOutOfRoom(a, deleteMemberEvent.roomId())))
                .onErrorContinue((throwable, obj) -> log.error("Process Delete Member Error. Error: {}, Object: {}.", throwable.getMessage(), obj))
                .subscribe();
    }

    public Flux<ChatMessage> subscribe(String accountId, String roomId) {
        userSubscriptions.computeIfAbsent(accountId, k -> ConcurrentHashMap.newKeySet()).add(roomId);
        roomMemberCounters.computeIfAbsent(roomId, k -> new LongAdder()).increment();
        return roomBroadcasters.computeIfAbsent(roomId, k -> Sinks.many().multicast().directBestEffort())
                .asFlux()
                .takeUntilOther(kickSink.asFlux().filter(sig ->
                        (sig.type() == RoomControlSignal.Type.ROOM_DELETED && sig.roomId().equals(roomId))
                                || (sig.type() == RoomControlSignal.Type.KICK_MEMBER && sig.roomId().equals(roomId) && sig.accountId().equals(accountId))
                                || (sig.type() == RoomControlSignal.Type.USER_OFFLINE && sig.accountId().equals(accountId))))
                .doFinally(signalType -> {
                    this.unSubscribeInternal(accountId, roomId);
                    this.decrementCounter(roomId);
                });
    }

    public void unSubscribeInternal(String accountId, String roomId) {
        userSubscriptions.computeIfPresent(accountId, (accId, rooms) -> {
            if (rooms.remove(roomId)) {
                log.info("Cleanup: Account '{}' removed from room '{}'. Remaining: {}", accId, roomId, rooms.size());
            }
            return rooms.isEmpty() ? null : rooms;
        });
    }

    private void decrementCounter(String roomId) {
        roomMemberCounters.computeIfPresent(roomId, (id, counter) -> {
            counter.decrement();
            return counter.sum() <= 0 ? null : counter;
        });
    }

    private void kickMemberOutOfRoom(String accountId, String roomId) {
        kickSink.tryEmitNext(new RoomControlSignal(accountId, roomId, RoomControlSignal.Type.KICK_MEMBER));
    }

    private void kickOutRoom(String roomId) {
        log.info("Signal: Room '{}' is being deleted.", roomId);
        kickSink.tryEmitNext(new RoomControlSignal(null, roomId, RoomControlSignal.Type.ROOM_DELETED));
        roomBroadcasters.remove(roomId);
    }

    public void handleUserOffline(RSocketRequester requester) {
        String accountId = sessionToAccount.remove(requester);
        if (StringUtils.hasText(accountId) && !sessionToAccount.containsValue(accountId)) {
            kickSink.tryEmitNext(new RoomControlSignal(accountId, null, RoomControlSignal.Type.USER_OFFLINE));
        }
    }

    public void broadcastMessage(ChatMessage message) {
        Sinks.Many<ChatMessage> sink = roomBroadcasters.get(message.getRoomId());
        if (sink != null) {
            Sinks.EmitResult result = sink.tryEmitNext(message);
            if (result.isFailure()) {
                log.error("Failed to push message in room {}: {}.", message.getRoomId(), result);
            }
        }
    }

    public void registerSession(RSocketRequester requester, String accountId) {
        sessionToAccount.put(requester, accountId);
    }

    public Set<String> getActiveRoomIds() {
        return roomBroadcasters.keySet();
    }

    public int getSessionCount() {
        return sessionToAccount.size();
    }

    public Map<String, Long> getRoomStats() {
        return roomMemberCounters.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().sum()));
    }

    public Long getSingleRoomMemberCount(String roomId) {
        LongAdder counter = roomMemberCounters.get(roomId);
        return (counter != null) ? counter.sum() : 0L;
    }
}
