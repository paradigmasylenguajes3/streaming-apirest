package com.streaming.music.repository;

import com.streaming.music.model.Artista;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ArtistaRepository {
    private final Map<UUID, Artista> artistas = new HashMap<>();

    public List<Artista> findAll() {
        return List.copyOf(artistas.values());
    }

    public Optional<Artista> findById(UUID id) {
        return Optional.ofNullable(artistas.get(id));
    }

    public Artista save(Artista artista) {
        artistas.put(artista.getId(), artista);
        return artista;
    }

    public boolean existsById(UUID id) {
        return artistas.containsKey(id);
    }

    public int count() {
        return artistas.size();
    }

    public void deleteById(UUID id) {
        artistas.remove(id);
    }
}
