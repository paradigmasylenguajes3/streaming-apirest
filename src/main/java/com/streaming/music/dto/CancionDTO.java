package com.streaming.music.dto;

import com.streaming.music.model.Cancion;

import java.time.LocalDate;
import java.util.UUID;

public record CancionDTO(
        UUID id,
        String titulo,
        String genero,
        int duracionSegundos,
        int reproducciones,
        double rating,
        LocalDate fechaLanzamiento,
        ArtistaDTO artista,
        AlbumDTO album) {

    public static CancionDTO from(Cancion cancion, ArtistaDTO artista, AlbumDTO album) {
        return new CancionDTO(
                cancion.getId(),
                cancion.getTitulo(),
                cancion.getGenero().name(),
                cancion.getDuracionSegundos(),
                cancion.getReproducciones(),
                cancion.getRating(),
                cancion.getFechaLanzamiento(),
                artista,
                album);
    }
}
