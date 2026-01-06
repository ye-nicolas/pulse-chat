package com.nicolas.pulse.service.usecase.listener;

import com.nicolas.pulse.entity.event.DeleteMemberEvent;
import com.nicolas.pulse.entity.event.DeleteRoomEvent;
import com.nicolas.pulse.service.usecase.sink.ChatRoomManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class ChatRoomEventListener {

    private final ChatRoomManager chatRoomManager;

    public ChatRoomEventListener(ChatRoomManager chatRoomManager) {
        this.chatRoomManager = chatRoomManager;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDeleteRoom(DeleteRoomEvent event) {
        chatRoomManager.kickOutRoom(event.roomId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDeleteRoom(DeleteMemberEvent event) {
        event.accountIdSet()
                .forEach(accountId -> chatRoomManager.kickOutAccount(event.roomId(), accountId));
    }
}
