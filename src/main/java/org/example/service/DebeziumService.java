package org.example.service;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * Service for managing the Debezium engine lifecycle.
 */
@Slf4j
@Service
public class DebeziumService {

    private final Executor executor;
    private final Optional<DebeziumEngine<ChangeEvent<String, String>>> debeziumEngine;

    public DebeziumService(@Qualifier("debeziumExecutor") Executor executor,
                           Optional<DebeziumEngine<ChangeEvent<String, String>>> debeziumEngine) {
        this.executor = executor;
        this.debeziumEngine = debeziumEngine;
    }

    /**
     * Starts the Debezium engine when the application starts.
     * 
     * Note: When using the Camel Debezium listener, no Debezium engine is started
     * as the Camel Debezium PostgreSQL component handles CDC events directly.
     */
    @PostConstruct
    public void start() {
        if (debeziumEngine.isPresent()) {
            log.info("Starting Debezium engine...");
            this.executor.execute(debeziumEngine.get());
            log.info("Debezium engine started successfully");
        } else {
            log.info("No Debezium engine to start - Camel Debezium PostgreSQL component handles CDC events directly");
        }
    }

    /**
     * Stops the Debezium engine when the application stops.
     */
    @PreDestroy
    public void stop() {
        if (debeziumEngine.isPresent()) {
            log.info("Stopping Debezium engine...");
            try {
                debeziumEngine.get().close();
                log.info("Debezium engine stopped successfully");
            } catch (IOException e) {
                log.error("Error while stopping Debezium engine", e);
            }
        }
    }
}
