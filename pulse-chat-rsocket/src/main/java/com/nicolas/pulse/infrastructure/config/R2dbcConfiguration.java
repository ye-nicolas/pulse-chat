package com.nicolas.pulse.infrastructure.config;

import com.nicolas.pulse.entity.domain.SecurityAccount;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

import java.time.Duration;

@EnableR2dbcAuditing
@EnableR2dbcRepositories(basePackages = "com.nicolas.pulse.adapter.repository", entityOperationsRef = "MainR2dbcEntityOperations")
@Configuration
public class R2dbcConfiguration extends AbstractR2dbcConfiguration {
    @Value("${pulse-chat.db.host}")
    private String host;

    @Value("${pulse-chat.db.port}")
    private int port;

    @Value("${pulse-chat.db.username}")
    private String username;

    @Value("${pulse-chat.db.password}")
    private String password;

    @Value("${pulse-chat.db.database}")
    private String database;

    @Value("${pulse-chat.db.schema}")
    private String schema;

    @Value("${pulse-chat.db.connection-timeout}")
    private int connectionTimeout;

    // --- pool settings ---
    @Value("${pulse-chat.db.pool.initial-size}")
    private int initialSize;

    @Value("${pulse-chat.db.pool.max-idle-time}")
    private int maxIdleTime;

    @Value("${pulse-chat.db.pool.max-size}")
    private int maxSize;

    @Value("${pulse-chat.db.pool.max-acquire-time}")
    private int maxAcquireTime;

    @Value("${pulse-chat.db.pool.max-create-connection-time}")
    private int maxCreateConnectionTime;

    @Value("${pulse-chat.db.pool.acquire-retry}")
    private int acquireRetry;

    @Primary
    @Bean("MainConnectionFactory")
    @Override
    public ConnectionFactory connectionFactory() {
        PostgresqlConnectionConfiguration connectionConfiguration = PostgresqlConnectionConfiguration.builder()
                .addHost(host, port)
                .connectTimeout(Duration.ofSeconds(connectionTimeout))
                .database(database)
                .schema(schema)
                .username(username)
                .password(password)
                .build();
        PostgresqlConnectionFactory postgresqlConnectionFactory = new PostgresqlConnectionFactory(connectionConfiguration);
        ConnectionPoolConfiguration poolConfig = ConnectionPoolConfiguration.builder(postgresqlConnectionFactory)
                .initialSize(initialSize)                         // 初始建立 5 條連線
                .maxSize(maxSize)                            // 連線池最大容量 20 條
                .maxIdleTime(Duration.ofSeconds(maxIdleTime))    // 空閒連線超過 30 分鐘自動回收
                .maxAcquireTime(Duration.ofSeconds(maxAcquireTime)) // 取連線最長等候 10 秒，超過丟錯
                .maxCreateConnectionTime(Duration.ofSeconds(maxCreateConnectionTime)) // 新建連線最長等候 5 秒
                .acquireRetry(acquireRetry)                        // 嘗試取連線最多重試 3 次
                .name("pool_" + database + "_" + schema)                  // 池名稱，用於監控或 debug
                .validationQuery("SELECT 1")            // 驗證連線有效性
                .build();
        return new ConnectionPool(poolConfig);
    }

    @Primary
    @Bean("MainR2dbcEntityOperations")
    public R2dbcEntityOperations mainR2dbcEntityOperations(@Qualifier("MainConnectionFactory") ConnectionFactory connectionFactory) {
        DatabaseClient databaseClient = DatabaseClient.create(connectionFactory);
        return new R2dbcEntityTemplate(databaseClient, PostgresDialect.INSTANCE);
    }

    @Bean
    public ReactiveAuditorAware<String> getAuditorProvider() {
        return () -> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .flatMap(authentication -> {
                    if (authentication.getPrincipal() instanceof SecurityAccount securityAccount) {
                        return Mono.just(securityAccount.getId());
                    }
                    return Mono.empty();
                })
                .switchIfEmpty(Mono.just("system"));
    }
}
