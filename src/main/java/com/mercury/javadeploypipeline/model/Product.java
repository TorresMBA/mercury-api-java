package com.mercury.javadeploypipeline.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Product {

    private final Long id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final Integer stock;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Product(Long id, String name, String description, BigDecimal price, Integer stock,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Product ofNew(String name, String description, BigDecimal price, Integer stock) {
        LocalDateTime now = LocalDateTime.now();
        return new Product(null, name, description, price, stock, now, now);
    }

    public Product withId(Long id) {
        return new Product(id, this.name, this.description, this.price, this.stock, this.createdAt, this.updatedAt);
    }

    public Product withUpdates(String name, String description, BigDecimal price, Integer stock) {
        return new Product(this.id, name, description, price, stock, this.createdAt, LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }
}
