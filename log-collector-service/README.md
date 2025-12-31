# Log Collector – Spring WebFlux

A lightweight **reactive log collection service** built using **Spring Boot WebFlux**.
It receives raw Linux `sudo` audit logs, extracts meaningful fields, enriches them with metadata,
and forwards structured events to a downstream ingest service.

---

## Overview

This service acts as a **log forwarder / enrichment layer** in a logging pipeline.

Linux Logs → POST /log → Parse & Enrich → POST /ingest

---

## Key Features

- Fully reactive (non-blocking I/O)
- Regex-based parsing of Linux `sudo` logs
- Blacklisted user detection
- Metadata enrichment
- Forwards events using WebClient
- Easy to extend for production use

---

## Tech Stack

- Java 17+
- Spring Boot 4.x
- Spring WebFlux
- Reactor
- Maven
- JUnit 5

---

## Project Structure

log-collector  
├── src/main/java  
│   └── com/example/demo  
│       ├── CollectorApplication.java  
│       └── controller  
│           └── CollectorController.java  
├── src/test/java  
│   └── com/example/demo  
│       └── controller  
│           └── CollectorControllerTest.java  
├── pom.xml  
└── README.md

---

## API Reference

### POST /log

Accepts a raw Linux sudo log as plain text.

Example request:

<34> myhost sudo: pam_unix(sudo:session): session opened for user root

---

## Parsing Logic

Regex used:

<\d+> (\S+) sudo: .* user (\w+)

Extracted fields:
- hostname
- username

---

## Forwarded Event Payload

{
"timestamp": "2025-01-01T12:00:00Z",
"event.category": "login.audit",
"event.source.type": "linux",
"username": "root",
"hostname": "myhost",
"severity": "INFO",
"raw.message": "<34> myhost sudo: ...",
"is.blacklisted": true
}

---

## Blacklist

Currently hardcoded:

Set<String> blacklist = Set.of("root");

---

## Running the Application

mvn clean spring-boot:run

Application starts at http://localhost:8080

---

## Notes

- /ingest endpoint is assumed to exist
- Configuration should be externalized in production
- Intended for learning and demo purposes