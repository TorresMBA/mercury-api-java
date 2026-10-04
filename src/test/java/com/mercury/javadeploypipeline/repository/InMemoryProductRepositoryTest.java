package com.mercury.javadeploypipeline.repository;

import com.mercury.javadeploypipeline.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryProductRepositoryTest {

    private InMemoryProductRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProductRepository();
    }

    @Test
    @DisplayName("Debe guardar un producto y autogenerar el ID secuencial")
    void shouldSaveAndGenerateId() {
        Product product = Product.ofNew("Teclado", "Mecánico", new BigDecimal("45.00"), 10);

        Product saved = repository.save(product);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getName()).isEqualTo("Teclado");
    }

    @Test
    @DisplayName("Debe buscar por ID y retornar Optional")
    void shouldFindById() {
        Product saved = repository.save(Product.ofNew("Mouse", "Óptico", new BigDecimal("20.00"), 5));

        Optional<Product> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Mouse");
    }

    @Test
    @DisplayName("Debe retornar lista vacía o con todos los productos ordenados por ID")
    void shouldFindAllOrderedById() {
        repository.save(Product.ofNew("Producto B", "Desc", new BigDecimal("10.00"), 1));
        repository.save(Product.ofNew("Producto A", "Desc", new BigDecimal("20.00"), 2));

        List<Product> all = repository.findAll();

        assertThat(all).hasSize(2);
        assertThat(all.get(0).getId()).isEqualTo(1L);
        assertThat(all.get(1).getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Debe verificar existencia por nombre ignorando mayúsculas/minúsculas")
    void shouldCheckExistsByNameIgnoreCase() {
        repository.save(Product.ofNew("Auriculares", "Bluetooth", new BigDecimal("50.00"), 8));

        assertThat(repository.existsByNameIgnoreCase("AURICULARES")).isTrue();
        assertThat(repository.existsByNameIgnoreCase("  auriculares  ")).isTrue();
        assertThat(repository.existsByNameIgnoreCase("Parlantes")).isFalse();
    }

    @Test
    @DisplayName("Debe verificar existencia por nombre excluyendo un ID específico")
    void shouldCheckExistsByNameIgnoreCaseAndIdNot() {
        Product p1 = repository.save(Product.ofNew("Auriculares", "Bluetooth", new BigDecimal("50.00"), 8));
        Product p2 = repository.save(Product.ofNew("Mouse", "Óptico", new BigDecimal("25.00"), 4));

        assertThat(repository.existsByNameIgnoreCaseAndIdNot("Auriculares", p1.getId())).isFalse();
        assertThat(repository.existsByNameIgnoreCaseAndIdNot("Auriculares", p2.getId())).isTrue();
    }

    @Test
    @DisplayName("Debe eliminar un producto por ID")
    void shouldDeleteById() {
        Product saved = repository.save(Product.ofNew("Webcam", "HD", new BigDecimal("40.00"), 3));

        boolean deleted = repository.deleteById(saved.getId());

        assertThat(deleted).isTrue();
        assertThat(repository.findById(saved.getId())).isEmpty();
    }
}
