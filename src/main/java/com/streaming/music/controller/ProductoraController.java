package com.streaming.music.controller;

import com.streaming.music.dto.ProductoraDTO;
import com.streaming.music.exception.CancionNotFoundException;
import com.streaming.music.model.Productora;
import com.streaming.music.repository.ProductoraRepository;
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
@RequestMapping("/api/productoras")
@Tag(name = "Productoras", description = "Endpoints para gestión y consulta de productoras/discográficas")
public class ProductoraController {

    private final ProductoraRepository productoraRepository;

    public ProductoraController(ProductoraRepository productoraRepository) {
        this.productoraRepository = productoraRepository;
    }

    @GetMapping
    @Operation(summary = "Listar todas las productoras")
    @ApiResponse(responseCode = "200", description = "Lista de productoras")
    public ResponseEntity<List<ProductoraDTO>> findAll() {
        List<ProductoraDTO> productoras = productoraRepository.findAll().stream()
                .map(ProductoraDTO::from)
                .toList();
        return ResponseEntity.ok(productoras);
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
        Productora productora = productoraRepository.findById(id)
                .orElseThrow(() -> new CancionNotFoundException(id));
        return ResponseEntity.ok(ProductoraDTO.from(productora));
    }
}
