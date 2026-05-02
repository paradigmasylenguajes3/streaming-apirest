package com.streaming.music.exception;

import java.util.UUID;

public class ProductoraNotFoundException extends RuntimeException {
    public ProductoraNotFoundException(UUID id) {
        super("Productora no encontrada con ID: " + id);
    }
}
