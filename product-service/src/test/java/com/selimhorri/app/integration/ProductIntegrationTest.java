package com.selimhorri.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para ProductResource
 * Validan endpoints reales del microservicio Product-Service.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDto sampleProduct;

    @BeforeEach
    void setUp() {
        // Crea una categoría simulada
        CategoryDto category = CategoryDto.builder()
                .categoryId(1) // Puede ser cualquier ID ficticio
                .categoryTitle("Electronics")
                .imageUrl("https://example.com/electronics.jpg")
                .build();

        // Crea un producto asociado a esa categoría
        sampleProduct = ProductDto.builder()
                .productId(1)
                .productTitle("Laptop Dell XPS")
                .sku("LAP-001")
                .priceUnit(3500.00)
                .quantity(5)
                .imageUrl("https://example.com/laptop.jpg")
                .categoryDto(category)
                .build();
    }

    @Test
    void testFindAllProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection", hasSize(greaterThan(0))));
    }

    @Test
    void testFindProductById() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.productTitle", notNullValue()));
    }

    @Test
    void testCreateProduct() throws Exception {
        String productJson = objectMapper.writeValueAsString(sampleProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productTitle", is("Laptop Dell XPS")))
                .andExpect(jsonPath("$.category.categoryId", is(1)));
    }

    @Test
    void testUpdateProduct() throws Exception {
        sampleProduct.setProductTitle("Laptop Dell XPS Updated");
        String updatedJson = objectMapper.writeValueAsString(sampleProduct);

        mockMvc.perform(put("/api/products/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productTitle", is("Laptop Dell XPS Updated")));
    }

    @Test
    void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
