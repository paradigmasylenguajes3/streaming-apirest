package com.streaming.music.service.estrategia;

import com.streaming.music.model.Cancion;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecomendacionPorGenero implements EstrategiaRecomendacion {

    @Override
    public List<Cancion> recomendar(List<Cancion> catalogo, Cancion base) {
        return catalogo.stream()
                .filter(c -> !c.equals(base))
                .filter(c -> c.getGenero() == base.getGenero())
                .sorted((a, b) -> Integer.compare(b.getReproducciones(), a.getReproducciones()))
                .limit(10)
                .toList();
    }
}
