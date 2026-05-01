package com.streaming.music.service.estrategia;

import com.streaming.music.model.Cancion;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class RecomendacionPorPopularidad implements EstrategiaRecomendacion {

    @Override
    public List<Cancion> recomendar(List<Cancion> catalogo, Cancion base) {
        return catalogo.stream()
                .filter(c -> !c.equals(base))
                .sorted(Comparator.comparingInt(Cancion::getReproducciones).reversed())
                .limit(5)
                .toList();
    }
}
