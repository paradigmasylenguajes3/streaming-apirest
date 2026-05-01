package com.streaming.music.repository;

import com.streaming.music.model.Cancion;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CancionRepository {
    private final ConcurrentHashMap<UUID, Cancion> canciones = new ConcurrentHashMap<>();

    public List<Cancion> findAll() {
        return new ArrayList<>(canciones.values());
    }

    public Optional<Cancion> findById(UUID id) {
        return Optional.ofNullable(canciones.get(id));
    }

    public Cancion save(Cancion cancion) {
        canciones.put(cancion.getId(), cancion);
        return cancion;
    }

    public void saveAll(List<Cancion> lista) {
        lista.forEach(c -> canciones.put(c.getId(), c));
    }

    public boolean existsById(UUID id) {
        return canciones.containsKey(id);
    }

    public int count() {
        return canciones.size();
    }
}
