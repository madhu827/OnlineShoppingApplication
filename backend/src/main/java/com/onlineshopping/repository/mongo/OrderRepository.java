package com.onlineshopping.repository.mongo;

import com.onlineshopping.entity.mongo.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for {@link Order} documents.
 */
@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> findByUsernameOrderByCreatedAtDesc(String username);

    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findAllByOrderByCreatedAtDesc();
}

