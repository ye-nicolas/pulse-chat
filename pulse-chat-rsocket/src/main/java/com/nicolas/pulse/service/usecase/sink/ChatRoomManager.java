package com.nicolas.pulse.service.usecase.sink;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
public class ChatRoomManager {
    private final ConcurrentMap<String, RoomContext> roomContexts = new ConcurrentHashMap<>();

    private static class RoomContext {
        @Getter
        private final Sinks.Many<ChatMessage> sink;
        private final Set<String> activeAccountIdSet = Collections.newSetFromMap(new ConcurrentHashMap<>());

        public RoomContext(Sinks.Many<ChatMessage> sink) {
            this.sink = sink;
        }

        public void increment(String accountId) {
            activeAccountIdSet.add(accountId);
        }

        public int decrementAndGet(String accountId) {
            activeAccountIdSet.remove(accountId);
            return getTotalSubscriber();
        }

        public int getTotalSubscriber() {
            return activeAccountIdSet.size();
        }
    }

    public Sinks.Many<ChatMessage> subscribe(String accountId, String roomId) {
        RoomContext context = roomContexts.computeIfAbsent(roomId, k -> new RoomContext(Sinks.many().multicast().onBackpressureBuffer(256)));
        context.increment(accountId);
        log.info("Subscribed room '{}', account id= '{}'.", roomId, accountId);
        return context.getSink();
    }

    public void unSubscribe(String accountId, String roomId) {
        RoomContext context = roomContexts.get(roomId);
        if (context != null) {
            int totalSubscribe = context.decrementAndGet(accountId);
            log.info("Un Subscribed room '{}', account id= '{}'.", roomId, accountId);
            if (totalSubscribe < 1) {
                removeRoomSink(roomId);
                log.info("Room no account Subscribe close.");
            }
        }
    }

    public void broadcastMessage(ChatMessage message) {
        RoomContext context = roomContexts.get(message.getRoomId());
        if (context != null) {
            Sinks.EmitResult emitResult = context.sink.tryEmitNext(message);
            if (emitResult.isFailure()) {
                log.warn("Failed to emit message to room \"{}\". Result: {}.", message.getRoomId(), emitResult);
            }
        }
    }

    private void removeRoomSink(String roomId) {
        RoomContext removedContext = roomContexts.remove(roomId);
        if (removedContext != null) {
            removedContext.sink.tryEmitComplete();
        }
    }
}
