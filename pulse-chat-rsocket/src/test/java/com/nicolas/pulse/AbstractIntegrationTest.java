package com.nicolas.pulse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.testcontainers.containers.PostgreSQLContainer.POSTGRESQL_PORT;


@SpringBootTest
@AutoConfigureWebTestClient
public abstract class AbstractIntegrationTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.10-alpine");

    static {
        postgres.withDatabaseName("pulse_chat");
        postgres.withInitScript("data/schema.sql");//不需要添加 classpath:，底層使用 URL的getResource()方法
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("pulse-chat.db.host", postgres::getHost);
        registry.add("pulse-chat.db.port", () -> String.valueOf(postgres.getMappedPort(POSTGRESQL_PORT)));
        registry.add("pulse-chat.db.username", postgres::getUsername);
        registry.add("pulse-chat.db.password", postgres::getPassword);
    }

    @Autowired
    protected WebTestClient webTestClient;
}
