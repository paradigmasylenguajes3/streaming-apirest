package com.streaming.music.repository;

import com.streaming.music.model.Productora;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ProductoraRepository {
    private final Map<UUID, Productora> productoras = new HashMap<>();

    public List<Productora> findAll() {
        return List.copyOf(productoras.values());
    }

    public Optional<Productora> findById(UUID id) {
        return Optional.ofNullable(productoras.get(id));
    }

    public Productora save(Productora productora) {
        productoras.put(productora.getId(), productora);
        return productora;
    }

    public boolean existsById(UUID id) {
        return productoras.containsKey(id);
    }

    public void deleteById(UUID id) {
        productoras.remove(id);
    }
}
