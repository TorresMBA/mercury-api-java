package com.mercury.javadeploypipeline.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercury.javadeploypipeline.dto.ProductCreateRequest;
import com.mercury.javadeploypipeline.dto.ProductUpdateRequest;
import com.mercury.javadeploypipeline.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v1/products - Debe crear un producto y retornar 201 Created con cabecera Location")
    void shouldCreateProduct() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest(
                "Monitor 4K",
                "Monitor de 27 pulgadas",
                new BigDecimal("350.00"),
                10
        );

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Monitor 4K")))
                .andExpect(jsonPath("$.price", is(350.00)))
                .andExpect(jsonPath("$.stock", is(10)));
    }

    @Test
    @DisplayName("POST /api/v1/products - Validación: Debe retornar 400 cuando los campos son inválidos")
    void shouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest(
                "", // Blank name
                "Descripción",
                new BigDecimal("-5.00"), // Invalid price
                -1 // Invalid stock
        );

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.details", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/products - Debe retornar lista de productos con status 200")
    void shouldGetAllProducts() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest(
                "Teclado Mecánico",
                "Switches red",
                new BigDecimal("80.00"),
                20
        );

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Teclado Mecánico")));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Debe retornar 404 cuando no existe")
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id} - Debe actualizar y retornar 200")
    void shouldUpdateProduct() throws Exception {
        ProductCreateRequest createRequest = new ProductCreateRequest(
                "Silla Ergonómica",
                "Silla de oficina",
                new BigDecimal("200.00"),
                5
        );

        String createResponse = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        ProductUpdateRequest updateRequest = new ProductUpdateRequest(
                "Silla Ergonómica Pro",
                "Silla de oficina mejorada",
                new BigDecimal("250.00"),
                8
        );

        mockMvc.perform(put("/api/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Silla Ergonómica Pro")))
                .andExpect(jsonPath("$.price", is(250.00)))
                .andExpect(jsonPath("$.stock", is(8)));
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id} - Debe eliminar y retornar 204 No Content")
    void shouldDeleteProduct() throws Exception {
        ProductCreateRequest createRequest = new ProductCreateRequest(
                "Webcam HD",
                "1080p 60fps",
                new BigDecimal("60.00"),
                15
        );

        String createResponse = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(delete("/api/v1/products/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isNotFound());
    }
}
