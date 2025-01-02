package org.thivernale.booknetwork.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.thivernale.booknetwork.book.BookResponse;

@Service
@RequiredArgsConstructor
//@EnableScheduling
public class CacheInit {

    private final CacheManager cacheManager;

    @Bean
    public CommandLineRunner initCaching() {
        return args -> {
            evictAllCaches();
        };
    }

    public void evictAllCaches() {
        /*
        System.out.println(cacheManager.getCacheNames());
        cacheManager.getCacheNames()
            .forEach(name -> cacheManager.getCache(name)
                .clear());*/
        if (cacheManager.getCache("shareable_books") != null) {
            cacheManager.getCache("shareable_books")
                .clear();
        }
        Cache cache = cacheManager.getCache("book_response");
        assert cache != null;
        System.out.println(cache.get(7L, BookResponse.class));

    }

    //@Scheduled(fixedRate = 1000 * 6)
    public void evictAllCachesAtIntervals() {
        evictAllCaches();
    }
}
