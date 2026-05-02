package com.streaming.music.repository;

import com.streaming.music.model.Album;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AlbumRepository {
    private final Map<UUID, Album> albumes = new HashMap<>();

    public List<Album> findAll() {
        return List.copyOf(albumes.values());
    }

    public Optional<Album> findById(UUID id) {
        return Optional.ofNullable(albumes.get(id));
    }

    public Album save(Album album) {
        albumes.put(album.getId(), album);
        return album;
    }

    public boolean existsById(UUID id) {
        return albumes.containsKey(id);
    }

    public void deleteById(UUID id) {
        albumes.remove(id);
    }
}
