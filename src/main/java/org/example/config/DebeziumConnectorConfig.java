package org.example.config;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.storage.FileOffsetBackingStore;
import org.example.listener.DebeziumListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Configuration class for setting up the Debezium connector.
 */
@Slf4j
@Configuration
public class DebeziumConnectorConfig {

    @Value("${debezium.connector.name}")
    private String connectorName;

    @Value("${debezium.connector.connector.class}")
    private String connectorClass;

    @Value("${debezium.connector.database.hostname}")
    private String databaseHostname;

    @Value("${debezium.connector.database.port}")
    private String databasePort;

    @Value("${debezium.connector.database.user}")
    private String databaseUser;

    @Value("${debezium.connector.database.password}")
    private String databasePassword;

    @Value("${debezium.connector.database.dbname}")
    private String databaseName;

    @Value("${debezium.connector.database.server.name}")
    private String databaseServerName;

    @Value("${debezium.connector.schema.include}")
    private String schemaInclude;

    @Value("${debezium.connector.table.include.list}")
    private String tableIncludeList;

    @Value("${debezium.connector.plugin.name}")
    private String pluginName;

    @Value("${debezium.connector.slot.name}")
    private String slotName;

    @Value("${debezium.connector.publication.name}")
    private String publicationName;

    @Value("${debezium.listener.type}")
    private String listenerType;

    @Value("${debezium.connector.topic-prefix}")
    private String topicPrefix;

    /**
     * Creates a Debezium engine configuration.
     */
    private Properties createConnectorConfiguration() {
        final Properties props = new Properties();
        props.setProperty("name", connectorName);
        props.setProperty("connector.class", connectorClass);
        props.setProperty("offset.storage", FileOffsetBackingStore.class.getName());
        props.setProperty("offset.storage.file.filename", "./offsets.dat");
        props.setProperty("offset.flush.interval.ms", "60000");

        // Database connection details
        props.setProperty("database.hostname", databaseHostname);
        props.setProperty("database.port", databasePort);
        props.setProperty("database.user", databaseUser);
        props.setProperty("database.password", databasePassword);
        props.setProperty("database.dbname", databaseName);
        props.setProperty("database.server.name", databaseServerName);

        // PostgreSQL specific configurations
        props.setProperty("topic.prefix", topicPrefix);
        props.setProperty("schema.include", schemaInclude);
        props.setProperty("table.include.list", tableIncludeList);
        props.setProperty("plugin.name", pluginName);
        props.setProperty("slot.name", slotName);
        props.setProperty("publication.name", publicationName);

        // Additional configurations
        props.setProperty("include.schema.changes", "true");
        props.setProperty("tombstones.on.delete", "false");
        props.setProperty("key.converter", JsonConverter.class.getName());
        props.setProperty("key.converter.schemas.enable", "false");
        props.setProperty("value.converter", JsonConverter.class.getName());
        props.setProperty("value.converter.schemas.enable", "false");

        return props;
    }

    /**
     * Creates a Debezium engine bean.
     * 
     * Note: When using the Camel Debezium listener, no Debezium engine is created
     * as the Camel Debezium PostgreSQL component handles CDC events directly
     * using the properties defined in application.properties with the
     * camel.component.debezium-postgres prefix.
     */
    @Bean
    @ConditionalOnProperty(name = "debezium.listener.type", havingValue = "springboot")
    public DebeziumEngine<ChangeEvent<String, String>> debeziumEngine(
            DebeziumListener springBootListener) throws IOException {

        log.info("Using Spring Boot Debezium listener");
        final Properties props = createConnectorConfiguration();
        return DebeziumEngine.create(Json.class)
                .using(props)
                .notifying(springBootListener::handleChangeEvent)
                .build();
    }

    /**
     * Creates an executor for the Debezium engine.
     */
    @Bean
    public Executor debeziumExecutor() {
        return Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "debezium-executor");
            thread.setDaemon(true);
            return thread;
        });
    }
}
