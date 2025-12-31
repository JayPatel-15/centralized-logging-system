package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class CollectorController {

    private final WebClient client;

    private final Set<String> blacklist = Set.of("root");
    private final Pattern p =
            Pattern.compile("<\\d+> (\\S+) sudo: .* user (\\w+)");

    public CollectorController(WebClient client) {
        this.client = client;
    }

    @PostMapping("/log")
    public Mono<Void> collect(@RequestBody String body) {

        Matcher m = p.matcher(body);
        String host = "unknown";
        String user = "unknown";

        if (m.find()) {
            host = m.group(1);
            user = m.group(2);
        }

        boolean isBlacklisted = blacklist.contains(user);

        Map<String, Object> payload = new HashMap<>();
        payload.put("timestamp", Instant.now());
        payload.put("event.category", "login.audit");
        payload.put("event.source.type", "linux");
        payload.put("username", user);
        payload.put("hostname", host);
        payload.put("severity", "INFO");
        payload.put("raw.message", body);
        payload.put("is.blacklisted", isBlacklisted);

        return client.post()
                .uri("/ingest")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Void.class);
    }
}





//package com.example.demo.controller;
//
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.time.Instant;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.regex.*;
//
//@RestController
//public class CollectorController {
//
//    WebClient client = WebClient.create("http://localhost:8080");
//    Set<String> blacklist = Set.of("root");
//
//    Pattern p = Pattern.compile("<\\d+> (\\S+) sudo: .* user (\\w+)");
//
//    @PostMapping("/log")
//    public Mono<Void> collect(@RequestBody String body) {
//
//        Matcher m = p.matcher(body);
//        String host = "unknown";
//        String user = "unknown";
//
//        if(m.find()) {
//            host = m.group(1);
//            user = m.group(2);
//        }
//
//        boolean isBlacklisted = blacklist.contains(user);
//
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("timestamp", Instant.now());
//        payload.put("event.category", "login.audit");
//        payload.put("event.source.type", "linux");
//        payload.put("username", user);
//        payload.put("hostname", host);
//        payload.put("severity", "INFO");
//        payload.put("raw.message", body);
//        payload.put("is.blacklisted", isBlacklisted);
//
//        return client.post()
//                .uri("/ingest")
//                .bodyValue(payload)
//                .retrieve()
//                .bodyToMono(Void.class);
//    }
//}
