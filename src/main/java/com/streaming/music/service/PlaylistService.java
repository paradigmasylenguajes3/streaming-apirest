package com.streaming.music.service;

import com.streaming.music.model.Cancion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de generación de playlist automática.
 * Resuelve el problema de la mochila (subset sum) con backtracking recursivo
 * para encontrar un subconjunto de canciones cuya duración total sea exactamente
 * {@code X} minutos.
 *
 * <p><b>Complejidad Big O:</b> O(2^n) en el peor caso (backtracking sin poda).</p>
 */
@Service
public class PlaylistService {

    /**
     * Encuentra una lista de canciones cuya duración total sea exactamente {@code minutosObjetivo}.
     *
     * @param candidatas      lista de canciones disponibles
     * @param minutosObjetivo duración total deseada en minutos
     * @return lista de canciones que suman exactamente los minutos, o lista vacía si no hay solución
     */
    public List<Cancion> playlistExacta(List<Cancion> candidatas, int minutosObjetivo) {
        int segundosObjetivo = minutosObjetivo * 60;
        List<Cancion> resultado = new ArrayList<>();

        backtrack(candidatas, 0, segundosObjetivo, resultado, new ArrayList<>());

        return resultado;
    }

    /**
     * Backtracking recursivo para subset sum.
     *
     * @param canciones catálogo completo
     * @param index     índice actual
     * @param remaining segundos restantes por cubrir
     * @param result    resultado final (se llena si se encuentra solución)
     * @param current   selección actual en construcción
     * @return true si se encontró solución exacta
     */
    private boolean backtrack(List<Cancion> canciones, int index, int remaining,
                              List<Cancion> result, List<Cancion> current) {
        if (remaining == 0) {
            result.addAll(current);
            return true;
        }
        if (remaining < 0 || index >= canciones.size()) {
            return false;
        }

        Cancion c = canciones.get(index);

        // Incluir canción actual
        current.add(c);
        if (backtrack(canciones, index + 1, remaining - c.getDuracionSegundos(), result, current)) {
            return true;
        }
        current.removeLast();

        // Excluir canción actual y avanzar
        return backtrack(canciones, index + 1, remaining, result, current);
    }
}
