package com.streaming.music.service.estrategia;

import java.util.Collections;
import com.streaming.music.model.Cancion;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RecomendadorRandom implements EstrategiaRecomendacion {
    
    @Override
    public List<Cancion> recomendar(List<Cancion> catalogo, Cancion base) {
        Collections.shuffle(catalogo);
        return catalogo.stream()
                .filter(c -> !c.equals(base))
                .limit(5)
                .toList();
    }

    public Cancion recomendarUna(List<Cancion> catalogo, Cancion base) {
        Collections.shuffle(catalogo);
        return catalogo.stream()
                .filter(c -> !c.equals(base))
                .findFirst()
                .orElse(null);
    }
}
