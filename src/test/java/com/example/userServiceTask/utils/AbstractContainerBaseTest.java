package com.example.userServiceTask.utils;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Testcontainers
public abstract class AbstractContainerBaseTest {

    protected static final String REDIS_IMAGE = "redis:7.0-alpine";
    protected static final String MYSQL_IMAGE = "mysql:8.0";
    protected static final int REDIS_PORT = 6379;
    protected static final int MYSQL_PORT = 3306;
    protected static final String MYSQL_DATABASE_NAME = "userdb";
    protected static final String MYSQL_USERNAME = "root";
    protected static final String MYSQL_PASSWORD = "wtpassword";
    protected static final String RESOURCE_PATH = "db/changelog";
    protected static final String INITIAL_SCHEMA = RESOURCE_PATH + "/v1-initial-schema.xml";

    @Container
    @ServiceConnection
    protected static final MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse(MYSQL_IMAGE))
            .withDatabaseName(MYSQL_DATABASE_NAME)
            .withUsername(MYSQL_USERNAME)
            .withPassword(MYSQL_PASSWORD)
            .withExposedPorts(MYSQL_PORT)
            .withReuse(true)
            .withCopyToContainer(
                    MountableFile.forClasspathResource(RESOURCE_PATH),
                    INITIAL_SCHEMA
            );

    @Container
    @ServiceConnection
    protected static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
            .withExposedPorts(REDIS_PORT)
            .withReuse(true);

}