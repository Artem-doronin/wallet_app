package com.example.wallet_app.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "retry")
@Getter
@Setter
public class RetryProperties {
    private int maxAttempts = 3;
    private long delay = 1000;
}
