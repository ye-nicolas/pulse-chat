package com.nicolas.pulse.service.usecase.sink;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
public class ChatRoomManager {
    private final ConcurrentMap<String, RoomContext> roomContexts = new ConcurrentHashMap<>();

    public ChatRoomManager(ChatEventBus eventBus) {
        eventBus.onRoomDelete()
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(event -> this.kickOutRoom(event.roomId()))
                .onErrorContinue((throwable, obj) -> log.error("Process Delete Room Error. Error: {}, Object: {}.", throwable.getMessage(), obj))
                .subscribe();
        eventBus.onMemberDelete()
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(deleteMemberEvent -> this.unSubscribeMembers(deleteMemberEvent.roomId(), deleteMemberEvent.accountIdSet()))
                .onErrorContinue((throwable, obj) -> log.error("Process Delete Member Error. Error: {}, Object: {}.", throwable.getMessage(), obj))
                .subscribe();
    }

    @Getter
    private static class RoomContext {
        final ConcurrentMap<String, Sinks.Many<ChatMessage>> accountSinks = new ConcurrentHashMap<>();

        public Sinks.Many<ChatMessage> getOrCreateSink(String accountId) {
            return accountSinks.computeIfAbsent(accountId, k -> Sinks.many().multicast().onBackpressureBuffer(10));
        }

        public void removeSink(String accountId) {
            Sinks.Many<ChatMessage> sink = accountSinks.remove(accountId);
            if (sink != null) {
                sink.tryEmitComplete();
            }
        }

        public boolean isEmpty() {
            return accountSinks.isEmpty();
        }
    }

    public Sinks.Many<ChatMessage> subscribe(String accountId, String roomId) {
        RoomContext room = roomContexts.computeIfAbsent(roomId, k -> new RoomContext());
        Sinks.Many<ChatMessage> sink = room.getOrCreateSink(accountId);
        log.info("Account '{}' subscribed to room '{}'. Current subscribers: {}", accountId, roomId, room.getAccountSinks().size());
        return sink;
    }

    public void unSubscribe(String accountId, String roomId) {
        if (StringUtils.hasText(accountId)) {
            roomContexts.computeIfPresent(roomId, (rid, context) -> {
                context.removeSink(accountId);
                if (context.isEmpty()) {
                    log.info("Room '{}' is empty, removing context.", rid);
                    return null;
                }
                return context;
            });
            log.info("Unsubscribed: Account '{}' from Room '{}'", accountId, roomId);
        }
    }

    public void unSubscribeMembers(String roomId, Set<String> accountIdSet) {
        roomContexts.computeIfPresent(roomId, (rid, context) -> {
            accountIdSet.forEach(context::removeSink);
            return context.isEmpty() ? null : context;
        });
    }

    public void broadcastMessage(ChatMessage message) {
        RoomContext context = roomContexts.get(message.getRoomId());
        if (context != null) {
            context.getAccountSinks().forEach((accountId, sink) -> {
                Sinks.EmitResult result = sink.tryEmitNext(message);
                if (result.isFailure()) {
                    log.error("Failed to push message to {} in room {}: {}", accountId, message.getRoomId(), result);
                    if (result == Sinks.EmitResult.FAIL_TERMINATED || result == Sinks.EmitResult.FAIL_CANCELLED) {
                        context.removeSink(accountId);
                    }
                }
                log.info("send");
            });
        }
    }

    public void kickOutRoom(String roomId) {
        roomContexts.computeIfPresent(roomId, (id, context) -> {
            // 在鎖的保護下關閉所有 Sink
            context.getAccountSinks().forEach((accId, sink) -> sink.tryEmitComplete());
            context.getAccountSinks().clear();
            return null;
        });
    }
}
