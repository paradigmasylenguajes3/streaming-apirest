package com.streaming.music.model;

import java.util.Objects;
import java.util.UUID;

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

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public Genero getGenero() { return genero; }
    public String getBiografia() { return biografia; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artista other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Artista{nombre='%s', genero=%s}".formatted(nombre, genero);
    }
}
