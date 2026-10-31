package com.onlineshopping.controller;

import com.onlineshopping.dto.request.ProductRequest;
import com.onlineshopping.dto.response.ProductResponse;
import com.onlineshopping.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for product CRUD operations.
 *
 * <p>Read operations (GET) are public.
 * Write operations (POST / PUT / DELETE) require the {@code ROLE_ADMIN} role.
 *
 * <p>All data is stored in and retrieved from Apache Cassandra.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog CRUD — backed by Apache Cassandra")
public class ProductController {

    private final ProductService productService;

    // ── CREATE ─────────────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new product (ADMIN only)")
    @ApiResponse(responseCode = "201", description = "Product created")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProduct(request));
    }

    // ── READ ───────────────────────────────────────────────────────────────

    @GetMapping("/active")
    @Operation(summary = "List all active products (public)")
    public ResponseEntity<List<ProductResponse>> getAllActive() {
        return ResponseEntity.ok(productService.getAllActiveProducts());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "List products by category (public)")
    public ResponseEntity<List<ProductResponse>> getByCategory(
            @Parameter(description = "Product category name", example = "electronics")
            @PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @GetMapping("/category/{category}/brand/{brand}")
    @Operation(summary = "List products by category and brand (public)")
    public ResponseEntity<List<ProductResponse>> getByCategoryAndBrand(
            @PathVariable String category,
            @PathVariable String brand) {
        return ResponseEntity.ok(
                productService.getProductsByCategoryAndBrand(category, brand));
    }

    @GetMapping("/category/{category}/{id}")
    @Operation(summary = "Get a single product by category + ID (public)")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductResponse> getOne(
            @PathVariable String category,
            @Parameter(description = "Product UUID")
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                productService.getProductByCategoryAndId(category, id));
    }

    // ── UPDATE ─────────────────────────────────────────────────────────────

    @PutMapping("/category/{category}/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a product (ADMIN only)")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductResponse> update(
            @PathVariable String category,
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(
                productService.updateProduct(category, id, request));
    }

    // ── DELETE ─────────────────────────────────────────────────────────────

    @DeleteMapping("/category/{category}/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a product (ADMIN only)")
    @ApiResponse(responseCode = "204", description = "Product deleted")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<Void> delete(
            @PathVariable String category,
            @PathVariable UUID id) {
        productService.deleteProduct(category, id);
        return ResponseEntity.noContent().build();
    }
}

