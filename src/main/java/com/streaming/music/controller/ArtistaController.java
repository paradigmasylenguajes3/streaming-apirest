package com.streaming.music.controller;

import com.streaming.music.dto.ArtistaDTO;
import com.streaming.music.exception.CancionNotFoundException;
import com.streaming.music.model.Artista;
import com.streaming.music.repository.ArtistaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/artistas")
@Tag(name = "Artistas", description = "Endpoints para gestión y consulta de artistas")
public class ArtistaController {

    private final ArtistaRepository artistaRepository;

    public ArtistaController(ArtistaRepository artistaRepository) {
        this.artistaRepository = artistaRepository;
    }

    @GetMapping
    @Operation(summary = "Listar todos los artistas")
    @ApiResponse(responseCode = "200", description = "Lista de artistas")
    public ResponseEntity<List<ArtistaDTO>> findAll() {
        List<ArtistaDTO> artistas = artistaRepository.findAll().stream()
                .map(ArtistaDTO::from)
                .toList();
        return ResponseEntity.ok(artistas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener artista por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Artista encontrado"),
            @ApiResponse(responseCode = "404", description = "Artista no encontrado")
    })
    public ResponseEntity<ArtistaDTO> findById(
            @Parameter(description = "UUID del artista", required = true)
            @PathVariable UUID id) {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(() -> new CancionNotFoundException(id));
        return ResponseEntity.ok(ArtistaDTO.from(artista));
    }
}
