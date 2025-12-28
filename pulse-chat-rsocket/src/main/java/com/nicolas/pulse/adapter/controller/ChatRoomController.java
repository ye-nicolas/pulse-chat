package com.nicolas.pulse.adapter.controller;

import com.nicolas.pulse.adapter.dto.req.AddChatRoomMemberReq;
import com.nicolas.pulse.adapter.dto.req.CreateChatRoomReq;
import com.nicolas.pulse.adapter.dto.req.RemoveChatRoomMemberReq;
import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.usecase.chat.member.AddChatRoomMemberUseCase;
import com.nicolas.pulse.service.usecase.chat.member.RemoveChatRoomMemberByRoomUsecase;
import com.nicolas.pulse.service.usecase.chat.room.CreateChatRoomUseCase;
import com.nicolas.pulse.service.usecase.chat.room.DeleteChatRoomUseCase;
import com.nicolas.pulse.service.usecase.chat.room.FindChatRoomByIdUseCase;
import com.nicolas.pulse.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;

@RestController
@RequestMapping(ChatRoomController.CHAT_ROOM_BASE_URL)
public class ChatRoomController {
    public static final String CHAT_ROOM_BASE_URL = "/chat-rooms";
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final FindChatRoomByIdUseCase findChatRoomByIdUseCase;
    private final CreateChatRoomUseCase createChatRoomUseCase;
    private final AddChatRoomMemberUseCase addChatRoomMemberUseCase;
    private final RemoveChatRoomMemberByRoomUsecase removeChatRoomMemberByRoomUsecase;
    private final DeleteChatRoomUseCase deleteChatRoomUseCase;

    public ChatRoomController(ChatRoomMemberRepository chatRoomMemberRepository,
                              FindChatRoomByIdUseCase findChatRoomByIdUseCase,
                              CreateChatRoomUseCase createChatRoomUseCase,
                              AddChatRoomMemberUseCase addChatRoomMemberUseCase,
                              RemoveChatRoomMemberByRoomUsecase removeChatRoomMemberByRoomUsecase,
                              DeleteChatRoomUseCase deleteChatRoomUseCase) {
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.findChatRoomByIdUseCase = findChatRoomByIdUseCase;
        this.createChatRoomUseCase = createChatRoomUseCase;
        this.addChatRoomMemberUseCase = addChatRoomMemberUseCase;
        this.removeChatRoomMemberByRoomUsecase = removeChatRoomMemberByRoomUsecase;
        this.deleteChatRoomUseCase = deleteChatRoomUseCase;
    }

    @GetMapping("/")
    public ResponseEntity<Flux<ChatRoom>> findAll() {
        return ResponseEntity.ok(SecurityUtil.getCurrentAccountId()
                .flatMapMany(chatRoomMemberRepository::findAllByAccountId)
                .map(ChatRoomMember::getChatRoom)
                .distinct());
    }

    @GetMapping("/{roomId}")
    public Mono<ResponseEntity<ChatRoom>> findById(@PathVariable("roomId") String roomId) {
        FindChatRoomByIdUseCase.Output output = new FindChatRoomByIdUseCase.Output();
        return findChatRoomByIdUseCase.execute(new FindChatRoomByIdUseCase.Input(roomId), output)
                .then(Mono.fromSupplier(() -> ResponseEntity.ok(output.getChatRoom())));
    }

    @PostMapping("/")
    public Mono<ResponseEntity<String>> createChatRoom(@Valid @RequestBody Mono<CreateChatRoomReq> reqMono) {
        CreateChatRoomUseCase.Output output = new CreateChatRoomUseCase.Output();
        return reqMono.map(req -> CreateChatRoomUseCase.Input.builder()
                        .accountIdSet(req.getAccountIdSet())
                        .roomName(req.getRoomName())
                        .build())
                .flatMap(input -> createChatRoomUseCase.execute(input, output))
                .then(Mono.fromSupplier(() -> ResponseEntity.ok(output.getRoomId())));
    }

    @PostMapping("/{roomId}/member")
    public Mono<ResponseEntity<Void>> addRoomMember(@PathVariable("roomId") String roomId,
                                                    @Valid @RequestBody Mono<AddChatRoomMemberReq> reqMono) {
        return reqMono.map(req -> AddChatRoomMemberUseCase.Input.builder()
                        .roomId(roomId)
                        .accountIdSet(new HashSet<>(req.getAccountIdList()))
                        .build())
                .flatMap(addChatRoomMemberUseCase::execute)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{roomId}/member")
    public Mono<ResponseEntity<Void>> removeRoomMember(@PathVariable("roomId") String roomId,
                                                       @Valid @RequestBody Mono<RemoveChatRoomMemberReq> reqMono) {
        return reqMono.map(req -> RemoveChatRoomMemberByRoomUsecase.Input.builder()
                        .roomId(roomId)
                        .deleteMemberIdList(new HashSet<>(req.getMemberId()))
                        .build())
                .flatMap(removeChatRoomMemberByRoomUsecase::execute)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{roomId}")
    public Mono<ResponseEntity<Void>> deleteRoom(@PathVariable("roomId") String roomId) {
        return deleteChatRoomUseCase.execute(new DeleteChatRoomUseCase.Input(roomId))
                .map(ResponseEntity::ok);
    }
}