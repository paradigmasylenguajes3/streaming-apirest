package com.streaming.music.service;

import com.streaming.music.dto.CreateProductoraRequest;
import com.streaming.music.dto.ProductoraDTO;
import com.streaming.music.dto.UpdateProductoraRequest;
import com.streaming.music.exception.ProductoraNotFoundException;
import com.streaming.music.model.Productora;
import com.streaming.music.repository.ProductoraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductoraService {

    private final ProductoraRepository productoraRepository;

    public ProductoraService(ProductoraRepository productoraRepository) {
        this.productoraRepository = productoraRepository;
    }

    public List<ProductoraDTO> findAll() {
        return productoraRepository.findAll().stream()
                .map(ProductoraDTO::from)
                .toList();
    }

    public ProductoraDTO findById(UUID id) {
        Productora productora = productoraRepository.findById(id)
                .orElseThrow(() -> new ProductoraNotFoundException(id));
        return ProductoraDTO.from(productora);
    }

    public ProductoraDTO create(CreateProductoraRequest request) {
        Productora productora = new Productora(null, request.nombre(), request.pais());
        Productora saved = productoraRepository.save(productora);
        return ProductoraDTO.from(saved);
    }

    public ProductoraDTO update(UUID id, UpdateProductoraRequest request) {
        productoraRepository.findById(id)
                .orElseThrow(() -> new ProductoraNotFoundException(id));
        Productora updated = new Productora(id, request.nombre(), request.pais());
        productoraRepository.save(updated);
        return ProductoraDTO.from(updated);
    }

    public void delete(UUID id) {
        if (!productoraRepository.existsById(id)) {
            throw new ProductoraNotFoundException(id);
        }
        productoraRepository.deleteById(id);
    }
}
