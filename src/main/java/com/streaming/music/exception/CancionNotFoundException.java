package com.streaming.music.exception;

import java.util.UUID;

public class CancionNotFoundException extends RuntimeException {
    public CancionNotFoundException(UUID id) {
        super("Canción no encontrada con ID: " + id);
    }
}
