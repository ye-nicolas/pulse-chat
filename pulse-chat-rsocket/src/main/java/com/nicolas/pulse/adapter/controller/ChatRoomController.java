package com.nicolas.pulse.adapter.controller;

import com.nicolas.pulse.adapter.dto.req.CreateChatRoomReq;
import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import com.nicolas.pulse.service.usecase.chat.CreateChatRoomUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ChatRoomController.CHAT_ROOM_BASE_URL)
public class ChatRoomController {
    public static final String CHAT_ROOM_BASE_URL = "/chat-room";
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final CreateChatRoomUseCase createChatRoomUseCase;

    public ChatRoomController(ChatRoomRepository chatRoomRepository,
                              ChatRoomMemberRepository chatRoomMemberRepository,
                              CreateChatRoomUseCase createChatRoomUseCase) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.createChatRoomUseCase = createChatRoomUseCase;
    }

    @GetMapping("/")
    public ResponseEntity<Flux<ChatRoom>> findAll(@RequestParam("accountId") String accountId) {
        if (StringUtils.hasText(accountId)) {
            return ResponseEntity.ok(chatRoomMemberRepository.findByAccountId(accountId).map(ChatRoomMember::getChatRoom));
        }
        return ResponseEntity.ok(chatRoomRepository.findAll());
    }

    @GetMapping("/{roomId}")
    public Mono<ResponseEntity<ChatRoom>> findById(@PathVariable("roomId") String roomId) {
        return chatRoomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(new TargetNotFoundException("Chat room not found, id = '%s'.".formatted(roomId))))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/")
    public Mono<ResponseEntity<String>> createChatRoom(@Valid @RequestBody Mono<CreateChatRoomReq> createChatRoomReq) {
        CreateChatRoomUseCase.Output output = new CreateChatRoomUseCase.Output();
        return createChatRoomReq.map(req -> CreateChatRoomUseCase.Input.builder()
                        .roomType(req.getRoomType())
                        .accountIdSet(req.getAccountIdSet())
                        .roomName(req.getRoomName())
                        .build())
                .flatMap(input -> createChatRoomUseCase.execute(input, output))
                .then(Mono.defer(() -> Mono.just(ResponseEntity.ok(output.getRoomId()))));
    }
}