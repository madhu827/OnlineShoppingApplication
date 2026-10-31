package com.onlineshopping.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Request body for product create ({@code POST}) and full update ({@code PUT}).
 */
@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Name cannot exceed 200 characters")
    private String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price format invalid")
    private BigDecimal price;

    @NotBlank(message = "Category is required")
    private String category;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private int stockQuantity;

    private String brand;
    private String imageUrl;
    private String sku;

    private boolean active = true;
}

