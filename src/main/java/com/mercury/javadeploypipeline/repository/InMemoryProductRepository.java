package com.mercury.javadeploypipeline.repository;

import com.mercury.javadeploypipeline.model.Product;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final Map<Long, Product> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            Long newId = idGenerator.getAndIncrement();
            Product newProduct = product.withId(newId);
            storage.put(newId, newProduct);
            return newProduct;
        }

        storage.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Product> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Product::getId))
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        return storage.containsKey(id);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        if (name == null) {
            return false;
        }
        String trimmedName = name.trim();
        return storage.values().stream()
                .anyMatch(product -> product.getName().equalsIgnoreCase(trimmedName));
    }

    @Override
    public boolean existsByNameIgnoreCaseAndIdNot(String name, Long id) {
        if (name == null || id == null) {
            return false;
        }
        String trimmedName = name.trim();
        return storage.values().stream()
                .anyMatch(product -> !product.getId().equals(id) && product.getName().equalsIgnoreCase(trimmedName));
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }
        return storage.remove(id) != null;
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }
}
