package com.onlineshopping.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB document representing an order placed by a customer.
 *
 * <p>Stored in the {@code orders} collection inside the {@code online_shopping} database.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    @Indexed(unique = true)
    private String orderNumber;

    @Indexed
    private String userId;

    @Indexed
    private String username;

    private String userEmail;

    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    private BigDecimal totalAmount;

    private String paymentMethod; // CARD, UPI, NETBANKING, COD

    private String transactionId;

    @Builder.Default
    private String status = "CONFIRMED"; // CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED

    private String shippingAddress;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

