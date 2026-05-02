package com.streaming.music.exception;

import java.util.UUID;

public class AlbumNotFoundException extends RuntimeException {
    public AlbumNotFoundException(UUID id) {
        super("Álbum no encontrado con ID: " + id);
    }
}
