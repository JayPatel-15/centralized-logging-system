package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

@RestController
public class LogController {

    List<String> logs = new CopyOnWriteArrayList<>();

    @PostMapping("/ingest")
    public void ingest(@RequestBody String body) {
            logs.add(body);
    }

    @GetMapping("/logs")
    public Flux<String> query(
            @RequestParam(required=false) String username,
            @RequestParam(required=false) Boolean is_blacklisted
    ) {
        Stream<String> s = logs.stream();
        if(username != null)
            s = s.filter(l -> l.contains("\"username\":\""+username+"\""));
        if(is_blacklisted != null)
            s = s.filter(l -> l.contains("\"is.blacklisted\":"+is_blacklisted));
        return Flux.fromStream(s);
    }

    @GetMapping("/metrics")
    public Map<String,Object> metrics() {
        Map<String,Object> m = new HashMap<>();
        m.put("total_logs", logs.size());
        return m;
    }
}
