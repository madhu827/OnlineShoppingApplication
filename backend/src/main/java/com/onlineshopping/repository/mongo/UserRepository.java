package com.onlineshopping.repository.mongo;

import com.onlineshopping.entity.mongo.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data MongoDB repository for {@link User} documents.
 *
 * <p>All methods here operate exclusively on the {@code users} collection
 * in MongoDB.  Never inject this repository in a Cassandra-related service.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /** Find a user by their unique username. */
    Optional<User> findByUsername(String username);

    /** Find a user by their email address. */
    Optional<User> findByEmail(String email);

    /** Check whether a username is already taken. */
    boolean existsByUsername(String username);

    /** Check whether an email address is already registered. */
    boolean existsByEmail(String email);
}

