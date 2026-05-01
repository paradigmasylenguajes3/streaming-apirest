package com.streaming.music;

import com.streaming.music.model.Cancion;
import com.streaming.music.repository.CancionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Prueba de concurrencia con Virtual Threads")
class ConcurrencyVirtualThreadsTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CancionRepository cancionRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/canciones";
    }

    @Test
    @DisplayName("reproducir_ConVirtualThreads_AtomicIntegerConsistency")
    void reproducir_ConVirtualThreads_AtomicIntegerConsistency() throws Exception {
        List<Cancion> todas = cancionRepository.findAll();
        assertTrue(todas.size() > 0, "Debe haber canciones inicializadas");
        Cancion target = todas.get(0);
        String url = baseUrl + "/" + target.getId() + "/reproducir";

        int initialReproducciones = target.getReproducciones();
        int concurrentRequests = 5000;

        CountDownLatch latch = new CountDownLatch(concurrentRequests);
        AtomicInteger successfulCalls = new AtomicInteger(0);
        AtomicInteger failedCalls = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < concurrentRequests; i++) {
                executor.submit(() -> {
                    try {
                        ResponseEntity<String> response = restTemplate.exchange(
                                url,
                                HttpMethod.POST,
                                HttpEntity.EMPTY,
                                String.class);
                        if (response.getStatusCode().is2xxSuccessful()) {
                            successfulCalls.incrementAndGet();
                        } else {
                            failedCalls.incrementAndGet();
                        }
                    } catch (Exception e) {
                        failedCalls.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        boolean completed = latch.await(30, TimeUnit.SECONDS);
        long duration = System.currentTimeMillis() - startTime;

        assertTrue(completed, "Todas las peticiones deben completarse en 30s");

        Cancion updated = cancionRepository.findById(target.getId()).orElseThrow();
        int expectedReproducciones = initialReproducciones + successfulCalls.get();
        int actualReproducciones = updated.getReproducciones();

        System.out.printf("=== Resultados de Concurrencia ===%n");
        System.out.printf("Peticiones concurrentes: %d%n", concurrentRequests);
        System.out.printf("Exitosas: %d | Fallidas: %d%n", successfulCalls.get(), failedCalls.get());
        System.out.printf("Duración total: %d ms%n", duration);
        System.out.printf("Reproducciones iniciales: %d%n", initialReproducciones);
        System.out.printf("Reproducciones esperadas: %d%n", expectedReproducciones);
        System.out.printf("Reproducciones actuales: %d%n", actualReproducciones);
        System.out.printf("Consistencia AtomicInteger: %s%n",
                expectedReproducciones == actualReproducciones ? "OK" : "FALLO");

        assertEquals(expectedReproducciones, actualReproducciones,
                "El AtomicInteger debe mantener consistencia bajo concurrencia con Virtual Threads");
        assertEquals(0, failedCalls.get(),
                "Todas las peticiones deben ser exitosas");
    }
}
