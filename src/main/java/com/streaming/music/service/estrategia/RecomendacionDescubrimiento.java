package com.streaming.music.service.estrategia;

import com.streaming.music.model.Cancion;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class RecomendacionDescubrimiento implements EstrategiaRecomendacion {

    @Override
    public List<Cancion> recomendar(List<Cancion> catalogo, Cancion base) {
        return catalogo.stream()
                .filter(c -> !c.equals(base))
                .filter(c -> c.getReproducciones() < 1000)
                .filter(c -> ChronoUnit.YEARS.between(c.getFechaLanzamiento(), LocalDate.now()) < 2)
                .filter(c -> c.getGenero() != base.getGenero())
                .limit(10)
                .toList();
    }
}
