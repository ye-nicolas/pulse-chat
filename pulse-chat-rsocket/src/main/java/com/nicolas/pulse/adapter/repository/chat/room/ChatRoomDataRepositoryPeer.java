package com.nicolas.pulse.adapter.repository.chat.room;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ChatRoomDataRepositoryPeer extends R2dbcRepository<ChatRoomData, String> {
}
