package com.onlineshopping.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;

import java.util.Collections;
import java.util.List;

/**
 * Cassandra configuration — extends {@link AbstractCassandraConfiguration} so
 * Spring Data Cassandra uses THIS class exclusively for all Cassandra wiring.
 * {@code @EnableCassandraRepositories} lives in {@link com.onlineshopping.OnlineShoppingApplication}.
 *
 * <p>Spring Boot's own {@code CassandraAutoConfiguration} is excluded in
 * {@code application.properties} to avoid creating a duplicate {@code CqlSession}
 * bean:
 * <pre>
 *   spring.autoconfigure.exclude=\
 *     org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration
 * </pre>
 *
 * <p>Key behaviours:
 * <ul>
 *   <li>Auto-creates the keyspace {@code online_shopping} on first start.</li>
 *   <li>Schema action = {@code CREATE_IF_NOT_EXISTS}: tables are created from
 *       {@code @Table} entity classes if they don't already exist.</li>
 *   <li>Entity scan limited to {@code com.onlineshopping.entity.cassandra}.</li>
 * </ul>
 */
@Slf4j
@Configuration
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Value("${spring.cassandra.keyspace-name:online_shopping}")
    private String keyspaceName;

    @Value("${spring.cassandra.contact-points:127.0.0.1}")
    private String contactPoints;

    @Value("${spring.cassandra.port:9042}")
    private int port;

    @Value("${spring.cassandra.local-datacenter:datacenter1}")
    private String localDatacenter;

    // ── AbstractCassandraConfiguration overrides ───────────────────────────

    @Override
    protected String getKeyspaceName() {
        return keyspaceName;
    }

    @Override
    protected String getContactPoints() {
        return contactPoints;
    }

    @Override
    protected int getPort() {
        return port;
    }

    @Override
    protected String getLocalDataCenter() {
        return localDatacenter;
    }

    /**
     * CREATE_IF_NOT_EXISTS: Spring Data Cassandra generates DDL to create any
     * missing tables declared with {@code @Table} on startup.
     */
    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    /**
     * Restricts entity scanning to the Cassandra entity package only.
     * Prevents Spring Data from accidentally picking up MongoDB documents.
     */
    @Override
    public String[] getEntityBasePackages() {
        return new String[]{"com.onlineshopping.entity.cassandra"};
    }

    /**
     * Auto-create the keyspace on first startup.
     * Uses {@code SimpleStrategy} with {@code replication_factor=1} — suitable
     * for a single-node local Cassandra instance (Docker).
     *
     * <p>For production clusters, change to {@code NetworkTopologyStrategy}:
     * <pre>
     *   .withNetworkReplication(Map.of("datacenter1", 3))
     * </pre>
     */
    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        log.info("Ensuring Cassandra keyspace '{}' exists …", keyspaceName);
        CreateKeyspaceSpecification spec = CreateKeyspaceSpecification
                .createKeyspace(keyspaceName)
                .ifNotExists()
                .with(KeyspaceOption.DURABLE_WRITES, true)
                .withSimpleReplication(1);
        return Collections.singletonList(spec);
    }
}
