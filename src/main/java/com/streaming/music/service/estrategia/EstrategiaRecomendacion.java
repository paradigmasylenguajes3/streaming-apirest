package com.streaming.music.service.estrategia;

import com.streaming.music.model.Cancion;

import java.util.List;

public interface EstrategiaRecomendacion {
    List<Cancion> recomendar(List<Cancion> catalogo, Cancion base);
}
