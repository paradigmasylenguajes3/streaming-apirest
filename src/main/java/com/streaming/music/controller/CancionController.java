package com.streaming.music.controller;

import com.streaming.music.dto.CancionDTO;
import com.streaming.music.model.Genero;
import com.streaming.music.service.CancionService;
import com.streaming.music.service.ProcesamientoCancionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/canciones")
@Tag(name = "Canciones", description = "Endpoints para gestión y consulta de canciones")
public class CancionController {

    private final CancionService cancionService;

    public CancionController(CancionService cancionService) {
        this.cancionService = cancionService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las canciones", description = "Retorna el catálogo completo de canciones con información de artista y álbum resuelta")
    @ApiResponse(responseCode = "200", description = "Catálogo completo")
    public ResponseEntity<List<CancionDTO>> findAll() {
        return ResponseEntity.ok(cancionService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener canción por ID", description = "Busca una canción por su UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canción encontrada"),
            @ApiResponse(responseCode = "404", description = "Canción no encontrada")
    })
    public ResponseEntity<CancionDTO> findById(
            @Parameter(description = "UUID de la canción", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(cancionService.findById(id));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar canciones con filtros compuestos",
            description = "Filtra canciones por género, rating mínimo y duración máxima usando Streams API")
    @ApiResponse(responseCode = "200", description = "Resultado del filtrado")
    public ResponseEntity<List<CancionDTO>> buscar(
            @Parameter(description = "Género musical (ROCK, POP, JAZZ, ELECTRÓNICA, CLÁSICA)")
            @RequestParam(required = false) Genero genero,
            @Parameter(description = "Rating mínimo (0.0 - 5.0)")
            @RequestParam(required = false) Integer ratingMinimo,
            @Parameter(description = "Duración máxima en segundos")
            @RequestParam(required = false) Integer duracionMaxima) {
        return ResponseEntity.ok(cancionService.filtrar(genero, ratingMinimo, duracionMaxima));
    }

    @PostMapping("/{id}/reproducir")
    @Operation(summary = "Registrar reproducción",
            description = "Incrementa el contador atómico de reproducciones (AtomicInteger) de forma thread-safe. Diseñado para soportar concurrencia con Virtual Threads.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reproducción registrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Canción no encontrada")
    })
    public ResponseEntity<CancionDTO> reproducir(
            @Parameter(description = "UUID de la canción", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(cancionService.incrementarReproducciones(id));
    }

    @GetMapping("/{id}/recomendaciones/genero")
    @Operation(summary = "Recomendaciones por género (Strategy)",
            description = "Usa la estrategia RecomendacionPorGenero: retorna hasta 10 canciones del mismo género, excluyendo la canción base")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de recomendaciones"),
            @ApiResponse(responseCode = "404", description = "Canción base no encontrada")
    })
    public ResponseEntity<List<CancionDTO>> recomendarPorGenero(
            @Parameter(description = "UUID de la canción base", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(cancionService.recomendarPorGenero(id));
    }

    @GetMapping("/recomendaciones/top5")
    @Operation(summary = "Top 5 global por popularidad (Strategy)",
            description = "Usa la estrategia RecomendacionPorPopularidad: retorna las 5 canciones más reproducidas del catálogo")
    @ApiResponse(responseCode = "200", description = "Top 5 global")
    public ResponseEntity<List<CancionDTO>> recomendarTopGlobal(
            @Parameter(description = "UUID de la canción base (se excluye del resultado)")
            @RequestParam UUID baseId) {
        return ResponseEntity.ok(cancionService.recomendarTopGlobal(baseId));
    }

    @GetMapping("/{id}/recomendaciones/descubrimiento")
    @Operation(summary = "Recomendaciones de descubrimiento (Strategy)",
            description = "Usa la estrategia RecomendacionDescubrimiento: canciones con <1000 reproducciones, <2 años de antigüedad y género diferente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de descubrimiento"),
            @ApiResponse(responseCode = "404", description = "Canción base no encontrada")
    })
    public ResponseEntity<List<CancionDTO>> recomendarDescubrimiento(
            @Parameter(description = "UUID de la canción base", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(cancionService.recomendarDescubrimiento(id));
    }

    @GetMapping("/top10")
    @Operation(summary = "Top 10 más reproducidas",
            description = "Retorna las 10 canciones con mayor número de reproducciones usando Streams API")
    @ApiResponse(responseCode = "200", description = "Top 10")
    public ResponseEntity<List<CancionDTO>> top10() {
        return ResponseEntity.ok(cancionService.top10());
    }

    @GetMapping("/estadisticas/promedio-por-genero")
    @Operation(summary = "Promedio de rating por género",
            description = "Calcula el rating promedio agrupado por género usando Streams API y Collectors")
    @ApiResponse(responseCode = "200", description = "Mapa de género → promedio")
    public ResponseEntity<Map<Genero, Double>> promedioPorGenero() {
        return ResponseEntity.ok(cancionService.promedioPorGenero());
    }

    @GetMapping("/estadisticas/artista-mas-popular")
    @Operation(summary = "Artista más popular (maxBy)",
            description = "Encuentra el artista con mayor suma de reproducciones usando maxBy sobre groupingBy")
    @ApiResponse(responseCode = "200", description = "Estadística del artista más popular")
    public ResponseEntity<ProcesamientoCancionService.ArtistaEstadistica> artistaMasPopular() {
        return ResponseEntity.ok(cancionService.artistaMasPopular());
    }

    @GetMapping("/estadisticas/distribucion-decadas")
    @Operation(summary = "Distribución de canciones por décadas",
            description = "Agrupa las canciones por la década de su fecha de lanzamiento")
    @ApiResponse(responseCode = "200", description = "Mapa de década → cantidad")
    public ResponseEntity<Map<String, Long>> distribucionDecadas() {
        return ResponseEntity.ok(cancionService.distribucionDecadas());
    }

    @GetMapping("/playlist")
    @Operation(summary = "Playlist automática por duración exacta (Mochila/Knapsack)",
            description = "Genera una playlist cuyas canciones suman exactamente los minutos indicados. Usa backtracking recursivo (subset sum). Complejidad O(2^n).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Playlist generada"),
            @ApiResponse(responseCode = "400", description = "Minutos inválidos")
    })
    public ResponseEntity<List<CancionDTO>> playlistExacta(
            @Parameter(description = "Duración total deseada en minutos", required = true, example = "15")
            @RequestParam int minutos) {
        if (minutos <= 0) throw new IllegalArgumentException("Los minutos deben ser mayores a 0");
        return ResponseEntity.ok(cancionService.playlistExacta(minutos));
    }

    @GetMapping("/busqueda-binaria")
    @Operation(summary = "Búsqueda binaria por título",
            description = "Ordena el catálogo alfabéticamente y realiza búsqueda binaria por título exacto. Complejidad O(n log n + log n).")
    @ApiResponse(responseCode = "200", description = "Resultado de la búsqueda binaria")
    public ResponseEntity<List<CancionDTO>> busquedaBinaria(
            @Parameter(description = "Título exacto a buscar", required = true, example = "So What")
            @RequestParam String titulo) {
        return ResponseEntity.ok(cancionService.busquedaBinariaPorTitulo(titulo));
    }

    @GetMapping("/ordenar")
    @Operation(summary = "Ordenamiento personalizado",
            description = "Ordena el catálogo por el criterio especificado: reproducciones, rating, duracion o fecha")
    @ApiResponse(responseCode = "200", description = "Catálogo ordenado")
    public ResponseEntity<List<CancionDTO>> ordenar(
            @Parameter(description = "Criterio: reproducciones, rating, duracion, fecha", required = true, example = "reproducciones")
            @RequestParam String criterio) {
        return ResponseEntity.ok(cancionService.ordenarPersonalizado(criterio));
    }
}
