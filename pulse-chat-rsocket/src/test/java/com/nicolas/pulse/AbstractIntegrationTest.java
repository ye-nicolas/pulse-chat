package com.nicolas.pulse;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.account.AccountData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.testcontainers.containers.PostgreSQLContainer.POSTGRESQL_PORT;


@SpringBootTest
@AutoConfigureWebTestClient
public abstract class AbstractIntegrationTest {
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.10-alpine");

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
    @Autowired
    protected DatabaseClient databaseClient;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z");
    private static final String creator = "01KDFWP8CSRTWJ04A6WS7CDNWM";
    protected static final AccountData ACCOUNT_DATA_1 = AccountData.builder()
            .id("01KDFX186KHJ0EC3TBED58Z5S4")
            .name("root1")
            .showName("TEst1")
            .password("$2a$10$yjj8bwj1RPn146if3K9SeerD8o/NReFTBokcpWVRbYNzkYZDr7GOm")
            .isActive(true)
            .createdBy(creator)
            .updatedBy(creator)
            .createdAt(OffsetDateTime.parse("2025-12-27 20:37:46.961 +0800", formatter).toInstant())
            .updatedAt(OffsetDateTime.parse("2025-12-27 20:37:46.961 +0800", formatter).toInstant())
            .build();
    protected static AccountData ACCOUNT_DATA_2 = AccountData.builder()
            .id("01KDFX1JMD3FTC7E5NCP1AEV41")
            .name("root2")
            .showName("TEst2")
            .password("$2a$10$9yErubhpEXx5xwDM8qEAcOks0phFInZ/f7JdSgN2AJM4soar69lCW")
            .isActive(true)
            .createdBy(creator)
            .updatedBy(creator)
            .createdAt(OffsetDateTime.parse("2025-12-27 20:37:57.607 +0800", formatter).toInstant())
            .updatedAt(OffsetDateTime.parse("2025-12-27 20:37:57.607 +0800", formatter).toInstant())
            .build();
    protected static AccountData ACCOUNT_DATA_3 = AccountData.builder()
            .id("01KDFX1SB2RRW9R0KC9YQPMS73")
            .name("root3")
            .showName("TEst3")
            .password("$2a$10$w/.QFRqxn1oYpM3OjhaB7u0F.MQ.RNRVqzfkJDHDAUUT1hpPls8Dq")
            .isActive(true)
            .createdBy(creator)
            .updatedBy(creator)
            .createdAt(OffsetDateTime.parse("2025-12-27 20:38:04.481 +0800", formatter).toInstant())
            .updatedAt(OffsetDateTime.parse("2025-12-27 20:38:04.481 +0800", formatter).toInstant())
            .build();
    private static final String ACCOUNT_SQL = """
            INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s,%s,%s)
            VALUES %s;
            """.formatted(DbMeta.AccountData.TABLE_NAME,
            DbMeta.AccountData.COL_ID, DbMeta.AccountData.COL_NAME, DbMeta.AccountData.COL_SHOW_NAME, DbMeta.AccountData.COL_PASSWORD, DbMeta.AccountData.COL_IS_ACTIVE,
            DbMeta.AccountData.COL_CREATED_BY, DbMeta.AccountData.COL_UPDATED_BY, DbMeta.AccountData.COL_CREATED_AT, DbMeta.AccountData.COL_UPDATED_AT,
            Stream.of(ACCOUNT_DATA_1, ACCOUNT_DATA_2, ACCOUNT_DATA_3)
                    .map(accountData -> "('%s','%s','%s','%s',%s,'%s','%s','%s','%s')".formatted(
                            accountData.getId(), accountData.getName(), accountData.getShowName(), accountData.getPassword(), accountData.isActive(),
                            accountData.getCreatedBy(), accountData.getUpdatedBy(), accountData.getCreatedAt(), accountData.getUpdatedAt()
                    )).collect(Collectors.joining(",\n")));

    @BeforeEach
    void setUp() {
        databaseClient.sql(ACCOUNT_SQL)
                .fetch()
                .rowsUpdated()
                .block();
    }

    @AfterEach
    void cleanup() {
        String collect = Stream.of(DbMeta.AccountData.TABLE_NAME,
                        DbMeta.ChatRoomData.TABLE_NAME,
                        DbMeta.ChatRoomMemberData.TABLE_NAME,
                        DbMeta.ChatMessageData.TABLE_NAME,
                        DbMeta.ChatMessageLastReadData.TABLE_NAME,
                        DbMeta.FriendShipData.TABLE_NAME)
                .map("TRUNCATE TABLE \"%s\";"::formatted)
                .collect(Collectors.joining("\n"));
        databaseClient.sql(collect)
                .fetch()
                .rowsUpdated()
                .block();
    }
}
