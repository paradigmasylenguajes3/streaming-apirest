package com.streaming.music.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CreateAlbumRequest(String titulo, LocalDate fechaLanzamiento, UUID artistaId, UUID productoraId) {}
