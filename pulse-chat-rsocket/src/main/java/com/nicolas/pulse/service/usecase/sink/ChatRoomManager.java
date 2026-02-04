package com.nicolas.pulse.service.usecase.sink;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
@Service
public class ChatRoomManager {
    private static final String GAUGE_SESSION_ACTIVE = "chatroom.sessions.active";
    private static final String GAUGE_ROOM_ACTIVE = "chatroom.rooms.active";
    private final ConcurrentMap<RSocketRequester, String> sessionToAccount = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, LongAdder> accountSessionCounters = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Sinks.Many<ChatMessage>> roomBroadcasters = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<String>> userSubscriptions = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, LongAdder> roomMemberCounters = new ConcurrentHashMap<>();
    private final Sinks.Many<RoomControlSignal> kickSink = Sinks.many().multicast().directBestEffort();

    public record RoomControlSignal(String accountId, String roomId, Type type) {
        public enum Type {KICK_MEMBER, ROOM_DELETED, USER_OFFLINE}
    }

    public ChatRoomManager(ChatEventBus eventBus, MeterRegistry meterRegistry) {
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
        Gauge.builder(GAUGE_SESSION_ACTIVE, this, ChatRoomManager::getSessionCount)
                .description("Total active RSocket sessions")
                .register(meterRegistry);
        Gauge.builder(GAUGE_ROOM_ACTIVE, this, (chatRoomManager) -> chatRoomManager.getActiveRoomIds().size())
                .description("Total active chat rooms")
                .register(meterRegistry);
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
            if (counter.sum() <= 0) {
                roomBroadcasters.remove(roomId);
                log.info("Cleanup: Room '{}' is empty and has been removed.", roomId);
                return null;
            }
            return counter;
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
        if (StringUtils.hasText(accountId)) {
            accountSessionCounters.computeIfPresent(accountId, (id, counter) -> {
                counter.decrement();
                if (counter.sum() <= 0) {
                    kickSink.tryEmitNext(new RoomControlSignal(accountId, null, RoomControlSignal.Type.USER_OFFLINE));
                    return null;
                }
                return counter;
            });
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
        accountSessionCounters.computeIfAbsent(accountId, k -> new LongAdder()).increment();
    }

    public Set<String> getActiveRoomIds() {
        return roomBroadcasters.keySet();
    }

    public int getSessionCount() {
        return sessionToAccount.size();
    }
}
