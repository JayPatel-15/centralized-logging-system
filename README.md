# Centralized Logging System

This repository contains a centralized logging system with multiple services.

## Services

### 1. Client Linux Service
- Description: Collects logs from Linux clients and sends them to the log collector.
- Directory: `client-linux-service/`

### 2. Log Collector Service
- Description: Receives logs from clients and processes or forwards them.
- Directory: `log-collector-service/`

### 3. Log Server Service
- Description: Stores logs and provides visualization or querying.
- Directory: `log-server-service/`

> Note: This README provides an overview of all services included in this centralized logging system.

## How to Run

1. Start `log-server-service`.
2. Start `log-collector-service`.
3. Run `client-linux-service` on clients.
