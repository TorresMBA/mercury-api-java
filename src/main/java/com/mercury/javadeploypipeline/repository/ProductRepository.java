package com.mercury.javadeploypipeline.repository;

import com.mercury.javadeploypipeline.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findAll();

    boolean existsById(Long id);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean deleteById(Long id);

    void deleteAll();
}
