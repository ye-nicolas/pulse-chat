package com.nicolas.pulse.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Slf4j
@Component
@ConfigurationProperties(prefix = "logging.mdc")
public class MdcProperties {
    private String traceId;
}
