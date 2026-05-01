package com.streaming.music.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id")
@ToString
public class Artista {
    private final UUID id;
    private final String nombre;
    private final Genero genero;
    private final String biografia;

    public Artista(UUID id, String nombre, Genero genero, String biografia) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.nombre = Objects.requireNonNull(nombre);
        this.genero = Objects.requireNonNull(genero);
        this.biografia = Objects.requireNonNull(biografia);
    }
}
