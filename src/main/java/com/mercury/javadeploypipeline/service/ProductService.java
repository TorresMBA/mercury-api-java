package com.mercury.javadeploypipeline.service;

import com.mercury.javadeploypipeline.dto.ProductCreateRequest;
import com.mercury.javadeploypipeline.dto.ProductResponse;
import com.mercury.javadeploypipeline.dto.ProductUpdateRequest;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long id);

    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse updateProduct(Long id, ProductUpdateRequest request);

    void deleteProduct(Long id);
}
