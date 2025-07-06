package org.example.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.engine.ChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Listener for Debezium change events.
 * This class processes the CDC events captured by Debezium.
 */
@Slf4j
@Component
public class DebeziumListener {
    /**
     * Handles a change event from Debezium.
     *
     * @param event The change event containing the CDC data.
     */
    public void handleChangeEvent(ChangeEvent<String, String> event) {
        log.info("[Spring Boot CDC Listener]Received change event: {}", event);
    }
}
