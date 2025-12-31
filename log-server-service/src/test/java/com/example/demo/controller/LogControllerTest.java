package com.example.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    private WebTestClient webTestClient;
    private LogController logController;

    @BeforeEach
    void setup() {
        logController = new LogController();
        webTestClient = WebTestClient
                .bindToController(logController)
                .build();
    }

    @Test
    void ingest_shouldStoreLog() {
        String log = "{\"username\":\"john\",\"is.blacklisted\":false}";

        webTestClient.post()
                .uri("/ingest")
                .body(Mono.just(log), String.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void logs_shouldFilterByUsername() {
        webTestClient.post().uri("/ingest")
                .bodyValue("{\"username\":\"john\",\"is.blacklisted\":false}")
                .exchange();

        webTestClient.post().uri("/ingest")
                .bodyValue("{\"username\":\"jane\",\"is.blacklisted\":true}")
                .exchange();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/logs")
                        .queryParam("username", "john")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(String.class)
                .hasSize(1);
    }

    @Test
    void logs_shouldFilterByBlacklisted() {
        webTestClient.post().uri("/ingest")
                .bodyValue("{\"username\":\"john\",\"is.blacklisted\":false}")
                .exchange();

        webTestClient.post().uri("/ingest")
                .bodyValue("{\"username\":\"jane\",\"is.blacklisted\":true}")
                .exchange();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/logs")
                        .queryParam("is_blacklisted", true)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(String.class)
                .hasSize(1);
    }

    @Test
    void metrics_shouldReturnTotalLogs() {
        webTestClient.post().uri("/ingest")
                .bodyValue("log1")
                .exchange();

        webTestClient.post().uri("/ingest")
                .bodyValue("log2")
                .exchange();

        webTestClient.get()
                .uri("/metrics")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.total_logs").isEqualTo(2);
    }
}
