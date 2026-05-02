package com.streaming.music.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id")
@ToString
public class Album {
    private final UUID id;
    private final String titulo;
    private final LocalDate fechaLanzamiento;
    private final Artista artista;
    private final Productora productora;

    public Album(UUID id, String titulo, LocalDate fechaLanzamiento, Artista artista, Productora productora) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.titulo = Objects.requireNonNull(titulo);
        this.fechaLanzamiento = Objects.requireNonNull(fechaLanzamiento);
        this.artista = Objects.requireNonNull(artista);
        this.productora = Objects.requireNonNull(productora);
    }
}
