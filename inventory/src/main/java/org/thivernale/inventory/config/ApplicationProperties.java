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
        /**
         * The keyId header parameter is a hint indicating which key was used to secure a JWS or JWE.
         * This parameter allows originators to explicitly signal a change of key to recipients.
         * The structure of the keyId value is unspecified. Its value MUST be a case-sensitive string.
         */
        private String keyId;
    }
}
