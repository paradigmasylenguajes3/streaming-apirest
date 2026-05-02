package com.streaming.music.dto;

import com.streaming.music.model.Album;

import java.time.LocalDate;
import java.util.UUID;

public record AlbumDTO(UUID id, String titulo, LocalDate fechaLanzamiento, UUID artistaId, UUID productoraId) {
    public static AlbumDTO from(Album album) {
        return new AlbumDTO(album.getId(), album.getTitulo(), album.getFechaLanzamiento(),
                album.getArtista().getId(), album.getProductora().getId());
    }
}
