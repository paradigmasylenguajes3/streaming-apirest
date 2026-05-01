package com.streaming.music.dto;

import com.streaming.music.model.Productora;

import java.util.UUID;

public record ProductoraDTO(UUID id, String nombre, String pais) {
    public static ProductoraDTO from(Productora productora) {
        return new ProductoraDTO(productora.getId(), productora.getNombre(), productora.getPais());
    }
}
