package com.ecommerce.config;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.enums.RoleName;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // 1. Ensure ROLE_USER exists
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(RoleName.ROLE_USER)
                                .build()
                ));

        // 2. Ensure ROLE_ADMIN exists
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(RoleName.ROLE_ADMIN)
                                .build()
                ));

        // 3. Check if an admin user already exists (by ROLE_ADMIN or admin@ecommerce.com)
        boolean adminExists = userRepository.existsByRoles_Name(RoleName.ROLE_ADMIN)
                || userRepository.existsByEmail("admin@ecommerce.com");

        if (!adminExists) {
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            User defaultAdmin = User.builder()
                    .username("Admin User")
                    .email("admin@ecommerce.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .roles(roles)
                    .build();

            userRepository.save(defaultAdmin);
        }

        // 4. Initialize default categories
        List<String> defaultCategories = List.of(
                "Electronics",
                "Furniture",
                "Fashion",
                "Home Appliances",
                "Books",
                "Sports",
                "Beauty",
                "Grocery"
        );

        for (String catName : defaultCategories) {
            if (!categoryRepository.existsByNameIgnoreCase(catName)) {
                Category category = new Category();
                category.setName(catName);
                category.setDescription("Default " + catName + " category");
                categoryRepository.save(category);
            }
        }

        // 5. Initialize default products (at least 5 per category, total 40)
        seedDefaultProducts();
    }

    private void seedDefaultProducts() {
        List<ProductSeedData> seedList = List.of(
                // ELECTRONICS (5)
                new ProductSeedData(
                        "Apple iPhone 15",
                        "Dynamic Island, 48MP main camera, USB-C, durable color-infused glass and aluminum design.",
                        79900.0, 25,
                        "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=600&auto=format&fit=crop&q=80",
                        "Electronics"
                ),
                new ProductSeedData(
                        "Samsung Galaxy S24",
                        "Galaxy AI integration, 50MP triple camera system, Snapdragon 8 Gen 3, FHD+ Dynamic AMOLED 2X.",
                        74999.0, 30,
                        "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=600&auto=format&fit=crop&q=80",
                        "Electronics"
                ),
                new ProductSeedData(
                        "ASUS TUF Gaming Laptop",
                        "Intel Core i5 11th Gen, 16GB RAM, 512GB SSD, NVIDIA GeForce RTX 3050, 144Hz FHD display.",
                        62990.0, 15,
                        "https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=600&auto=format&fit=crop&q=80",
                        "Electronics"
                ),
                new ProductSeedData(
                        "Sony WH-1000XM5 Headphones",
                        "Industry-leading noise canceling wireless headphones with Auto NC Optimizer, 30-hour battery life.",
                        29990.0, 40,
                        "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
                        "Electronics"
                ),
                new ProductSeedData(
                        "Samsung 4K Smart TV",
                        "43-inch Crystal 4K Vivid Pro Ultra HD Smart LED TV with HDR10+ and Dolby Digital Plus audio.",
                        42990.0, 18,
                        "https://images.unsplash.com/photo-1593784991095-a205069470b6?w=600&auto=format&fit=crop&q=80",
                        "Electronics"
                ),

                // FURNITURE (5)
                new ProductSeedData(
                        "Ergonomic Office Chair",
                        "High-back mesh office chair with adjustable lumbar support, 2D armrests, and heavy-duty metal base.",
                        7499.0, 35,
                        "https://images.unsplash.com/photo-1580481072645-022f9a6d1275?w=600&auto=format&fit=crop&q=80",
                        "Furniture"
                ),
                new ProductSeedData(
                        "Wooden Study Table",
                        "Engineered wood writing desk with built-in storage drawers and cable management pass-through.",
                        8999.0, 20,
                        "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=600&auto=format&fit=crop&q=80",
                        "Furniture"
                ),
                new ProductSeedData(
                        "Three-Seater Fabric Sofa",
                        "Modern Scandinavian design 3-seater sofa with high-density foam upholstery and solid wood legs.",
                        24999.0, 10,
                        "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600&auto=format&fit=crop&q=80",
                        "Furniture"
                ),
                new ProductSeedData(
                        "Queen Size Wooden Bed",
                        "Sheesham wood queen-size bed frame with hydraulic storage box and headboard lattice.",
                        18999.0, 12,
                        "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=600&auto=format&fit=crop&q=80",
                        "Furniture"
                ),
                new ProductSeedData(
                        "Four-Door Wardrobe",
                        "Spacious 4-door wardrobe with full-length mirror, hanging rods, and lockable interior drawers.",
                        22499.0, 8,
                        "https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=600&auto=format&fit=crop&q=80",
                        "Furniture"
                ),

                // FASHION (5)
                new ProductSeedData(
                        "Men's Cotton Casual Shirt",
                        "100% breathable pure cotton slim-fit button-down casual shirt with mandarin collar.",
                        1299.0, 50,
                        "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=600&auto=format&fit=crop&q=80",
                        "Fashion"
                ),
                new ProductSeedData(
                        "Women's Printed Kurta",
                        "Straight rayon floral printed ethnic A-line kurta with matching dupatta and palazzo pants.",
                        1499.0, 45,
                        "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?w=600&auto=format&fit=crop&q=80",
                        "Fashion"
                ),
                new ProductSeedData(
                        "Men's Running Shoes",
                        "Lightweight breathable mesh upper running sneakers with impact-absorbing EVA cushioned sole.",
                        2999.0, 30,
                        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop&q=80",
                        "Fashion"
                ),
                new ProductSeedData(
                        "Women's Handbag",
                        "Premium vegan leather tote handbag with zipper closure, gold-tone hardware, and adjustable strap.",
                        2199.0, 25,
                        "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&auto=format&fit=crop&q=80",
                        "Fashion"
                ),
                new ProductSeedData(
                        "Unisex Denim Jacket",
                        "Classic washed blue cotton denim trucker jacket with buttoned chest pockets and relaxed fit.",
                        2499.0, 20,
                        "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=600&auto=format&fit=crop&q=80",
                        "Fashion"
                ),

                // HOME APPLIANCES (5)
                new ProductSeedData(
                        "LG Double Door Refrigerator",
                        "242L 3-Star Smart Inverter Frost-Free double door refrigerator with Door Cooling+ technology.",
                        32990.0, 12,
                        "https://images.unsplash.com/photo-1571175443880-49e1d25b2bc5?w=600&auto=format&fit=crop&q=80",
                        "Home Appliances"
                ),
                new ProductSeedData(
                        "IFB Front Load Washing Machine",
                        "7kg 5-Star fully automatic front load washing machine with AI powered wash cycles and steam treatment.",
                        36990.0, 10,
                        "https://images.unsplash.com/photo-1626806787461-102c1bfaaea1?w=600&auto=format&fit=crop&q=80",
                        "Home Appliances"
                ),
                new ProductSeedData(
                        "Philips Air Fryer",
                        "Rapid Air Technology digital air fryer 4.1L for 90% less fat frying, baking, grilling, and roasting.",
                        8995.0, 25,
                        "https://images.unsplash.com/photo-1585515320310-259814833e62?w=600&auto=format&fit=crop&q=80",
                        "Home Appliances"
                ),
                new ProductSeedData(
                        "Prestige Mixer Grinder",
                        "750W heavy-duty copper motor mixer grinder with 3 stainless steel jars and ergonomic handles.",
                        3499.0, 40,
                        "https://images.unsplash.com/photo-1570222094114-d054a817e56b?w=600&auto=format&fit=crop&q=80",
                        "Home Appliances"
                ),
                new ProductSeedData(
                        "Dyson Cordless Vacuum Cleaner",
                        "V12 Detect Slim cordless stick vacuum cleaner with laser dust illumination and HEPA filtration.",
                        45900.0, 8,
                        "https://images.unsplash.com/photo-1558317374-067fb5f30001?w=600&auto=format&fit=crop&q=80",
                        "Home Appliances"
                ),

                // BOOKS (5)
                new ProductSeedData(
                        "Clean Code",
                        "A Handbook of Agile Software Craftsmanship by Robert C. Martin on writing elegant, readable code.",
                        699.0, 50,
                        "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=600&auto=format&fit=crop&q=80",
                        "Books"
                ),
                new ProductSeedData(
                        "Effective Java",
                        "Essential Java programming best practices guide by Joshua Bloch covering modern Java features.",
                        850.0, 40,
                        "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&auto=format&fit=crop&q=80",
                        "Books"
                ),
                new ProductSeedData(
                        "Atomic Habits",
                        "An Easy & Proven Way to Build Good Habits & Break Bad Ones by James Clear.",
                        499.0, 60,
                        "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=600&auto=format&fit=crop&q=80",
                        "Books"
                ),
                new ProductSeedData(
                        "The Psychology of Money",
                        "Timeless lessons on wealth, greed, and happiness by Morgan Housel.",
                        399.0, 55,
                        "https://images.unsplash.com/photo-1592496431122-2349e0fbc666?w=600&auto=format&fit=crop&q=80",
                        "Books"
                ),
                new ProductSeedData(
                        "Introduction to Algorithms",
                        "Comprehensive textbook on computer algorithms by Cormen, Leiserson, Rivest, and Stein (CLRS).",
                        1250.0, 30,
                        "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=600&auto=format&fit=crop&q=80",
                        "Books"
                ),

                // SPORTS (5)
                new ProductSeedData(
                        "Yonex Badminton Racket",
                        "Muscle Power 29 light aluminum badminton racquet with isometric head shape for high repulsion.",
                        2490.0, 35,
                        "https://images.unsplash.com/photo-1626225967045-94408422615d?w=600&auto=format&fit=crop&q=80",
                        "Sports"
                ),
                new ProductSeedData(
                        "Cosco Cricket Bat",
                        "Premium Kashmir willow cricket bat with full grain cover and comfortable rubber grip.",
                        1899.0, 25,
                        "https://images.unsplash.com/photo-1531415074968-036ba1b575da?w=600&auto=format&fit=crop&q=80",
                        "Sports"
                ),
                new ProductSeedData(
                        "Nivia Football",
                        "Size 5 hand-stitched synthetic leather outdoor training football with butyl bladder.",
                        899.0, 40,
                        "https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=600&auto=format&fit=crop&q=80",
                        "Sports"
                ),
                new ProductSeedData(
                        "Yoga Mat",
                        "6mm thick eco-friendly non-slip TPE exercise yoga mat with carrying strap.",
                        799.0, 50,
                        "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=600&auto=format&fit=crop&q=80",
                        "Sports"
                ),
                new ProductSeedData(
                        "Adjustable Dumbbell Set",
                        "20kg PVC dumbbell weights set with solid connecting rod for barbell conversion.",
                        4999.0, 20,
                        "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=600&auto=format&fit=crop&q=80",
                        "Sports"
                ),

                // BEAUTY (5)
                new ProductSeedData(
                        "Face Wash",
                        "Gentle foaming vitamin C tea tree face wash for deep pore cleansing and oil control.",
                        349.0, 60,
                        "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=600&auto=format&fit=crop&q=80",
                        "Beauty"
                ),
                new ProductSeedData(
                        "Moisturizing Cream",
                        "Hyaluronic acid non-greasy day & night hydration moisturizer for all skin types.",
                        499.0, 50,
                        "https://images.unsplash.com/photo-1608248597261-833258657640?w=600&auto=format&fit=crop&q=80",
                        "Beauty"
                ),
                new ProductSeedData(
                        "Sunscreen SPF 50",
                        "Ultra-light weight gel sunscreen with PA+++ broad spectrum UVA & UVB protection.",
                        599.0, 45,
                        "https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=600&auto=format&fit=crop&q=80",
                        "Beauty"
                ),
                new ProductSeedData(
                        "Hair Serum",
                        "Argan oil nourish smoothing hair serum for frizz control and shiny heat damage repair.",
                        425.0, 40,
                        "https://images.unsplash.com/photo-1608248543803-ba4f8c70ae0b?w=600&auto=format&fit=crop&q=80",
                        "Beauty"
                ),
                new ProductSeedData(
                        "Perfume",
                        "Long-lasting Eau De Parfum 100ml with woody and citrus top note fragrances.",
                        1499.0, 30,
                        "https://images.unsplash.com/photo-1523293182086-7651a899d37f?w=600&auto=format&fit=crop&q=80",
                        "Beauty"
                ),

                // GROCERY (5)
                new ProductSeedData(
                        "Basmati Rice 5 kg",
                        "Premium long grain aromatic extra-aged Royal Basmati Rice 5kg pack.",
                        749.0, 80,
                        "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=600&auto=format&fit=crop&q=80",
                        "Grocery"
                ),
                new ProductSeedData(
                        "Sunflower Oil 1 litre",
                        "100% refined cooking sunflower oil enriched with Vitamin A and D.",
                        185.0, 100,
                        "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=600&auto=format&fit=crop&q=80",
                        "Grocery"
                ),
                new ProductSeedData(
                        "Almonds 500 g",
                        "Crisp whole California raw almonds 500g pouch rich in protein and fiber.",
                        499.0, 70,
                        "https://images.unsplash.com/photo-1508061253366-f7da158b6d46?w=600&auto=format&fit=crop&q=80",
                        "Grocery"
                ),
                new ProductSeedData(
                        "Green Tea",
                        "Pure organic lemon honey green tea bags 100-count pack for immunity and metabolism.",
                        299.0, 90,
                        "https://images.unsplash.com/photo-1627435601361-ec25f5b1d0e5?w=600&auto=format&fit=crop&q=80",
                        "Grocery"
                ),
                new ProductSeedData(
                        "Organic Honey",
                        "100% pure raw unprocessed forest organic honey 500g glass jar.",
                        399.0, 60,
                        "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=600&auto=format&fit=crop&q=80",
                        "Grocery"
                )
        );

        for (ProductSeedData seed : seedList) {
            if (!productRepository.existsByNameIgnoreCase(seed.name())) {
                Category cat = categoryRepository.findByNameIgnoreCase(seed.categoryName())
                        .orElseGet(() -> {
                            Category newCat = new Category();
                            newCat.setName(seed.categoryName());
                            newCat.setDescription("Default " + seed.categoryName() + " category");
                            return categoryRepository.save(newCat);
                        });

                Product product = new Product();
                product.setName(seed.name());
                product.setDescription(seed.description());
                product.setPrice(seed.price());
                product.setStockQuantity(seed.stockQuantity());
                product.setImageUrl(seed.imageUrl());
                product.setCategory(cat);

                productRepository.save(product);
            }
        }
    }

    private record ProductSeedData(
            String name,
            String description,
            Double price,
            Integer stockQuantity,
            String imageUrl,
            String categoryName
    ) {}
}