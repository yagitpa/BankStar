package com.bank.star.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Конфигурация кеширования с использованием Caffeine
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                       .maximumSize(1000)
                       .expireAfterWrite(10, TimeUnit.MINUTES)
                       .recordStats();
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);

        cacheManager.setCacheNames(java.util.List.of(
                "userOfCache",
                "activeUserOfCache",
                "transactionSumCompareCache",
                "transactionSumCompareDepositWithdrawCache"
        ));

        return cacheManager;
    }
}
