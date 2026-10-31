package com.onlineshopping.service.impl;

import com.onlineshopping.dto.request.ProductRequest;
import com.onlineshopping.dto.response.ProductResponse;
import com.onlineshopping.entity.cassandra.Product;
import com.onlineshopping.exception.ResourceNotFoundException;
import com.onlineshopping.repository.cassandra.ProductRepository;
import com.onlineshopping.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ProductService}.
 *
 * <p>All database operations target Apache Cassandra exclusively.
 * MongoDB is never touched in this class.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .id(UUID.randomUUID())
                .category(request.getCategory().toLowerCase().trim())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .brand(request.getBrand())
                .imageUrl(request.getImageUrl())
                .sku(request.getSku())
                .active(request.isActive())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Product saved = productRepository.save(product);
        log.info("Product created: {} in category '{}'", saved.getId(), saved.getCategory());
        return toResponse(saved);
    }

    @Override
    public ProductResponse getProductByCategoryAndId(String category, UUID id) {
        return productRepository
                .findByCategoryAndId(category.toLowerCase().trim(), id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product", "id", id));
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository
                .findByCategory(category.toLowerCase().trim())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getAllActiveProducts() {
        return productRepository
                .findAllActiveProducts()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository
                .findByCategoryAndBrand(category.toLowerCase().trim(), brand)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse updateProduct(String category, UUID id, ProductRequest request) {
        Product existing = productRepository
                .findByCategoryAndId(category.toLowerCase().trim(), id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Cassandra does not support in-place updates across primary key columns.
        // If the category changed we must delete the old row and insert a new one.
        String newCategory = request.getCategory().toLowerCase().trim();
        boolean categoryChanged = !existing.getCategory().equals(newCategory);

        if (categoryChanged) {
            productRepository.deleteByCategoryAndId(existing.getCategory(), id);
        }

        Product updated = Product.builder()
                .id(id)
                .category(newCategory)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .brand(request.getBrand())
                .imageUrl(request.getImageUrl())
                .sku(request.getSku())
                .active(request.isActive())
                .createdAt(existing.getCreatedAt())   // preserve original creation time
                .updatedAt(Instant.now())
                .build();

        Product saved = productRepository.save(updated);
        log.info("Product {} updated", saved.getId());
        return toResponse(saved);
    }

    @Override
    public void deleteProduct(String category, UUID id) {
        if (productRepository.findByCategoryAndId(category.toLowerCase().trim(), id).isEmpty()) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteByCategoryAndId(category.toLowerCase().trim(), id);
        log.info("Product {} deleted from category '{}'", id, category);
    }

    // ── Mapper ─────────────────────────────────────────────────────────────

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .category(p.getCategory())
                .stockQuantity(p.getStockQuantity())
                .brand(p.getBrand())
                .imageUrl(p.getImageUrl())
                .sku(p.getSku())
                .active(p.isActive())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}

