package com.streaming.music.config;

import com.streaming.music.model.*;
import com.streaming.music.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer {

    private final ArtistaRepository artistaRepository;
    private final AlbumRepository albumRepository;
    private final ProductoraRepository productoraRepository;
    private final CancionRepository cancionRepository;

    public DataInitializer(ArtistaRepository artistaRepository,
                           AlbumRepository albumRepository,
                           ProductoraRepository productoraRepository,
                           CancionRepository cancionRepository) {
        this.artistaRepository = artistaRepository;
        this.albumRepository = albumRepository;
        this.productoraRepository = productoraRepository;
        this.cancionRepository = cancionRepository;
    }

    @PostConstruct
    public void init() {
        // 1. Productoras
        Productora warner = productoraRepository.save(new Productora(null, "Warner Music", "EE.UU."));
        Productora sony = productoraRepository.save(new Productora(null, "Sony Music", "EE.UU."));
        Productora universal = productoraRepository.save(new Productora(null, "Universal Music", "EE.UU."));
        Productora indie = productoraRepository.save(new Productora(null, "Independiente", "Argentina"));

        // 2. Artistas
        Artista artista1 = artistaRepository.save(new Artista(null, "Radiohead", Genero.ROCK, "Banda británica de rock alternativo."));
        Artista artista2 = artistaRepository.save(new Artista(null, "Daft Punk", Genero.ELECTRÓNICA, "Dúo francés de música electrónica."));
        Artista artista3 = artistaRepository.save(new Artista(null, "Miles Davis", Genero.JAZZ, "Legendario trompetista de jazz estadounidense."));
        Artista artista4 = artistaRepository.save(new Artista(null, "Taylor Swift", Genero.POP, "Cantautora estadounidense de pop."));
        Artista artista5 = artistaRepository.save(new Artista(null, "Beethoven", Genero.CLÁSICA, "Compositor alemán del clasicismo y romanticismo."));
        Artista artista6 = artistaRepository.save(new Artista(null, "Nirvana", Genero.ROCK, "Banda estadounidense de grunge."));
        Artista artista7 = artistaRepository.save(new Artista(null, "Kraftwerk", Genero.ELECTRÓNICA, "Pioneros alemanes de la música electrónica."));
        Artista artista8 = artistaRepository.save(new Artista(null, "Billie Eilish", Genero.POP, "Cantante y compositora estadounidense."));
        Artista artista9 = artistaRepository.save(new Artista(null, "John Coltrane", Genero.JAZZ, "Saxofonista y compositor de jazz estadounidense."));
        Artista artista10 = artistaRepository.save(new Artista(null, "Mozart", Genero.CLÁSICA, "Compositor austríaco del período clásico."));

        // 3. Álbumes (con referencias a objetos Artista y Productora)
        Album album1 = albumRepository.save(new Album(null, "OK Computer", LocalDate.of(1997, 6, 16), artista1, warner));
        Album album2 = albumRepository.save(new Album(null, "Discovery", LocalDate.of(2001, 3, 12), artista2, sony));
        Album album3 = albumRepository.save(new Album(null, "Kind of Blue", LocalDate.of(1959, 8, 17), artista3, universal));
        Album album4 = albumRepository.save(new Album(null, "1989", LocalDate.of(2014, 10, 27), artista4, universal));
        Album album5 = albumRepository.save(new Album(null, "Symphony No. 9", LocalDate.of(1824, 5, 7), artista5, indie));
        Album album6 = albumRepository.save(new Album(null, "Nevermind", LocalDate.of(1991, 9, 24), artista6, warner));
        Album album7 = albumRepository.save(new Album(null, "Autobahn", LocalDate.of(1974, 11, 1), artista7, sony));
        Album album8 = albumRepository.save(new Album(null, "Happier Than Ever", LocalDate.of(2021, 7, 30), artista8, universal));
        Album album9 = albumRepository.save(new Album(null, "A Love Supreme", LocalDate.of(1965, 2, 1), artista9, indie));
        Album album10 = albumRepository.save(new Album(null, "Requiem", LocalDate.of(1791, 12, 5), artista10, indie));

        // 4. Canciones (con referencias a objetos Artista y Album)
        List<Cancion> seed = List.of(
            new Cancion("Paranoid Android", Genero.ROCK, 396, 4.8, LocalDate.of(1997, 6, 16), artista1, album1),
            new Cancion("Karma Police", Genero.ROCK, 264, 4.6, LocalDate.of(1997, 6, 16), artista1, album1),
            new Cancion("No Surprises", Genero.ROCK, 228, 4.5, LocalDate.of(1997, 6, 16), artista1, album1),
            new Cancion("One More Time", Genero.ELECTRÓNICA, 320, 4.9, LocalDate.of(2001, 3, 12), artista2, album2),
            new Cancion("Harder Better Faster Stronger", Genero.ELECTRÓNICA, 225, 4.7, LocalDate.of(2001, 3, 12), artista2, album2),
            new Cancion("Digital Love", Genero.ELECTRÓNICA, 300, 4.5, LocalDate.of(2001, 3, 12), artista2, album2),
            new Cancion("So What", Genero.JAZZ, 573, 4.9, LocalDate.of(1959, 8, 17), artista3, album3),
            new Cancion("Freddie Freeloader", Genero.JAZZ, 594, 4.6, LocalDate.of(1959, 8, 17), artista3, album3),
            new Cancion("Blue in Green", Genero.JAZZ, 330, 4.7, LocalDate.of(1959, 8, 17), artista3, album3),
            new Cancion("Shake It Off", Genero.POP, 219, 4.3, LocalDate.of(2014, 10, 27), artista4, album4),
            new Cancion("Blank Space", Genero.POP, 231, 4.4, LocalDate.of(2014, 10, 27), artista4, album4),
            new Cancion("Style", Genero.POP, 230, 4.5, LocalDate.of(2014, 10, 27), artista4, album4),
            new Cancion("Ode to Joy", Genero.CLÁSICA, 1440, 5.0, LocalDate.of(1824, 5, 7), artista5, album5),
            new Cancion("Molto vivace", Genero.CLÁSICA, 870, 4.8, LocalDate.of(1824, 5, 7), artista5, album5),
            new Cancion("Smells Like Teen Spirit", Genero.ROCK, 301, 4.9, LocalDate.of(1991, 9, 24), artista6, album6),
            new Cancion("Come As You Are", Genero.ROCK, 219, 4.7, LocalDate.of(1991, 9, 24), artista6, album6),
            new Cancion("Lithium", Genero.ROCK, 258, 4.5, LocalDate.of(1991, 9, 24), artista6, album6),
            new Cancion("Autobahn", Genero.ELECTRÓNICA, 1368, 4.6, LocalDate.of(1974, 11, 1), artista7, album7),
            new Cancion("Kometenmelodie 2", Genero.ELECTRÓNICA, 330, 4.2, LocalDate.of(1974, 11, 1), artista7, album7),
            new Cancion("Happier Than Ever", Genero.POP, 298, 4.4, LocalDate.of(2021, 7, 30), artista8, album8),
            new Cancion("Your Power", Genero.POP, 245, 4.3, LocalDate.of(2021, 7, 30), artista8, album8),
            new Cancion("Acknowledgement", Genero.JAZZ, 465, 4.8, LocalDate.of(1965, 2, 1), artista9, album9),
            new Cancion("Resolution", Genero.JAZZ, 441, 4.7, LocalDate.of(1965, 2, 1), artista9, album9),
            new Cancion("Requiem - Introitus", Genero.CLÁSICA, 402, 4.9, LocalDate.of(1791, 12, 5), artista10, album10),
            new Cancion("Dies Irae", Genero.CLÁSICA, 180, 4.8, LocalDate.of(1791, 12, 5), artista10, album10)
        );

        seed.forEach(cancionRepository::save);

        // Pre-populate reproducciones for distribution testing
        cancionRepository.findById(seed.get(0).getId()).ifPresent(c -> {
            for (int i = 0; i < 5000; i++) c.incrementarReproducciones();
        });
        cancionRepository.findById(seed.get(3).getId()).ifPresent(c -> {
            for (int i = 0; i < 4500; i++) c.incrementarReproducciones();
        });
        cancionRepository.findById(seed.get(6).getId()).ifPresent(c -> {
            for (int i = 0; i < 3000; i++) c.incrementarReproducciones();
        });
        cancionRepository.findById(seed.get(14).getId()).ifPresent(c -> {
            for (int i = 0; i < 8000; i++) c.incrementarReproducciones();
        });
        cancionRepository.findById(seed.get(12).getId()).ifPresent(c -> {
            for (int i = 0; i < 1500; i++) c.incrementarReproducciones();
        });
    }
}
