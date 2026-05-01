package com.streaming.music.controller;

import com.streaming.music.dto.AlbumDTO;
import com.streaming.music.exception.CancionNotFoundException;
import com.streaming.music.model.Album;
import com.streaming.music.repository.AlbumRepository;
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
@RequestMapping("/api/albumes")
@Tag(name = "Álbumes", description = "Endpoints para gestión y consulta de álbumes")
public class AlbumController {

    private final AlbumRepository albumRepository;

    public AlbumController(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @GetMapping
    @Operation(summary = "Listar todos los álbumes")
    @ApiResponse(responseCode = "200", description = "Lista de álbumes")
    public ResponseEntity<List<AlbumDTO>> findAll() {
        List<AlbumDTO> albumes = albumRepository.findAll().stream()
                .map(AlbumDTO::from)
                .toList();
        return ResponseEntity.ok(albumes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener álbum por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Álbum encontrado"),
            @ApiResponse(responseCode = "404", description = "Álbum no encontrado")
    })
    public ResponseEntity<AlbumDTO> findById(
            @Parameter(description = "UUID del álbum", required = true)
            @PathVariable UUID id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new CancionNotFoundException(id));
        return ResponseEntity.ok(AlbumDTO.from(album));
    }
}
