package com.streaming.music.dto;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateAlbumRequest(String titulo, LocalDate fechaLanzamiento, UUID artistaId, UUID productoraId) {}
