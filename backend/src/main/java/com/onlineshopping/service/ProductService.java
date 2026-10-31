package com.onlineshopping.service;

import com.onlineshopping.dto.request.ProductRequest;
import com.onlineshopping.dto.response.ProductResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service contract for product CRUD operations backed by Apache Cassandra.
 */
public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductByCategoryAndId(String category, UUID id);

    List<ProductResponse> getProductsByCategory(String category);

    List<ProductResponse> getAllActiveProducts();

    List<ProductResponse> getProductsByCategoryAndBrand(String category, String brand);

    ProductResponse updateProduct(String category, UUID id, ProductRequest request);

    void deleteProduct(String category, UUID id);
}

