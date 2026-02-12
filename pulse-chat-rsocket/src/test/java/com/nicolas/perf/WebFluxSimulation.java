package com.nicolas.perf;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class WebFluxSimulation extends Simulation {
    private static final String url = "jdbc:postgresql://localhost:%d/pulse_chat?currentSchema=pulse_chat&reWriteBatchedInserts=true".formatted(5434);
    private static final String user = "nicolas";
    private static final String pwd = "123456789";

    @Override
    public void before() {
        PrepareTestDataUseCase testDataUseCase = new PrepareTestDataUseCase();
        PrepareTestDataUseCase.Input build = PrepareTestDataUseCase.Input.builder()
                .accountSize(50_000)
                .roomSize(10_000)
                .url(url)
                .user(user)
                .pwd(pwd)
                .build();
        PrepareTestDataUseCase.Output output = new PrepareTestDataUseCase.Output();
        testDataUseCase.execute(build, output);
        output.getMemberVo().forEach(System.out::println);
    }

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // 2. 定義劇本 (Scenario)
    ScenarioBuilder scn = scenario("Spring Boot 3.5 High Concurrency Test")
            .exec(http("My_First_Request")
                    .get("/pulse-chat/accounts")
                    .check(status().is(200)))
            .pause(1); // 每個虛擬用戶請求完後停 1 秒，模擬真實人類行為

    {
        setUp(scn.injectOpen(rampUsers(10).during(10)))
                .protocols(httpProtocol);
    }
}
