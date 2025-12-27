package com.nicolas.pulse.service.usecase.chat.message;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.ChatMessageRepository;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
                .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Message not found, message id = '%s'.".formatted(input.getMessageId()))))
                .delayUntil(chatMessage -> Mono.when(
                        this.validateIsCreator(chatMessage),
                        this.validateCanUpdate(chatMessage)))
                .flatMap(chatMessage -> updateMessage(chatMessage, input.getNewContent()))
                .doOnNext(output::setNewChatMessage)
                .then();
    }

    private Mono<ChatMessage> updateMessage(ChatMessage chatMessage, String newContent) {
        chatMessage.setContent(newContent);
        return chatMessageRepository.save(chatMessage);
    }

    private Mono<Void> validateIsCreator(ChatMessage chatMessage) {
        return SecurityUtil.getCurrentAccountId()
                .flatMap(accountId -> accountId.equals(chatMessage.getCreatedBy())
                        ? Mono.empty()
                        : Mono.error(() -> new AccessDeniedException("Can't update message by permission denied, account id = '%s'.".formatted(accountId))));
    }

    private Mono<Void> validateCanUpdate(ChatMessage chatMessage) {
        return chatMessage.isDelete() ? Mono.error(() -> new IllegalStateException("Message is already deleted, cannot update.")) : Mono.empty();
    }

    @Data
    @Builder
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
