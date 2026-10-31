package com.onlineshopping.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Embedded document representing a single purchased product line item within an Order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private String productId;
    private String category;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subtotal;
    private String imageUrl;
}

