package com.streaming.music.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@EqualsAndHashCode(of = "id")
@ToString
public class Cancion {
    private final UUID id;
    private final String titulo;
    private final Genero genero;
    private final int duracionSegundos;
    private final AtomicInteger reproducciones;
    private final double rating;
    private final LocalDate fechaLanzamiento;
    private final Artista artista;
    private final Album album;

    public Cancion(String titulo, Genero genero, int duracionSegundos,
                   double rating, LocalDate fechaLanzamiento,
                   Artista artista, Album album) {
        this.id = UUID.randomUUID();
        this.titulo = Objects.requireNonNull(titulo);
        this.genero = Objects.requireNonNull(genero);
        this.duracionSegundos = duracionSegundos;
        this.reproducciones = new AtomicInteger(0);
        this.rating = rating;
        this.fechaLanzamiento = Objects.requireNonNull(fechaLanzamiento);
        this.artista = Objects.requireNonNull(artista);
        this.album = Objects.requireNonNull(album);
    }

    public int getReproducciones() {
        return reproducciones.get();
    }

    public int incrementarReproducciones() {
        return reproducciones.incrementAndGet();
    }
}
