package com.onlineshopping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Response body returned for every product API call. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private int stockQuantity;
    private String brand;
    private String imageUrl;
    private String sku;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}

