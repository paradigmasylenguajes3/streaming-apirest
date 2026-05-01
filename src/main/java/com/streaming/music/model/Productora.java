package com.streaming.music.model;

import java.util.Objects;
import java.util.UUID;

public class Productora {
    private final UUID id;
    private final String nombre;
    private final String pais;

    public Productora(UUID id, String nombre, String pais) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.nombre = Objects.requireNonNull(nombre);
        this.pais = Objects.requireNonNull(pais);
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getPais() { return pais; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Productora other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Productora{nombre='%s', pais='%s'}".formatted(nombre, pais);
    }
}
