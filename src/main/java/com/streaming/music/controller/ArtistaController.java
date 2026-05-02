package com.streaming.music.controller;

import com.streaming.music.dto.ArtistaDTO;
import com.streaming.music.dto.CreateArtistaRequest;
import com.streaming.music.dto.UpdateArtistaRequest;
import com.streaming.music.service.ArtistaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/artistas")
@Tag(name = "Artistas", description = "Endpoints para gestión y consulta de artistas")
public class ArtistaController {

    private final ArtistaService artistaService;

    public ArtistaController(ArtistaService artistaService) {
        this.artistaService = artistaService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los artistas")
    @ApiResponse(responseCode = "200", description = "Lista de artistas")
    public ResponseEntity<List<ArtistaDTO>> findAll() {
        return ResponseEntity.ok(artistaService.findAll());
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
        return ResponseEntity.ok(artistaService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo artista",
            description = "Crea un artista con los datos proporcionados. El ID se genera automáticamente. El género debe ser uno de: ROCK, POP, JAZZ, ELECTRÓNICA, CLÁSICA.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Artista creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (género no válido)")
    })
    public ResponseEntity<ArtistaDTO> create(
            @RequestBody @Parameter(description = "Datos del artista a crear", required = true)
            CreateArtistaRequest request) {
        ArtistaDTO created = artistaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar artista existente",
            description = "Reemplaza todos los datos de un artista existente. El género debe ser uno de: ROCK, POP, JAZZ, ELECTRÓNICA, CLÁSICA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Artista actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (género no válido)"),
            @ApiResponse(responseCode = "404", description = "Artista no encontrado")
    })
    public ResponseEntity<ArtistaDTO> update(
            @Parameter(description = "UUID del artista a actualizar", required = true)
            @PathVariable UUID id,
            @RequestBody @Parameter(description = "Nuevos datos del artista", required = true)
            UpdateArtistaRequest request) {
        return ResponseEntity.ok(artistaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar artista")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Artista eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Artista no encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "UUID del artista a eliminar", required = true)
            @PathVariable UUID id) {
        artistaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
