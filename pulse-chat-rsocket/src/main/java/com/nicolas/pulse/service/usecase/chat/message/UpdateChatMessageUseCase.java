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
public class UpdateChatMessageUseCase {
    private final ChatMessageRepository chatMessageRepository;

    public UpdateChatMessageUseCase(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public Mono<Void> execute(Input input, Output output) {
        return chatMessageRepository.findById(input.getMessageId())
                .switchIfEmpty(Mono.error(new TargetNotFoundException("Message not found, message id = '%s'.".formatted(input.getMessageId()))))
                .flatMap(this::validateCanUpdateByAccount)
                .flatMap(this::validateCanUpdate)
                .flatMap(chatMessage -> updateMessage(chatMessage, input.getNewContent()))
                .doOnSuccess(output::setNewChatMessage)
                .then();
    }

    private Mono<ChatMessage> updateMessage(ChatMessage chatMessage, String newContent) {
        chatMessage.setContent(newContent);
        return chatMessageRepository.save(chatMessage);
    }

    private Mono<ChatMessage> validateCanUpdateByAccount(ChatMessage chatMessage) {
        return SecurityUtil.getCurrentAccountId()
                .flatMap(accountId -> {
                    if (accountId.equals(chatMessage.getCreatedBy())) {
                        return Mono.just(chatMessage);
                    }
                    return Mono.error(new AccessDeniedException("Can't update message by permission denied, accountId = '%s'".formatted(accountId)));
                });
    }

    private Mono<ChatMessage> validateCanUpdate(ChatMessage chatMessage) {
        if (chatMessage.isDelete()) {
            return Mono.error(new IllegalStateException("Message is already deleted, cannot update."));
        }
        return Mono.just(chatMessage);
    }

    @Data
    @AllArgsConstructor
    public static class Input {
        private String messageId;
        private String newContent;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        private ChatMessage newChatMessage;
    }
}
