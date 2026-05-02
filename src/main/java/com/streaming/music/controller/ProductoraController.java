package com.streaming.music.controller;

import com.streaming.music.dto.CreateProductoraRequest;
import com.streaming.music.dto.ProductoraDTO;
import com.streaming.music.dto.UpdateProductoraRequest;
import com.streaming.music.service.ProductoraService;
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
@RequestMapping("/api/productoras")
@Tag(name = "Productoras", description = "Endpoints para gestión y consulta de productoras/discográficas")
public class ProductoraController {

    private final ProductoraService productoraService;

    public ProductoraController(ProductoraService productoraService) {
        this.productoraService = productoraService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las productoras")
    @ApiResponse(responseCode = "200", description = "Lista de productoras")
    public ResponseEntity<List<ProductoraDTO>> findAll() {
        return ResponseEntity.ok(productoraService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener productora por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Productora encontrada"),
            @ApiResponse(responseCode = "404", description = "Productora no encontrada")
    })
    public ResponseEntity<ProductoraDTO> findById(
            @Parameter(description = "UUID de la productora", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(productoraService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva productora",
            description = "Crea una productora/discográfica con los datos proporcionados. El ID se genera automáticamente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Productora creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ProductoraDTO> create(
            @RequestBody @Parameter(description = "Datos de la productora a crear", required = true)
            CreateProductoraRequest request) {
        ProductoraDTO created = productoraService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar productora existente",
            description = "Reemplaza todos los datos de una productora existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Productora actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Productora no encontrada")
    })
    public ResponseEntity<ProductoraDTO> update(
            @Parameter(description = "UUID de la productora a actualizar", required = true)
            @PathVariable UUID id,
            @RequestBody @Parameter(description = "Nuevos datos de la productora", required = true)
            UpdateProductoraRequest request) {
        return ResponseEntity.ok(productoraService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar productora")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Productora eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Productora no encontrada")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "UUID de la productora a eliminar", required = true)
            @PathVariable UUID id) {
        productoraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
