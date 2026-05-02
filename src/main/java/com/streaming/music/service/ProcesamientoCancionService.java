package com.streaming.music.service;

import com.streaming.music.model.Artista;
import com.streaming.music.model.Cancion;
import com.streaming.music.model.Genero;
import com.streaming.music.repository.CancionRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProcesamientoCancionService {

    private final CancionRepository cancionRepository;

    public ProcesamientoCancionService(CancionRepository cancionRepository) {
        this.cancionRepository = cancionRepository;
    }

    public List<Cancion> filtrar(Genero genero, Integer ratingMinimo, Integer duracionMaxima) {
        return cancionRepository.findAll().stream()
                .filter(c -> genero == null || c.getGenero() == genero)
                .filter(c -> ratingMinimo == null || c.getRating() >= ratingMinimo)
                .filter(c -> duracionMaxima == null || c.getDuracionSegundos() <= duracionMaxima)
                .toList();
    }

    public List<Cancion> top10MasReproducidas() {
        return cancionRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Cancion::getReproducciones).reversed())
                .limit(10)
                .toList();
    }

    public Map<Genero, Double> promedioRatingPorGenero() {
        return cancionRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Cancion::getGenero,
                        Collectors.averagingDouble(Cancion::getRating)));
    }

    public ArtistaEstadistica artistaMasPopular() {
        return cancionRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Cancion::getArtista,
                        Collectors.summingInt(Cancion::getReproducciones)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new ArtistaEstadistica(e.getKey(), e.getValue()))
                .orElse(null);
    }

    public Map<String, Long> distribucionPorDecadas() {
        return cancionRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        c -> (c.getFechaLanzamiento().getYear() / 10 * 10) + "s",
                        Collectors.counting()));
    }

    public record ArtistaEstadistica(Artista artista, int totalReproducciones) {}
}
