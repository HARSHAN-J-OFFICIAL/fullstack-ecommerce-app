package com.ecommerce.integration;

import com.ecommerce.dto.WishlistResponse;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.enums.RoleName;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.WishlistRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WishlistIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build()));

        testUser = userRepository.findByEmail("wishlist_test@example.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .username("WishlistTester")
                        .email("wishlist_test@example.com")
                        .password("password123")
                        .roles(Set.of(userRole))
                        .build()));

        Category category = new Category();
        category.setName("Audio Equipment");
        category = categoryRepository.save(category);
        testProduct = new Product();
        testProduct.setName("Test Wishlist Headphones");
        testProduct.setDescription("Noise canceling headphones");
        testProduct.setPrice(150.0);
        testProduct.setStockQuantity(20);
        testProduct.setCategory(category);
        testProduct = productRepository.save(testProduct);
    }

    @Test
    @WithMockUser(username = "wishlist_test@example.com", roles = {"USER"})
    void wishlistFlow_AddGetRemove_ShouldSucceed() throws Exception {
        // 1. Get empty wishlist
        mockMvc.perform(get("/wishlist"))
                .andExpect(status().isOk());

        // 2. Add product to wishlist
        MvcResult addResult = mockMvc.perform(post("/wishlist/add/" + testProduct.getId()))
                .andExpect(status().isOk())
                .andReturn();

        WishlistResponse response = objectMapper.readValue(
                addResult.getResponse().getContentAsString(),
                WishlistResponse.class
        );

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(testProduct.getId(), response.getItems().get(0).getProductId());

        // 3. Remove product from wishlist
        mockMvc.perform(delete("/wishlist/remove/" + testProduct.getId()))
                .andExpect(status().isOk());

        // 4. Verify empty wishlist
        MvcResult getResult = mockMvc.perform(get("/wishlist"))
                .andExpect(status().isOk())
                .andReturn();

        WishlistResponse getResponse = objectMapper.readValue(
                getResult.getResponse().getContentAsString(),
                WishlistResponse.class
        );

        assertEquals(0, getResponse.getItems().size());
    }
}
