package com.nicolas.pulse.service.usecase.sink;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class ChatRoomManager {
    private final ConcurrentMap<String, RoomContext> roomContexts = new ConcurrentHashMap<>();

    private static class RoomContext {
        @Getter
        private final Sinks.Many<ChatMessage> sink;
        private final AtomicInteger subscriberCount = new AtomicInteger(0);

        public RoomContext(Sinks.Many<ChatMessage> sink) {
            this.sink = sink;
        }

        public void increment() {
            subscriberCount.incrementAndGet();
        }

        public int decrementAndGet() {
            return subscriberCount.decrementAndGet();
        }

        public int getTotalSubscriber() {
            return subscriberCount.get();
        }
    }

    public Sinks.Many<ChatMessage> subscribe(String accountId, String roomId) {
        RoomContext context = roomContexts.computeIfAbsent(roomId, k -> new RoomContext(Sinks.many().multicast().onBackpressureBuffer(256)));
        context.increment();
        log.info("Subscribed room '{}', account id= '{}'.", roomId, accountId);
        return context.getSink();
    }

    public void unSubscribe(String roomId) {
        RoomContext context = roomContexts.get(roomId);
        if (context != null) {
            int totalSubscribe = context.decrementAndGet();
            log.info("Unsubscribed from room, room id = '{}'.", roomId);
            if (totalSubscribe < 1) {
                removeRoomSink(roomId);
                log.info("Room no account Subscribe close.");
            }
        }
    }

    private void removeRoomSink(String roomId) {
        RoomContext removedContext = roomContexts.remove(roomId);
        if (removedContext != null) {
            removedContext.sink.tryEmitComplete();
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
}
