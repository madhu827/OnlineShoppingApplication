package com.onlineshopping.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request payload for creating a new customer order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    private String orderNumber;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than zero")
    private BigDecimal totalAmount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String transactionId;

    private String shippingAddress;
}

