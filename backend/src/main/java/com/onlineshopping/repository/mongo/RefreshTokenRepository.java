package com.onlineshopping.repository.mongo;

import com.onlineshopping.entity.mongo.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data MongoDB repository for {@link RefreshToken} documents.
 */
@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    /** Remove all refresh tokens belonging to a specific user (used on logout). */
    void deleteByUserId(String userId);
}

