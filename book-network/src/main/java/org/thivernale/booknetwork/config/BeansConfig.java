package org.thivernale.booknetwork.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
@RequiredArgsConstructor
public class BeansConfig {

    @Value("${application.frontend.url}")
    private List<String> frontendUrl;

    @Bean
    public AuditorAware<String> auditorAware() {
        return new ApplicationAuditorAware();
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(frontendUrl);
        config.setAllowedHeaders(Arrays.asList(
            ORIGIN,
            CONTENT_TYPE,
            ACCEPT,
            AUTHORIZATION
        ));
        config.setAllowedMethods(Arrays.asList(
            GET.name(),
            PUT.name(),
            POST.name(),
            DELETE.name(),
            PATCH.name()
        ));
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
