package com.streaming.music.service;

import com.streaming.music.dto.AlbumDTO;
import com.streaming.music.dto.CreateAlbumRequest;
import com.streaming.music.dto.UpdateAlbumRequest;
import com.streaming.music.exception.AlbumNotFoundException;
import com.streaming.music.exception.ArtistaNotFoundException;
import com.streaming.music.exception.ProductoraNotFoundException;
import com.streaming.music.model.Album;
import com.streaming.music.model.Artista;
import com.streaming.music.model.Productora;
import com.streaming.music.repository.AlbumRepository;
import com.streaming.music.repository.ArtistaRepository;
import com.streaming.music.repository.ProductoraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistaRepository artistaRepository;
    private final ProductoraRepository productoraRepository;

    public AlbumService(AlbumRepository albumRepository,
                        ArtistaRepository artistaRepository,
                        ProductoraRepository productoraRepository) {
        this.albumRepository = albumRepository;
        this.artistaRepository = artistaRepository;
        this.productoraRepository = productoraRepository;
    }

    public List<AlbumDTO> findAll() {
        return albumRepository.findAll().stream()
                .map(AlbumDTO::from)
                .toList();
    }

    public AlbumDTO findById(UUID id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id));
        return AlbumDTO.from(album);
    }

    public AlbumDTO create(CreateAlbumRequest request) {
        Artista artista = artistaRepository.findById(request.artistaId())
                .orElseThrow(() -> new ArtistaNotFoundException(request.artistaId()));
        Productora productora = productoraRepository.findById(request.productoraId())
                .orElseThrow(() -> new ProductoraNotFoundException(request.productoraId()));
        Album album = new Album(null, request.titulo(), request.fechaLanzamiento(),
                artista, productora);
        Album saved = albumRepository.save(album);
        return AlbumDTO.from(saved);
    }

    public AlbumDTO update(UUID id, UpdateAlbumRequest request) {
        albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id));
        Artista artista = artistaRepository.findById(request.artistaId())
                .orElseThrow(() -> new ArtistaNotFoundException(request.artistaId()));
        Productora productora = productoraRepository.findById(request.productoraId())
                .orElseThrow(() -> new ProductoraNotFoundException(request.productoraId()));
        Album updated = new Album(id, request.titulo(), request.fechaLanzamiento(),
                artista, productora);
        albumRepository.save(updated);
        return AlbumDTO.from(updated);
    }

    public void delete(UUID id) {
        if (!albumRepository.existsById(id)) {
            throw new AlbumNotFoundException(id);
        }
        albumRepository.deleteById(id);
    }
}
