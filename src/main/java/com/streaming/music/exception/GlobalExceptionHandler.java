package com.streaming.music.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CancionNotFoundException.class)
    public ProblemDetail handleCancionNotFound(CancionNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Canción no encontrada");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(ArtistaNotFoundException.class)
    public ProblemDetail handleArtistaNotFound(ArtistaNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Artista no encontrado");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(AlbumNotFoundException.class)
    public ProblemDetail handleAlbumNotFound(AlbumNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Álbum no encontrado");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(ProductoraNotFoundException.class)
    public ProblemDetail handleProductoraNotFound(ProductoraNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Productora no encontrada");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadRequest(IllegalArgumentException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setTitle("Solicitud inválida");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        detail.setTitle("Error interno");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }
}
