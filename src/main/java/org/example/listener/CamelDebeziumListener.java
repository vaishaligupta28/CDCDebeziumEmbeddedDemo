package org.example.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.kafka.connect.data.Struct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Camel-based listener for Debezium change events.
 * This class processes the CDC events captured by Debezium using Apache Camel routes.
 * 
 * This implementation uses the Camel Debezium PostgreSQL component directly with the syntax
 * from("debezium-postgres:my_connector") to capture and process events.
 * 
 * The component is configured using properties in application.properties with the
 * camel.component.debezium-postgres prefix.
 */
@Slf4j
@Component
public class CamelDebeziumListener extends RouteBuilder {
    @Value("${debezium.listener.type}")
    private String listerType;
    /**
     * Configures the Camel routes.
     * 
     * This method is called by the Camel context when it starts up.
     * It defines the Debezium PostgreSQL component route that directly captures
     * database events using the syntax from("debezium-postgres:my_connector").
     */
    @Override
    public void configure() {
        if(!listerType.equalsIgnoreCase("camel")) {
            log.warn("Skipping CamelDebeziumListener because it is not camel");
            return;
        }
        from("debezium-postgres:my_connector")
                .routeId("debezium-postgres-route")
                .choice()
                .when(header("CamelDebeziumIdentifier").contains("heartbeat"))
                    .log("CDC Heartbeat message received")
                    .stop()
                .otherwise()
                    .process(exchange -> {
                        // First, let's see what we actually have
                        Object body = exchange.getIn().getBody();
                        log.info("[Debug]    Body class: {}", body != null ? body.getClass().getName() : "null");
                        log.info("[Debug] Body content: {}", body);

                        // Extract all the CDC event parts correctly
                        String operation = exchange.getIn().getHeader("CamelDebeziumOperation", String.class);
                        Struct afterData = exchange.getIn().getBody(Struct.class);
                        Struct beforeData = exchange.getIn().getHeader("CamelDebeziumBefore", Struct.class);
                        Object sourceInfo = exchange.getIn().getHeader("CamelDebeziumSourceMetadata");
                        Struct keyData = exchange.getIn().getHeader("CamelDebeziumKey", Struct.class);

                        // Log the complete event
                        log.info("[Camel Listener] =========================");
                        log.info("[Camel Listener] Operation: {}", operation);
                        log.info("[Camel Listener] Key: {}", keyData);
                        log.info("[Camel Listener] Before: {}", beforeData);
                        log.info("[Camel Listener] After: {}", afterData);
                        log.info("[Camel Listener] Source: {}", sourceInfo);
                        log.info("[Camel Listener] =========================");
                });
    }
}
