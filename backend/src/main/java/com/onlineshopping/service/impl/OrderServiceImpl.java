package com.onlineshopping.service.impl;

import com.onlineshopping.dto.request.OrderCreateRequest;
import com.onlineshopping.dto.request.OrderItemRequest;
import com.onlineshopping.dto.response.OrderItemResponse;
import com.onlineshopping.dto.response.OrderResponse;
import com.onlineshopping.entity.mongo.Order;
import com.onlineshopping.entity.mongo.OrderItem;
import com.onlineshopping.entity.mongo.User;
import com.onlineshopping.exception.ResourceNotFoundException;
import com.onlineshopping.repository.mongo.OrderRepository;
import com.onlineshopping.repository.mongo.UserRepository;
import com.onlineshopping.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing customer orders and history.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(String username, OrderCreateRequest request) {
        log.info("Creating order for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        String orderNumber = request.getOrderNumber();
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            orderNumber = "ORD-" + (System.currentTimeMillis() % 1000000);
        }

        String transactionId = request.getTransactionId();
        if (transactionId == null || transactionId.trim().isEmpty()) {
            transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        String shippingAddress = request.getShippingAddress();
        if ((shippingAddress == null || shippingAddress.trim().isEmpty()) && user.getAddress() != null) {
            shippingAddress = user.getAddress();
        }
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            shippingAddress = "Standard Delivery Address";
        }

        List<OrderItem> items = new ArrayList<>();
        if (request.getItems() != null) {
            for (OrderItemRequest ir : request.getItems()) {
                BigDecimal subtotal = ir.getPrice().multiply(BigDecimal.valueOf(ir.getQuantity()));
                items.add(OrderItem.builder()
                        .productId(ir.getProductId())
                        .category(ir.getCategory())
                        .productName(ir.getProductName())
                        .price(ir.getPrice())
                        .quantity(ir.getQuantity())
                        .subtotal(subtotal)
                        .imageUrl(ir.getImageUrl())
                        .build());
            }
        }

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(user.getId())
                .username(user.getUsername())
                .userEmail(user.getEmail())
                .items(items)
                .totalAmount(request.getTotalAmount())
                .paymentMethod(request.getPaymentMethod().toUpperCase())
                .transactionId(transactionId)
                .status("CONFIRMED")
                .shippingAddress(shippingAddress)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Successfully created order #{} for user {}", savedOrder.getOrderNumber(), username);

        return mapToResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getOrdersByUsername(String username) {
        log.debug("Fetching order history for username: {}", username);
        return orderRepository.findByUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(String orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUsername().equalsIgnoreCase(username)) {
            throw new SecurityException("Unauthorized access to order: " + orderId);
        }

        return mapToResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        log.debug("Fetching all orders for admin review");
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems() == null ? List.of() :
                order.getItems().stream()
                        .map(item -> OrderItemResponse.builder()
                                .productId(item.getProductId())
                                .category(item.getCategory())
                                .productName(item.getProductName())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .subtotal(item.getSubtotal())
                                .imageUrl(item.getImageUrl())
                                .build())
                        .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .username(order.getUsername())
                .userEmail(order.getUserEmail())
                .items(itemResponses)
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .transactionId(order.getTransactionId())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}

