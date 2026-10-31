package com.onlineshopping.entity.cassandra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import com.datastax.oss.driver.api.core.type.DataTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Cassandra table entity representing a product in the catalog.
 *
 * <p>Table: {@code products} inside the {@code online_shopping} keyspace.
 *
 * <p>Primary key design:
 * <ul>
 *   <li>{@code category} — PARTITION KEY: queries are typically filtered by category,
 *       which distributes data evenly across Cassandra nodes.</li>
 *   <li>{@code id} — CLUSTERING KEY (DESC): products within a category are
 *       returned newest-first.</li>
 * </ul>
 *
 * <p>This entity is intentionally separated from any MongoDB entity.
 * Never use a MongoDB repository to access this table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class Product {

    /**
     * Partition key — product category (e.g., "Electronics", "Clothing").
     * All products in the same category land on the same Cassandra partition.
     */
    @PrimaryKeyColumn(name = "category", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String category;

    /**
     * Clustering key — unique product identifier (UUID).
     * Sorted descending so the newest products appear first in range queries.
     */
    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID id;

    /** Human-readable product name. */
    @Column("name")
    private String name;

    /** Detailed product description. */
    @Column("description")
    private String description;

    /** Sale price with full decimal precision. */
    @Column("price")
    @CassandraType(type = CassandraType.Name.DECIMAL)
    private BigDecimal price;

    /** Number of units currently in stock. */
    @Column("stock_quantity")
    private int stockQuantity;

    /** Brand or manufacturer name. */
    @Column("brand")
    private String brand;

    /** URL or path to the primary product image. */
    @Column("image_url")
    private String imageUrl;

    /** SKU / barcode for inventory management. */
    @Column("sku")
    private String sku;

    /** Whether the product is visible and purchasable. */
    @Column("active")
    private boolean active;

    /** Timestamp when the product was first added. */
    @Column("created_at")
    private Instant createdAt;

    /** Timestamp of the last update to this record. */
    @Column("updated_at")
    private Instant updatedAt;
}

