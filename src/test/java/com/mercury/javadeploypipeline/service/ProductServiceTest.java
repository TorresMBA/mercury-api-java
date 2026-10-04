package com.mercury.javadeploypipeline.service;

import com.mercury.javadeploypipeline.dto.ProductCreateRequest;
import com.mercury.javadeploypipeline.dto.ProductResponse;
import com.mercury.javadeploypipeline.dto.ProductUpdateRequest;
import com.mercury.javadeploypipeline.exception.DuplicateResourceException;
import com.mercury.javadeploypipeline.exception.ResourceNotFoundException;
import com.mercury.javadeploypipeline.model.Product;
import com.mercury.javadeploypipeline.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product(
                1L,
                "Laptop Gamer",
                "Laptop potente",
                new BigDecimal("1200.00"),
                10,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Debe listar todos los productos")
    void shouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));

        List<ProductResponse> products = productService.getAllProducts();

        assertThat(products).hasSize(1);
        assertThat(products.get(0).name()).isEqualTo("Laptop Gamer");
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Debe retornar un producto por su ID cuando existe")
    void shouldReturnProductByIdWhenExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        ProductResponse response = productService.getProductById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Laptop Gamer");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el producto no existe por ID")
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ID 99 no fue encontrado");
    }

    @Test
    @DisplayName("Debe crear un producto correctamente")
    void shouldCreateProductSuccessfully() {
        ProductCreateRequest request = new ProductCreateRequest(
                "Mouse Inalámbrico",
                "Mouse ergonómico",
                new BigDecimal("29.99"),
                50
        );

        when(productRepository.existsByNameIgnoreCase("Mouse Inalámbrico")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(
                new Product(2L, "Mouse Inalámbrico", "Mouse ergonómico", new BigDecimal("29.99"), 50,
                        LocalDateTime.now(), LocalDateTime.now())
        );

        ProductResponse response = productService.createProduct(request);

        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.name()).isEqualTo("Mouse Inalámbrico");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear si ya existe un producto con el mismo nombre")
    void shouldThrowExceptionWhenCreatingDuplicateProductName() {
        ProductCreateRequest request = new ProductCreateRequest(
                "Laptop Gamer",
                "Laptop de prueba",
                new BigDecimal("999.99"),
                5
        );

        when(productRepository.existsByNameIgnoreCase("Laptop Gamer")).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Ya existe un producto con el nombre 'Laptop Gamer'");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe actualizar un producto existente correctamente")
    void shouldUpdateProductSuccessfully() {
        ProductUpdateRequest request = new ProductUpdateRequest(
                "Laptop Gamer Pro",
                "Laptop actualizada",
                new BigDecimal("1400.00"),
                15
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.existsByNameIgnoreCaseAndIdNot("Laptop Gamer Pro", 1L)).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(
                sampleProduct.withUpdates("Laptop Gamer Pro", "Laptop actualizada", new BigDecimal("1400.00"), 15)
        );

        ProductResponse response = productService.updateProduct(1L, request);

        assertThat(response.name()).isEqualTo("Laptop Gamer Pro");
        assertThat(response.price()).isEqualTo(new BigDecimal("1400.00"));
    }

    @Test
    @DisplayName("Debe eliminar un producto por ID cuando existe")
    void shouldDeleteProductWhenExists() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar un producto que no existe")
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productService.deleteProduct(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).deleteById(99L);
    }
}
