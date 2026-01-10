package com.bank.star;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс Spring Boot приложения BankStar.
 * <p>
 * Используется как точка входа для запуска приложения.
 * </p>
 */
@SpringBootApplication
public class BankStarApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankStarApplication.class, args);
    }

}
