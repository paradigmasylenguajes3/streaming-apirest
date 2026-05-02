package com.streaming.music.controller;

import com.streaming.music.dto.AlbumDTO;
import com.streaming.music.dto.CreateAlbumRequest;
import com.streaming.music.dto.UpdateAlbumRequest;
import com.streaming.music.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/albumes")
@Tag(name = "Álbumes", description = "Endpoints para gestión y consulta de álbumes")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los álbumes")
    @ApiResponse(responseCode = "200", description = "Lista de álbumes")
    public ResponseEntity<List<AlbumDTO>> findAll() {
        return ResponseEntity.ok(albumService.findAll());
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
        return ResponseEntity.ok(albumService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo álbum",
            description = "Crea un álbum con los datos proporcionados. El ID se genera automáticamente. Valida que el artista y la productora referenciados existan.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Álbum creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (artista o productora no existen)")
    })
    public ResponseEntity<AlbumDTO> create(
            @RequestBody @Parameter(description = "Datos del álbum a crear", required = true)
            CreateAlbumRequest request) {
        AlbumDTO created = albumService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar álbum existente",
            description = "Reemplaza todos los datos de un álbum existente. Valida que el artista y la productora referenciados existan.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Álbum actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (artista o productora no existen)"),
            @ApiResponse(responseCode = "404", description = "Álbum no encontrado")
    })
    public ResponseEntity<AlbumDTO> update(
            @Parameter(description = "UUID del álbum a actualizar", required = true)
            @PathVariable UUID id,
            @RequestBody @Parameter(description = "Nuevos datos del álbum", required = true)
            UpdateAlbumRequest request) {
        return ResponseEntity.ok(albumService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar álbum")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Álbum eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Álbum no encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "UUID del álbum a eliminar", required = true)
            @PathVariable UUID id) {
        albumService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
