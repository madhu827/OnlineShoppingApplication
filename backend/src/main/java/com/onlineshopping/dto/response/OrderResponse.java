package com.onlineshopping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Detailed order response returned to the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private String id;
    private String orderNumber;
    private String userId;
    private String username;
    private String userEmail;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String transactionId;
    private String status;
    private String shippingAddress;
    private Instant createdAt;
    private Instant updatedAt;
}

