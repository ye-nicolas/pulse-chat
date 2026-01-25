package com.nicolas.pulse.adapter.health;

import com.nicolas.pulse.service.usecase.sink.ChatRoomManager;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ChatRoomHealthIndicator implements HealthIndicator {
    private static final int MAX_SESSIONS = 10000;
    private static final int MAX_ROOMS = 1000;
    private final ChatRoomManager chatRoomManager;

    public ChatRoomHealthIndicator(ChatRoomManager chatRoomManager) {
        this.chatRoomManager = chatRoomManager;
    }

    @Override
    public Health health() {
        int sessionCount = chatRoomManager.getSessionCount();
        int roomCount = chatRoomManager.getActiveRoomIds().size();

        Health.Builder status = Health.up();

        if (sessionCount > MAX_SESSIONS) {
            status = Health.status("OVERLOADED");
        } else if (sessionCount == 0 && roomCount > 0) {
            status = Health.unknown();
        }
        return status
                .withDetail("sessions", sessionCount)
                .withDetail("rooms", roomCount)
                .withDetail("sessionThreshold", MAX_SESSIONS)
                .withDetail("timestamp", Instant.now())
                .build();
    }
}
