package com.nicolas.pulse.adapter.actuator;

import com.nicolas.pulse.service.usecase.sink.ChatRoomManager;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Component
@Endpoint(id = "chatroom")
public class ChatRoomActuator {
    private final ChatRoomManager chatRoomManager;

    public ChatRoomActuator(ChatRoomManager chatRoomManager) {
        this.chatRoomManager = chatRoomManager;
    }

    @ReadOperation
    public WebEndpointResponse<ChatRoomStatsResponse> getChatRoomStats(@Nullable Integer limit) {
        int finalLimit = (limit == null || limit <= 0) ? 10 : Math.min(limit, 100);
        Map<String, Long> roomStats = chatRoomManager.getRoomStats();
        if (roomStats.isEmpty()) {
            return new WebEndpointResponse<>(createEmptyResponse());
        }

        List<RoomCount> topRooms = roomStats.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(finalLimit)
                .map(e -> new RoomCount(e.getKey(), e.getValue()))
                .toList();

        return new WebEndpointResponse<>(new ChatRoomStatsResponse(
                OffsetDateTime.now(ZoneOffset.UTC).toString(),
                chatRoomManager.getSessionCount(),
                roomStats.size(),
                topRooms
        ));
    }

    @ReadOperation
    public WebEndpointResponse<RoomDetailResponse> getRoomDetail(@Selector String roomId) {
        Long count = chatRoomManager.getSingleRoomMemberCount(roomId);
        if (count == 0 && !chatRoomManager.getActiveRoomIds().contains(roomId)) {
            return new WebEndpointResponse<>(HttpStatus.NOT_FOUND.value());
        }
        return new WebEndpointResponse<>(new RoomDetailResponse(
                roomId,
                count,
                "ACTIVE",
                OffsetDateTime.now(ZoneOffset.UTC).toString()));
    }

    public record ChatRoomStatsResponse(String timestamp, int totalSessions, int activeRoomsCount, List<RoomCount> topRooms) {
    }

    public record RoomCount(String roomId, long count) {
    }

    public record RoomDetailResponse(String roomId, long memberCount, String status, String lastUpdated) {
    }

    private ChatRoomStatsResponse createEmptyResponse() {
        return new ChatRoomStatsResponse(
                OffsetDateTime.now(ZoneOffset.UTC).toString(),
                chatRoomManager.getSessionCount(),
                0,
                List.of()
        );
    }
}
