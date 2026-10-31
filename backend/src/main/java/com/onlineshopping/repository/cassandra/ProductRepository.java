package com.onlineshopping.repository.cassandra;

import com.onlineshopping.entity.cassandra.Product;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data Cassandra repository for {@link Product} table.
 *
 * <p>All methods here query the {@code products} table in Apache Cassandra.
 * Never inject this repository in a MongoDB-related service.
 *
 * <p>Because {@code (category, id)} is the compound primary key, Cassandra
 * requires the partition key ({@code category}) to be specified in most
 * efficient queries.  Full-table scans ({@code ALLOW FILTERING}) are provided
 * for convenience but should be avoided in high-traffic production paths.
 */
@Repository
public interface ProductRepository extends CassandraRepository<Product, UUID> {

    /**
     * Returns all products in a given category — this is the most efficient
     * query because it targets exactly one partition.
     */
    List<Product> findByCategory(String category);

    /**
     * Finds a specific product by its composite primary key (category + id).
     */
    Optional<Product> findByCategoryAndId(String category, UUID id);

    /**
     * Deletes a specific product by its composite primary key.
     */
    void deleteByCategoryAndId(String category, UUID id);

    /**
     * Returns only active products within a category.
     * Uses ALLOW FILTERING — acceptable for small partitions.
     */
    @Query("SELECT * FROM products WHERE category = ?0 AND active = true ALLOW FILTERING")
    List<Product> findByCategoryAndActiveTrue(String category);

    /**
     * Full-table scan for active products — use sparingly and only with
     * appropriate Cassandra guards (e.g., pagination).
     */
    @Query("SELECT * FROM products WHERE active = true ALLOW FILTERING")
    List<Product> findAllActiveProducts();

    /**
     * Search products by brand within a category.
     */
    @Query("SELECT * FROM products WHERE category = ?0 AND brand = ?1 ALLOW FILTERING")
    List<Product> findByCategoryAndBrand(String category, String brand);
}

