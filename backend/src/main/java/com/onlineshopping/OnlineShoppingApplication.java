package com.onlineshopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import com.onlineshopping.security.JwtProperties;

/**
 * Main entry point for the Online Shopping Spring Boot application.
 *
 * <p>This application uses two separate databases:
 * <ul>
 *   <li>MongoDB  – for users, authentication, and profiles</li>
 *   <li>Apache Cassandra – for products and catalog data</li>
 * </ul>
 *
 * <p>{@code @EnableMongoRepositories} restricts Spring Data MongoDB to only
 * the mongo repository package — preventing it from scanning Cassandra repos.
 *
 * <p>{@code @EnableCassandraRepositories} is declared in {@link com.onlineshopping.config.CassandraConfig}
 * alongside the Cassandra session configuration to keep all Cassandra wiring together.
 *
 * <p>{@code @EnableConfigurationProperties} activates type-safe binding for
 * {@link JwtProperties} (reads {@code app.jwt.*} from application.properties).
 */
@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
@EnableMongoRepositories(basePackages = "com.onlineshopping.repository.mongo")
public class OnlineShoppingApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineShoppingApplication.class, args);
    }
}
