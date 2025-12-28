package com.nicolas.pulse.service.usecase.sink;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
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
        RoomContext roomContext = roomContexts.computeIfAbsent(roomId, k -> new RoomContext());
        roomContext.increment(accountId);
        log.info("Subscribed room '{}', account id= '{}'.", roomId, accountId);
        return roomContext.getAccountSkins().get(accountId);
    }

    public void unSubscribe(String accountId, String roomId) {
        RoomContext roomContext = roomContexts.get(roomId);
        if (roomContext != null) {
            roomContext.decrement(accountId);
            log.info("Un Subscribed room '{}', account id= '{}'.", roomId, accountId);
            removeRoom(roomId);
        }
    }

    public void kickOutAccount(String roomId, String accountId) {
        RoomContext context = roomContexts.get(roomId);
        if (context == null) {
            return;
        }

        Sinks.Many<ChatMessage> sink = context.getAccountSkins().remove(accountId);
        if (sink != null) {
            log.warn("Kick out account, account id = '{}', room id = '{}'.", accountId, roomId);
            // Send error to stop sinks
            sink.tryEmitError(new AccessDeniedException("Account is not a member of chat room, room id = '%s'.".formatted(roomId)));
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

    private void removeRoom(String roomId) {
        RoomContext roomContext = roomContexts.get(roomId);
        if (roomContext.getTotalSubscriber() < 1) {
            roomContexts.remove(roomId);
            log.info("Room no account Subscribe close.");
        }
    }
}
