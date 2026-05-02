package com.streaming.music.exception;

import java.util.UUID;

public class ArtistaNotFoundException extends RuntimeException {
    public ArtistaNotFoundException(UUID id) {
        super("Artista no encontrado con ID: " + id);
    }
}
