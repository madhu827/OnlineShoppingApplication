package com.onlineshopping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Line item in an order response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private String productId;
    private String category;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subtotal;
    private String imageUrl;
}

