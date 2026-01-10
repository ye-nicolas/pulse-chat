package com.nicolas.pulse.service.usecase.sink;

import com.nicolas.pulse.entity.event.DeleteMemberEvent;
import com.nicolas.pulse.entity.event.DeleteRoomEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class ChatEventBus {
    private final Sinks.Many<DeleteRoomEvent> roomDeleteSink = Sinks.many().multicast().directBestEffort();
    private final Sinks.Many<DeleteMemberEvent> memberDeleteSink = Sinks.many().multicast().directBestEffort();

    public void publishRoomDelete(DeleteRoomEvent event) {
        roomDeleteSink.tryEmitNext(event);
    }

    public void publishMemberDelete(DeleteMemberEvent event) {
        memberDeleteSink.tryEmitNext(event);
    }

    public Flux<DeleteRoomEvent> onRoomDelete() {
        return roomDeleteSink.asFlux();
    }

    public Flux<DeleteMemberEvent> onMemberDelete() {
        return memberDeleteSink.asFlux();
    }
}
