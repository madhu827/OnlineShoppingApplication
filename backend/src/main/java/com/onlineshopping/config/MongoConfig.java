package com.onlineshopping.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * MongoDB configuration.
 *
 * <p>{@code @EnableMongoAuditing} activates the {@code @CreatedDate} and
 * {@code @LastModifiedDate} lifecycle annotations on MongoDB documents
 * (used by {@link com.onlineshopping.entity.mongo.User}).
 *
 * <p>{@link ValidatingMongoEventListener} runs JSR-303 Bean Validation
 * (e.g. {@code @NotNull}, {@code @Email}) before every save, ensuring
 * invalid documents never reach the database.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {

    /**
     * Factory bean that creates a JSR-303 {@code Validator} from the
     * classpath (Hibernate Validator is on the classpath via
     * {@code spring-boot-starter-validation}).
     */
    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }

    /**
     * Hooks the JSR-303 validator into the MongoDB save lifecycle.
     * Any constraint violation throws a {@code ConstraintViolationException}
     * before the document is written to MongoDB.
     */
    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener(
            LocalValidatorFactoryBean validatorFactory) {
        return new ValidatingMongoEventListener(validatorFactory.getValidator());
    }
}
