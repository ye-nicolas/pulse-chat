package com.nicolas.pulse.service.usecase.chat.message;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.ChatMessageRepository;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DeleteChatMessageUseCase {
    private final ChatMessageRepository chatMessageRepository;

    public DeleteChatMessageUseCase(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public Mono<Void> execute(Input input, Output output) {
        return chatMessageRepository.findById(input.getMessageId())
                .switchIfEmpty(Mono.error(new TargetNotFoundException("Message not found, message id = '%s'.".formatted(input.getMessageId()))))
                .flatMap(this::validateCanDeleteByAccount)
                .flatMap(this::validateCanDelete)
                .flatMap(this::deleteMessage)
                .doOnSuccess(output::setNewChatMessage)
                .then();
    }

    private Mono<ChatMessage> deleteMessage(ChatMessage chatMessage) {
        chatMessage.setContent("");
        chatMessage.setDelete(true);
        return chatMessageRepository.save(chatMessage);
    }

    private Mono<ChatMessage> validateCanDeleteByAccount(ChatMessage chatMessage) {
        return SecurityUtil.getCurrentAccountId()
                .flatMap(accountId -> {
                    if (accountId.equals(chatMessage.getCreatedBy())) {
                        return Mono.just(chatMessage);
                    }
                    return Mono.error(new AccessDeniedException("Can't delete message by permission denied, accountId = '%s'".formatted(accountId)));
                });
    }

    private Mono<ChatMessage> validateCanDelete(ChatMessage chatMessage) {
        if (chatMessage.isDelete()) {
            return Mono.error(new IllegalStateException("Message is already deleted, cannot delete again."));
        }
        return Mono.just(chatMessage);
    }

    @Data
    @AllArgsConstructor
    public static class Input {
        private String messageId;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        private ChatMessage newChatMessage;
    }
}
