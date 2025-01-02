package org.thivernale.booknetwork.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@EnableCaching
@EnableConfigurationProperties(value = {CacheConfig.RedisConfigurationProperties.class})
public class CacheConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisConfigurationProperties configurationProperties) {
        final RedisStandaloneConfiguration standaloneConfiguration =
            new RedisStandaloneConfiguration(
                configurationProperties.host,
                configurationProperties.port);
        standaloneConfiguration.setDatabase(configurationProperties.database);
        final LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
            //.useSsl()
            .commandTimeout(Duration.of(1, ChronoUnit.SECONDS))
            .build();
        return new LettuceConnectionFactory(standaloneConfiguration, clientConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
        template.setEnableDefaultSerializer(true);
        template.afterPropertiesSet();
        return template;
    }

    //@Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper =
            new Jackson2ObjectMapperBuilder().build(); //new ObjectMapper()

        return objectMapper
//            .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
//                .build(), ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.WRAPPER_OBJECT)
            //.enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION)
            .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            ;
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()
            ));
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(cacheConfiguration())
            .build();
    }

    @ConfigurationProperties(prefix = "spring.data.redis")
    public record RedisConfigurationProperties(String host, int port, int database) {
    }
}
