package com.mercury.javadeploypipeline.service;

import com.mercury.javadeploypipeline.dto.ProductCreateRequest;
import com.mercury.javadeploypipeline.dto.ProductResponse;
import com.mercury.javadeploypipeline.dto.ProductUpdateRequest;
import com.mercury.javadeploypipeline.exception.DuplicateResourceException;
import com.mercury.javadeploypipeline.exception.ResourceNotFoundException;
import com.mercury.javadeploypipeline.model.Product;
import com.mercury.javadeploypipeline.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        log.debug("Consultando listado de todos los productos");
        return productRepository.findAll().stream()
                .map(ProductResponse::fromDomain)
                .toList();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        log.debug("Buscando producto con id: {}", id);
        return productRepository.findById(id)
                .map(ProductResponse::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }

    @Override
    public ProductResponse createProduct(ProductCreateRequest request) {
        log.info("Creando nuevo producto con nombre: {}", request.name());

        if (productRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException(
                    String.format("Ya existe un producto con el nombre '%s'", request.name()));
        }

        Product product = Product.ofNew(
                request.name().trim(),
                request.description() != null ? request.description().trim() : null,
                request.price(),
                request.stock()
        );

        Product savedProduct = productRepository.save(product);
        log.info("Producto creado exitosamente con ID: {}", savedProduct.getId());
        return ProductResponse.fromDomain(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        log.info("Actualizando producto con ID: {}", id);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        if (productRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new DuplicateResourceException(
                    String.format("Ya existe otro producto con el nombre '%s'", request.name()));
        }

        Product updatedProduct = existingProduct.withUpdates(
                request.name().trim(),
                request.description() != null ? request.description().trim() : null,
                request.price(),
                request.stock()
        );

        Product savedProduct = productRepository.save(updatedProduct);
        log.info("Producto con ID {} actualizado exitosamente", id);
        return ProductResponse.fromDomain(savedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Eliminando producto con ID: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", id);
        }
        productRepository.deleteById(id);
        log.info("Producto con ID {} eliminado exitosamente", id);
    }
}
