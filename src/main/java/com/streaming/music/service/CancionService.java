package com.streaming.music.service;

import com.streaming.music.dto.*;
import com.streaming.music.exception.CancionNotFoundException;
import com.streaming.music.model.*;
import com.streaming.music.repository.*;
import com.streaming.music.service.estrategia.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CancionService {

    private final CancionRepository cancionRepository;
    private final RecomendacionPorGenero recomendacionPorGenero;
    private final RecomendacionPorPopularidad recomendacionPorPopularidad;
    private final RecomendacionDescubrimiento recomendacionDescubrimiento;
    private final ProcesamientoCancionService procesamientoCancionService;
    private final PlaylistService playlistService;
    private final BusquedaService busquedaService;

    public CancionService(CancionRepository cancionRepository,
                          RecomendacionPorGenero recomendacionPorGenero,
                          RecomendacionPorPopularidad recomendacionPorPopularidad,
                          RecomendacionDescubrimiento recomendacionDescubrimiento,
                          ProcesamientoCancionService procesamientoCancionService,
                          PlaylistService playlistService,
                          BusquedaService busquedaService) {
        this.cancionRepository = cancionRepository;
        this.recomendacionPorGenero = recomendacionPorGenero;
        this.recomendacionPorPopularidad = recomendacionPorPopularidad;
        this.recomendacionDescubrimiento = recomendacionDescubrimiento;
        this.procesamientoCancionService = procesamientoCancionService;
        this.playlistService = playlistService;
        this.busquedaService = busquedaService;
    }

    public List<CancionDTO> findAll() {
        return cancionRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public CancionDTO findById(UUID id) {
        Cancion cancion = cancionRepository.findById(id)
                .orElseThrow(() -> new CancionNotFoundException(id));
        return toDTO(cancion);
    }

    public CancionDTO incrementarReproducciones(UUID id) {
        Cancion cancion = cancionRepository.findById(id)
                .orElseThrow(() -> new CancionNotFoundException(id));
        cancion.incrementarReproducciones();
        return toDTO(cancion);
    }

    public List<CancionDTO> recomendarPorGenero(UUID cancionId) {
        Cancion base = cancionRepository.findById(cancionId)
                .orElseThrow(() -> new CancionNotFoundException(cancionId));
        List<Cancion> catalogo = cancionRepository.findAll();
        return recomendacionPorGenero.recomendar(catalogo, base).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<CancionDTO> recomendarTopGlobal(UUID cancionId) {
        Cancion base = cancionRepository.findById(cancionId)
                .orElseThrow(() -> new CancionNotFoundException(cancionId));
        List<Cancion> catalogo = cancionRepository.findAll();
        return recomendacionPorPopularidad.recomendar(catalogo, base).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<CancionDTO> recomendarDescubrimiento(UUID cancionId) {
        Cancion base = cancionRepository.findById(cancionId)
                .orElseThrow(() -> new CancionNotFoundException(cancionId));
        List<Cancion> catalogo = cancionRepository.findAll();
        return recomendacionDescubrimiento.recomendar(catalogo, base).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<CancionDTO> filtrar(Genero genero, Integer ratingMinimo, Integer duracionMaxima) {
        return procesamientoCancionService.filtrar(genero, ratingMinimo, duracionMaxima).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<CancionDTO> top10() {
        return procesamientoCancionService.top10MasReproducidas().stream()
                .map(this::toDTO)
                .toList();
    }

    public Map<Genero, Double> promedioPorGenero() {
        return procesamientoCancionService.promedioRatingPorGenero();
    }

    public ProcesamientoCancionService.ArtistaEstadistica artistaMasPopular() {
        return procesamientoCancionService.artistaMasPopular();
    }

    public Map<String, Long> distribucionDecadas() {
        return procesamientoCancionService.distribucionPorDecadas();
    }

    public List<CancionDTO> playlistExacta(int minutos) {
        List<Cancion> catalogo = cancionRepository.findAll();
        return playlistService.playlistExacta(catalogo, minutos).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<CancionDTO> busquedaBinariaPorTitulo(String titulo) {
        List<Cancion> catalogo = cancionRepository.findAll();
        return busquedaService.busquedaBinariaPorTitulo(catalogo, titulo)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<CancionDTO> ordenarPersonalizado(String criterio) {
        List<Cancion> catalogo = cancionRepository.findAll();
        return busquedaService.ordenarPersonalizado(catalogo, criterio).stream()
                .map(this::toDTO)
                .toList();
    }

    private CancionDTO toDTO(Cancion cancion) {
        ArtistaDTO artistaDTO = ArtistaDTO.from(cancion.getArtista());
        AlbumDTO albumDTO = AlbumDTO.from(cancion.getAlbum());
        return CancionDTO.from(cancion, artistaDTO, albumDTO);
    }
}
