package org.thivernale.booknetwork.book;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thivernale.booknetwork.config.CacheConfig;
import org.thivernale.booknetwork.file.FileStorageService;
import org.thivernale.booknetwork.history.BookTransactionHistoryRepository;
import org.thivernale.booknetwork.user.User;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import(
    {CacheConfig.class, BookService.class, BookMapper.class, FileStorageService.class}
)
@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration(classes = {CacheAutoConfiguration.class, RedisAutoConfiguration.class})
@TestPropertySource(
    properties = """
        spring.data.redis.host = localhost
        spring.data.redis.port = 6370
        spring.data.redis.database = 0
        """
)
@EnableConfigurationProperties(value = {CacheConfig.RedisConfigurationProperties.class})
class BookServiceCacheTest {
    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private BookTransactionHistoryRepository bookTransactionHistoryRepository;
    @Autowired
    private BookService bookService;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    @Qualifier("caffeineCacheManager")
    private CacheManager caffeineCacheManager;
    @Autowired
    @Qualifier("redisCacheManager")
    private CacheManager redisCacheManager;

    @Test
    public void givenBookIsPresent_whenFindByIdIsCalled_thenReturnBookResponseAndCacheIt() {
        final Long bookId = 100L;
        Book book = Book.builder()
            .title("Title")
            .authorName("Author")
            .isbn("Isbn")
            .synopsis("Synopsis")
            .owner(User.builder()
                .id(1L)
                .firstname("Test")
                .lastname("User")
                .build())
            .id(bookId)
            .build();
        given(bookRepository.findById(bookId))
            .willReturn(Optional.of(book));
        BookResponse bookResponse = bookMapper.toBookResponse(book);

        BookResponse bookResponseCacheMiss = bookService.findById(bookId);

        assertThat(bookResponseCacheMiss)
            .isEqualTo(bookResponse);
        verify(bookRepository, times(1))
            .findById(bookId);
        assertThat(caffeineCacheManager.getCache("book_response")
            .get(bookId)
            .get()).isEqualTo(bookResponse);
        assertThat(redisCacheManager.getCache("book_response")
            .get(bookId)
            .get()).isEqualTo(bookResponse);
    }

    @TestConfiguration
    static class TestRedisConfiguration {
        private final RedisServer redisServer;

        public TestRedisConfiguration(CacheConfig.RedisConfigurationProperties redisConfigurationProperties) throws IOException {
            this.redisServer = new RedisServer(redisConfigurationProperties.port());
        }

        @PostConstruct
        public void postConstruct() throws IOException {
            redisServer.start();
        }

        @PreDestroy
        public void preDestroy() throws IOException {
            redisServer.stop();
        }
    }
}
