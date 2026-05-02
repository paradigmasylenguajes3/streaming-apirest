package com.streaming.music.service;

import com.streaming.music.dto.ArtistaDTO;
import com.streaming.music.dto.CreateArtistaRequest;
import com.streaming.music.dto.UpdateArtistaRequest;
import com.streaming.music.exception.ArtistaNotFoundException;
import com.streaming.music.model.Artista;
import com.streaming.music.model.Genero;
import com.streaming.music.repository.ArtistaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ArtistaService {

    private final ArtistaRepository artistaRepository;

    public ArtistaService(ArtistaRepository artistaRepository) {
        this.artistaRepository = artistaRepository;
    }

    public List<ArtistaDTO> findAll() {
        return artistaRepository.findAll().stream()
                .map(ArtistaDTO::from)
                .toList();
    }

    public ArtistaDTO findById(UUID id) {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(() -> new ArtistaNotFoundException(id));
        return ArtistaDTO.from(artista);
    }

    public ArtistaDTO create(CreateArtistaRequest request) {
        Genero genero = Genero.valueOf(request.genero());
        Artista artista = new Artista(null, request.nombre(), genero, request.biografia());
        Artista saved = artistaRepository.save(artista);
        return ArtistaDTO.from(saved);
    }

    public ArtistaDTO update(UUID id, UpdateArtistaRequest request) {
        artistaRepository.findById(id)
                .orElseThrow(() -> new ArtistaNotFoundException(id));
        Genero genero = Genero.valueOf(request.genero());
        Artista updated = new Artista(id, request.nombre(), genero, request.biografia());
        artistaRepository.save(updated);
        return ArtistaDTO.from(updated);
    }

    public void delete(UUID id) {
        if (!artistaRepository.existsById(id)) {
            throw new ArtistaNotFoundException(id);
        }
        artistaRepository.deleteById(id);
    }
}
