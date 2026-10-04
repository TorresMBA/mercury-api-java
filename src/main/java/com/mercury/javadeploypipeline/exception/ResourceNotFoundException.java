package com.mercury.javadeploypipeline.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s con ID %d no fue encontrado", resourceName, id));
    }
}
