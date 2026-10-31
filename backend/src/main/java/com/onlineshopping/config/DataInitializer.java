package com.onlineshopping.config;

import com.onlineshopping.entity.cassandra.Product;
import com.onlineshopping.entity.mongo.User;
import com.onlineshopping.repository.cassandra.ProductRepository;
import com.onlineshopping.repository.mongo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Runs once on application startup to seed initial data when the databases
 * are empty or need updated product images.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository    userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder   passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedUsers();
        seedProducts();
    }

    // ── MongoDB Users ──────────────────────────────────────────────────────

    private void seedUsers() {
        // Admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@onlineshopping.com")
                    .password(passwordEncoder.encode("Admin@1234"))
                    .firstName("Super")
                    .lastName("Admin")
                    .phone("+91 9000000001")
                    .address("Admin HQ, Tech Park, Hyderabad")
                    .roles(Set.of("ROLE_ADMIN", "ROLE_USER"))
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info("✅ Admin user seeded  → username: admin / password: Admin@1234");
        }

        // Regular test user
        if (!userRepository.existsByUsername("testuser")) {
            User user = User.builder()
                    .username("testuser")
                    .email("testuser@onlineshopping.com")
                    .password(passwordEncoder.encode("Test@1234"))
                    .firstName("Test")
                    .lastName("User")
                    .phone("+91 9000000002")
                    .address("123 Main Street, Hyderabad")
                    .roles(Set.of("ROLE_USER"))
                    .enabled(true)
                    .build();
            userRepository.save(user);
            log.info("✅ Test user seeded   → username: testuser / password: Test@1234");
        }
    }

    // ── Cassandra Products ─────────────────────────────────────────────────

    private void seedProducts() {
        List<Product> existing = productRepository.findAllActiveProducts();
        boolean hasPlaceholders = existing.stream()
                .anyMatch(p -> p.getImageUrl() == null || p.getImageUrl().contains("placehold.co"));

        // Only seed if empty or replacing old placeholder URLs
        if (!existing.isEmpty() && !hasPlaceholders) {
            log.info("ℹ️  Products already have high-resolution images — skipping Cassandra seed.");
            return;
        }

        if (hasPlaceholders) {
            log.info("🔄 Refreshing Cassandra product catalog with real high-resolution images...");
            productRepository.deleteAll();
        }

        List<Product> products = List.of(

            // ── Electronics ────────────────────────────────────────────────
            product("electronics", "Samsung Galaxy S25 Ultra",
                    "6.9\" Dynamic AMOLED 2X, 200MP camera, Snapdragon 8 Elite, 5000mAh all-day battery.",
                    new BigDecimal("129999.00"), 85, "Samsung",
                    "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?auto=format&fit=crop&w=800&q=80"),

            product("electronics", "Apple MacBook Air M3",
                    "15.3\" Liquid Retina display, Apple M3 chip, 18-hour battery life, 8-core CPU, Space Gray.",
                    new BigDecimal("134900.00"), 45, "Apple",
                    "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=800&q=80"),

            product("electronics", "Sony WH-1000XM5 Wireless Headphones",
                    "Industry-leading noise cancellation, 30-hour battery, crystal-clear hands-free calling.",
                    new BigDecimal("29990.00"), 120, "Sony",
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=800&q=80"),

            product("electronics", "LG 4K OLED Smart TV 55\"",
                    "OLED evo 4K panel, α9 AI Processor Gen7, Dolby Vision IQ & Atmos, webOS 24.",
                    new BigDecimal("149999.00"), 18, "LG",
                    "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?auto=format&fit=crop&w=800&q=80"),

            // ── Clothing ────────────────────────────────────────────────────
            product("clothing", "Levi's 511 Slim Fit Jeans",
                    "Modern slim-fit jeans with room to move. Premium stretch denim with classic 5-pocket styling.",
                    new BigDecimal("2999.00"), 200, "Levi's",
                    "https://images.unsplash.com/photo-1542272604-780c96856423?auto=format&fit=crop&w=800&q=80"),

            product("clothing", "Nike Dri-FIT Athletic T-Shirt",
                    "Soft breathable fabric with sweat-wicking technology to keep you dry and comfortable.",
                    new BigDecimal("1499.00"), 350, "Nike",
                    "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=800&q=80"),

            product("clothing", "Zara Tailored Oversized Blazer",
                    "Structured oversized silhouette, lapel collar, front flap pockets, single-button closure.",
                    new BigDecimal("4999.00"), 60, "Zara",
                    "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?auto=format&fit=crop&w=800&q=80"),

            product("clothing", "Adidas Ultraboost Light Running Shoes",
                    "Epic energy in every stride. Lightest BOOST foam yet with Continental rubber grip.",
                    new BigDecimal("14999.00"), 75, "Adidas",
                    "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?auto=format&fit=crop&w=800&q=80"),

            // ── Books ───────────────────────────────────────────────────────
            product("books", "Clean Code by Robert C. Martin",
                    "A Handbook of Agile Software Craftsmanship. Principles, patterns, and practices for writing clean code.",
                    new BigDecimal("649.00"), 500, "Pearson",
                    "https://images.unsplash.com/photo-1532012164546-f432f2e3edd3?auto=format&fit=crop&w=800&q=80"),

            product("books", "System Design Interview – Volume 2",
                    "Comprehensive insider guide with step-by-step solutions to real-world large-scale system design.",
                    new BigDecimal("1299.00"), 320, "ByteByteGo",
                    "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=800&q=80"),

            product("books", "Spring Boot in Action",
                    "A fast-paced guide to building production-ready Spring Boot microservices and REST APIs.",
                    new BigDecimal("799.00"), 180, "Manning",
                    "https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=800&q=80"),

            product("books", "Designing Data-Intensive Applications",
                    "The definitive guide to architecture, reliability, scalability, and maintainability for modern data systems.",
                    new BigDecimal("1499.00"), 240, "O'Reilly",
                    "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=800&q=80"),

            // ── Sports ──────────────────────────────────────────────────────
            product("sports", "Yonex Astrox 99 Pro Badminton Racket",
                    "Rotational Generator System, power-assist bumper, stiff flex for devastating smashes.",
                    new BigDecimal("8999.00"), 40, "Yonex",
                    "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?auto=format&fit=crop&w=800&q=80"),

            product("sports", "Nivia Storm Match Football",
                    "FIFA Pro standard, 32-panel multi-layer composite construction, high air-retention bladder.",
                    new BigDecimal("1299.00"), 90, "Nivia",
                    "https://images.unsplash.com/photo-1579952363873-27f3bade9f55?auto=format&fit=crop&w=800&q=80"),

            product("sports", "Decathlon High-Density Yoga Mat 6mm",
                    "Eco-friendly non-slip grip, dual textured surface, high-density cushioning with carry strap.",
                    new BigDecimal("799.00"), 150, "Decathlon",
                    "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?auto=format&fit=crop&w=800&q=80"),

            product("sports", "Boldfit Heavy-Duty Resistance Bands Set",
                    "Set of 5 color-coded resistance tubes (10–50 lbs) with cushioned handles and door anchor.",
                    new BigDecimal("599.00"), 220, "Boldfit",
                    "https://images.unsplash.com/photo-1598289431512-b97b0917affc?auto=format&fit=crop&w=800&q=80"),

            // ── Home & Garden ───────────────────────────────────────────────
            product("home-garden", "Philips Smart Air Purifier 3000i",
                    "NanoProtect HEPA filter removes 99.97% of ultra-fine particles, real-time air quality display.",
                    new BigDecimal("24999.00"), 30, "Philips",
                    "https://images.unsplash.com/photo-1585771724684-38269d6639fd?auto=format&fit=crop&w=800&q=80"),

            product("home-garden", "InstantPot Duo 7-in-1 Multi-Cooker",
                    "Pressure cooker, slow cooker, rice cooker, steamer, sauté pan, yogurt maker and food warmer.",
                    new BigDecimal("8999.00"), 55, "Instant Brands",
                    "https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?auto=format&fit=crop&w=800&q=80"),

            product("home-garden", "Dyson V15 Detect Cordless Vacuum",
                    "Laser reveals microscopic dust, Piezo sensor counts particles, up to 60 minutes fade-free suction.",
                    new BigDecimal("54900.00"), 22, "Dyson",
                    "https://images.unsplash.com/photo-1558317374-067fb5f30001?auto=format&fit=crop&w=800&q=80"),

            product("home-garden", "Godrej Aer Smart Automatic Air Diffuser",
                    "Bluetooth smartphone control, schedule fragrance bursts, long-lasting 2200 sprays per refill.",
                    new BigDecimal("799.00"), 400, "Godrej",
                    "https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?auto=format&fit=crop&w=800&q=80")
        );

        productRepository.saveAll(products);
        log.info("✅ {} products updated with real high-resolution images in Cassandra.", products.size());
    }

    // ── Builder helper ─────────────────────────────────────────────────────

    private Product product(String category, String name, String description,
                            BigDecimal price, int stock, String brand, String imageUrl) {
        Instant now = Instant.now();
        return Product.builder()
                .id(UUID.randomUUID())
                .category(category)
                .name(name)
                .description(description)
                .price(price)
                .stockQuantity(stock)
                .brand(brand)
                .imageUrl(imageUrl)
                .sku("SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
