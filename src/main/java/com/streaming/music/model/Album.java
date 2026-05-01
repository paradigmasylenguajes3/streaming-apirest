package com.streaming.music.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Album {
    private final UUID id;
    private final String titulo;
    private final LocalDate fechaLanzamiento;
    private final UUID artistaId;
    private final UUID productoraId;

    public Album(UUID id, String titulo, LocalDate fechaLanzamiento, UUID artistaId, UUID productoraId) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.titulo = Objects.requireNonNull(titulo);
        this.fechaLanzamiento = Objects.requireNonNull(fechaLanzamiento);
        this.artistaId = Objects.requireNonNull(artistaId);
        this.productoraId = Objects.requireNonNull(productoraId);
    }

    public UUID getId() { return id; }
    public String getTitulo() { return titulo; }
    public LocalDate getFechaLanzamiento() { return fechaLanzamiento; }
    public UUID getArtistaId() { return artistaId; }
    public UUID getProductoraId() { return productoraId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Album other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Album{titulo='%s', fecha=%s}".formatted(titulo, fechaLanzamiento);
    }
}
