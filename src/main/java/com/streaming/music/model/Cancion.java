package com.streaming.music.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Cancion {
    private final UUID id;
    private final String titulo;
    private final Genero genero;
    private final int duracionSegundos;
    private final AtomicInteger reproducciones;
    private final double rating;
    private final LocalDate fechaLanzamiento;
    private final UUID artistaId;
    private final UUID albumId;

    public Cancion(String titulo, Genero genero, int duracionSegundos,
                   double rating, LocalDate fechaLanzamiento,
                   UUID artistaId, UUID albumId) {
        this.id = UUID.randomUUID();
        this.titulo = Objects.requireNonNull(titulo);
        this.genero = Objects.requireNonNull(genero);
        this.duracionSegundos = duracionSegundos;
        this.reproducciones = new AtomicInteger(0);
        this.rating = rating;
        this.fechaLanzamiento = Objects.requireNonNull(fechaLanzamiento);
        this.artistaId = Objects.requireNonNull(artistaId);
        this.albumId = Objects.requireNonNull(albumId);
    }

    public UUID getId() { return id; }
    public String getTitulo() { return titulo; }
    public Genero getGenero() { return genero; }
    public int getDuracionSegundos() { return duracionSegundos; }
    public double getRating() { return rating; }
    public LocalDate getFechaLanzamiento() { return fechaLanzamiento; }
    public UUID getArtistaId() { return artistaId; }
    public UUID getAlbumId() { return albumId; }

    public int getReproducciones() {
        return reproducciones.get();
    }

    public int incrementarReproducciones() {
        return reproducciones.incrementAndGet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cancion other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cancion{titulo='%s', genero=%s, duracion=%ds, reproducciones=%d, rating=%.1f}"
                .formatted(titulo, genero, duracionSegundos, getReproducciones(), rating);
    }
}
