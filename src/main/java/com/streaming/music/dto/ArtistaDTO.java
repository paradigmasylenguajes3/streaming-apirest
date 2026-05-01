package com.streaming.music.dto;

import com.streaming.music.model.Artista;

import java.util.UUID;

public record ArtistaDTO(UUID id, String nombre, String genero, String biografia) {
    public static ArtistaDTO from(Artista artista) {
        return new ArtistaDTO(artista.getId(), artista.getNombre(), artista.getGenero().name(), artista.getBiografia());
    }
}
