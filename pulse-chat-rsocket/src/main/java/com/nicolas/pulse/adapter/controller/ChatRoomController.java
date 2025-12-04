package com.nicolas.pulse.adapter.controller;

import com.nicolas.pulse.adapter.dto.req.AddChatRoomMemberReq;
import com.nicolas.pulse.adapter.dto.req.CreateChatRoomReq;
import com.nicolas.pulse.adapter.dto.req.RemoveChatRoomMemberReq;
import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import com.nicolas.pulse.service.usecase.chat.AddChatRoomMemberUseCase;
import com.nicolas.pulse.service.usecase.chat.CreateChatRoomUseCase;
import com.nicolas.pulse.service.usecase.chat.RemoveChatRoomMemberByRoomUsecase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;

@RestController
@RequestMapping(ChatRoomController.CHAT_ROOM_BASE_URL)
public class ChatRoomController {
    public static final String CHAT_ROOM_BASE_URL = "/chat-room";
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final CreateChatRoomUseCase createChatRoomUseCase;
    private final AddChatRoomMemberUseCase addChatRoomMemberUseCase;
    private final RemoveChatRoomMemberByRoomUsecase removeChatRoomMemberByRoomUsecase;

    public ChatRoomController(ChatRoomRepository chatRoomRepository,
                              ChatRoomMemberRepository chatRoomMemberRepository,
                              CreateChatRoomUseCase createChatRoomUseCase,
                              AddChatRoomMemberUseCase addChatRoomMemberUseCase,
                              RemoveChatRoomMemberByRoomUsecase removeChatRoomMemberByRoomUsecase) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.createChatRoomUseCase = createChatRoomUseCase;
        this.addChatRoomMemberUseCase = addChatRoomMemberUseCase;
        this.removeChatRoomMemberByRoomUsecase = removeChatRoomMemberByRoomUsecase;
    }

    @GetMapping("/")
    public ResponseEntity<Flux<ChatRoom>> findAll(@RequestParam("accountId") String accountId) {
        if (StringUtils.hasText(accountId)) {
            return ResponseEntity.ok(chatRoomMemberRepository.findAllByAccountId(accountId).map(ChatRoomMember::getChatRoom));
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
                        .accountIdSet(req.getAccountIdSet())
                        .roomName(req.getRoomName())
                        .build())
                .flatMap(input -> createChatRoomUseCase.execute(input, output))
                .then(Mono.defer(() -> Mono.just(ResponseEntity.ok(output.getRoomId()))));
    }

    @PostMapping("/{roomId}")
    public Mono<ResponseEntity<Void>> addRoomMember(@PathVariable("roomId") String roomId,
                                                    @Valid @RequestBody Mono<AddChatRoomMemberReq> roomMemberReqMono) {
        return roomMemberReqMono.map(req -> AddChatRoomMemberUseCase.Input.builder()
                        .roomId(roomId)
                        .accountIdSet(new HashSet<>(req.getAccountIdList()))
                        .build())
                .flatMap(addChatRoomMemberUseCase::execute)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{roomId}")
    public Mono<ResponseEntity<Void>> removeRoomMember(@PathVariable("roomId") String roomId,
                                                       @Valid @RequestBody Mono<RemoveChatRoomMemberReq> roomMemberReqMono) {
        return roomMemberReqMono.map(req -> RemoveChatRoomMemberByRoomUsecase.Input.builder()
                        .roomId(roomId)
                        .deleteMemberIdList(new HashSet<>(req.getMemberId()))
                        .build())
                .flatMap(removeChatRoomMemberByRoomUsecase::execute)
                .map(ResponseEntity::ok);
    }
}