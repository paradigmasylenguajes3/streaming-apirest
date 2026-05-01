package com.streaming.music.service;

import com.streaming.music.model.Cancion;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class BusquedaService {

    /**
     * Búsqueda binaria por título sobre una lista ordenada.
     * Complejidad Big O: O(n log n) por el ordenamiento + O(log n) búsqueda.
     *
     * @param canciones lista de canciones
     * @param titulo    título exacto a buscar
     * @return canción encontrada o vacío
     */
    public Optional<Cancion> busquedaBinariaPorTitulo(List<Cancion> canciones, String titulo) {
        List<Cancion> ordenadas = canciones.stream()
                .sorted(Comparator.comparing(Cancion::getTitulo, String.CASE_INSENSITIVE_ORDER))
                .toList();

        int left = 0;
        int right = ordenadas.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            String tituloMid = ordenadas.get(mid).getTitulo();
            int cmp = tituloMid.compareToIgnoreCase(titulo);

            if (cmp == 0) {
                return Optional.of(ordenadas.get(mid));
            } else if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return Optional.empty();
    }

    /**
     * Ordenamiento personalizado por múltiples criterios.
     *
     * @param canciones lista a ordenar
     * @param criterio  criterio: "reproducciones", "rating", "duracion", "fecha"
     * @return lista ordenada descendentemente
     */
    public List<Cancion> ordenarPersonalizado(List<Cancion> canciones, String criterio) {
        Comparator<Cancion> comparator = switch (criterio.toLowerCase()) {
            case "reproducciones" -> Comparator.comparingInt(Cancion::getReproducciones).reversed();
            case "rating" -> Comparator.comparingDouble(Cancion::getRating).reversed();
            case "duracion" -> Comparator.comparingInt(Cancion::getDuracionSegundos);
            case "fecha" -> Comparator.comparing(Cancion::getFechaLanzamiento).reversed();
            default -> throw new IllegalArgumentException("Criterio de ordenamiento no válido: " + criterio);
        };

        return canciones.stream().sorted(comparator).toList();
    }

    /**
     * Búsqueda lineal con predicados múltiples combinados con AND.
     *
     * @param canciones  catálogo
     * @param predicados lista de condiciones (se aplican con AND)
     * @return canciones que cumplen todos los predicados
     */
    public List<Cancion> busquedaConPredicados(List<Cancion> canciones, List<Predicate<Cancion>> predicados) {
        Predicate<Cancion> combinado = predicados.stream()
                .reduce(Predicate::and)
                .orElse(c -> true);

        return canciones.stream()
                .filter(combinado)
                .toList();
    }
}
