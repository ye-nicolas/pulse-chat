package com.nicolas.pulse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
public class ClientTest {
    @Autowired
    private Mono<RSocketRequester> requesterMono;

    @Test
    public void send() {
        requesterMono.map(r -> r.route("chat.room.{roomId}.{accountId}", "VV","BB")
                        .data(Flux.just("AA", "BB")))
                .flatMapMany(r -> r.retrieveFlux(String.class)) // 執行呼叫，向遠端發出請求
                .doOnNext(res -> System.out.println("RES: " + res)) // 處理返回數據
                .blockLast();
    }
}

