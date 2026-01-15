package com.nicolas.perf;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class WebFluxSimulation extends Simulation {
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8809") // 指向你本機跑起來的 Spring Boot
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // 2. 定義劇本 (Scenario)
    ScenarioBuilder scn = scenario("Spring Boot 3.5 High Concurrency Test")
            .exec(http("My_First_Request")
                    .get("/pulse-chat/accounts")
                    .check(status().is(200)))
            .pause(1); // 每個虛擬用戶請求完後停 1 秒，模擬真實人類行為

    {
        setUp(scn.injectOpen(rampUsers(100).during(10))) // 策略：10 秒內均勻增加到 1000 個虛擬用戶
                .protocols(httpProtocol);
    }
}
