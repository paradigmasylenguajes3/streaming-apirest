package com.streaming.music.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id")
@ToString
public class Productora {
    private final UUID id;
    private final String nombre;
    private final String pais;

    public Productora(UUID id, String nombre, String pais) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.nombre = Objects.requireNonNull(nombre);
        this.pais = Objects.requireNonNull(pais);
    }
}
