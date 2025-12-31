package com.example.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectorControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private CollectorController collectorController;

    @BeforeEach
    void setup() {
        webTestClient = WebTestClient
                .bindToController(collectorController)
                .build();
    }

    private void mockWebClient() {
        WebClient.RequestBodyUriSpec request =
                mock(WebClient.RequestBodyUriSpec.class);

        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersSpec headers =
                mock(WebClient.RequestHeadersSpec.class);

        WebClient.ResponseSpec response =
                mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(request);
        when(request.uri("/ingest")).thenReturn(request);
        when(request.bodyValue(any())).thenReturn(headers);
        when(headers.retrieve()).thenReturn(response);
        when(response.bodyToMono(Void.class)).thenReturn(Mono.empty());
    }

    @Test
    void testCollect_blacklistedUser() {
        mockWebClient();

        String log =
                "<12345> myhost sudo: pam_unix(sudo:session): session opened for user root by (uid=0)";

        webTestClient.post()
                .uri("/log")
                .bodyValue(log)
                .exchange()
                .expectStatus().isOk();

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(webClient.post()
                .uri("/ingest")).bodyValue(captor.capture());

        String payload = captor.getValue().toString();
        assert payload.contains("is.blacklisted=true");
    }

    @Test
    void testCollect_nonBlacklistedUser() {
        mockWebClient();

        String log =
                "<12345> myhost sudo: pam_unix(sudo:session): session opened for user john by (uid=1000)";

        webTestClient.post()
                .uri("/log")
                .bodyValue(log)
                .exchange()
                .expectStatus().isOk();

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(webClient.post()
                .uri("/ingest")).bodyValue(captor.capture());

        String payload = captor.getValue().toString();
        assert payload.contains("is.blacklisted=false");
    }

    @Test
    void testCollect_endpointReturns200() {
        mockWebClient();

        webTestClient.post()
                .uri("/log")
                .bodyValue("invalid log")
                .exchange()
                .expectStatus().isOk();
    }
}
