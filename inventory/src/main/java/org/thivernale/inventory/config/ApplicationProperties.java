package org.thivernale.inventory.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Data
public class ApplicationProperties {
    private JwtConfig jwt = new JwtConfig();

    @Data
    public static final class JwtConfig {
        private static final Long DEFAULT_JWT_TOKEN_EXPIRES = 604_800L;

        private Long expiresIn = DEFAULT_JWT_TOKEN_EXPIRES;
        private String secret;
    }
}
