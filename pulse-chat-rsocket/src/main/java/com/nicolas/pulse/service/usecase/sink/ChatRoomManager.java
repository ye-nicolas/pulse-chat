package com.nicolas.pulse.service.usecase.sink;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ChatRoomManager {
    private final Map<String, Sinks.Many<ChatMessage>> roomSinks = new ConcurrentHashMap<>();

    public Sinks.Many<ChatMessage> getOrCreateRoomSinks(String roomId) {
        return roomSinks.computeIfAbsent(roomId, id -> {
            log.info("Creating multicast Sinks for chat room: {}", roomId);
            return Sinks.many().multicast().onBackpressureBuffer(256);
        });
    }

    public void broadcastMessage(ChatMessage message) {
        Sinks.Many<ChatMessage> sinks = roomSinks.get(message.getRoomId());
        if (sinks != null) {
            Sinks.EmitResult result = sinks.tryEmitNext(message);
            if (result.isFailure()) {
                log.warn("Failed to emit message to room {}. Result: {}", message.getRoomId(), result);
            }
        }
    }
}
