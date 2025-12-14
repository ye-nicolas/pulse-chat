package com.nicolas.pulse.service.usecase.sink;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@Service
public class ChatRoomManager {
    private final ConcurrentMap<String, RoomContext> roomContexts = new ConcurrentHashMap<>();

    @Getter
    private static class RoomContext {
        final ConcurrentMap<String, Sinks.Many<ChatMessage>> accountSkins = new ConcurrentHashMap<>();

        public void increment(String accountId) {
            accountSkins.computeIfAbsent(accountId, k -> Sinks.many().unicast().onBackpressureBuffer(new LinkedBlockingDeque<>(10)));
        }

        public void decrement(String accountId) {
            Sinks.Many<ChatMessage> removedSink = accountSkins.remove(accountId);
            if (removedSink != null) {
                removedSink.tryEmitComplete();
            }
        }

        public int getTotalSubscriber() {
            return accountSkins.size();
        }
    }

    public Sinks.Many<ChatMessage> subscribe(String accountId, String roomId) {
        RoomContext context = roomContexts.computeIfAbsent(roomId, k -> new RoomContext());
        context.increment(accountId);
        log.info("Subscribed room '{}', account id= '{}'.", roomId, accountId);
        return context.getAccountSkins().get(roomId);
    }

    public void unSubscribe(String accountId, String roomId) {
        RoomContext context = roomContexts.get(roomId);
        if (context != null) {
            context.decrement(accountId);
            log.info("Un Subscribed room '{}', account id= '{}'.", roomId, accountId);
            if (context.getTotalSubscriber() < 1) {
                removeRoomSink(roomId);
                log.info("Room no account Subscribe close.");
            }
        }
    }

    public void broadcastMessage(ChatMessage message) {
        RoomContext context = roomContexts.get(message.getRoomId());
        if (context != null) {
            context.getAccountSkins().forEach((accountId, sink) -> {
                Sinks.EmitResult result = sink.tryEmitNext(message);
                if (result.isFailure()) {
                    log.warn("Failed to emit message to AccountId: {} in RoomId: {}. Result: {}", accountId, message.getRoomId(), result);
                }
            });
        }
    }

    private void removeRoomSink(String roomId) {
        roomContexts.remove(roomId);
    }
}
