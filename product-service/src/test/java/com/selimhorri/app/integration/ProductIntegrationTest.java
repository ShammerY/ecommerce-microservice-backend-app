package com.selimhorri.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.domain.Category;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.repository.CategoryRepository;
import com.selimhorri.app.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    private ProductDto sampleProduct;
    private Category savedCategory;

    @BeforeEach
    void setUp() {

        // 🔹 Limpiar BD antes de cada test
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        // 🔹 Crear una categoría real en la base de datos
        Category category = new Category();
        category.setCategoryTitle("Electronics");
        category.setImageUrl("https://example.com/electronics.jpg");
        savedCategory = categoryRepository.save(category);

        // 🔹 Crear un producto que usa esa categoría
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(savedCategory.getCategoryId())
                .categoryTitle(savedCategory.getCategoryTitle())
                .imageUrl(savedCategory.getImageUrl())
                .build();

        sampleProduct = ProductDto.builder()
                .productTitle("Laptop Dell XPS")
                .sku("LAP-001")
                .priceUnit(3500.00)
                .quantity(5)
                .imageUrl("https://example.com/laptop.jpg")
                .categoryDto(categoryDto)
                .build();
    }

    @Test
    void testCreateProduct() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productTitle", is("Laptop Dell XPS")))
                .andExpect(jsonPath("$.sku", is("LAP-001")));
    }

    @Test
    void testFindAllProducts() throws Exception {
        // Crear primero un producto
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleProduct)));

        // Luego obtenerlos todos
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection", not(empty())))
                .andExpect(jsonPath("$.collection[0].productTitle", is("Laptop Dell XPS")));
    }

    @Test
    void testFindProductById() throws Exception {
        // Crear producto
        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProductDto created = objectMapper.readValue(response, ProductDto.class);

        // Buscar producto por ID
        mockMvc.perform(get("/api/products/" + created.getProductId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", is(created.getProductId())))
                .andExpect(jsonPath("$.productTitle", is("Laptop Dell XPS")));
    }

    @Test
    void testUpdateProduct() throws Exception {
        // Crear producto inicial
        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProductDto created = objectMapper.readValue(response, ProductDto.class);
        created.setProductTitle("Laptop Dell XPS Updated");

        // Actualizarlo
        mockMvc.perform(put("/api/products/" + created.getProductId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productTitle", is("Laptop Dell XPS Updated")));
    }

    @Test
    void testDeleteProduct() throws Exception {
        // Crear producto
        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProductDto created = objectMapper.readValue(response, ProductDto.class);

        // Eliminar producto
        mockMvc.perform(delete("/api/products/" + created.getProductId()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
