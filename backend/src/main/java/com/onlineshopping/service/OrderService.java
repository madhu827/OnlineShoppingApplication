package com.onlineshopping.service;

import com.onlineshopping.dto.request.OrderCreateRequest;
import com.onlineshopping.dto.response.OrderResponse;

import java.util.List;

/**
 * Service contract for customer orders and order history.
 */
public interface OrderService {

    OrderResponse createOrder(String username, OrderCreateRequest request);

    List<OrderResponse> getOrdersByUsername(String username);

    OrderResponse getOrderById(String orderId, String username);

    List<OrderResponse> getAllOrders();
}

