package com.jobmatch.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        logDatabaseConnectionResult();
        SpringApplication.run(BackendApplication.class, args);
    }

    private static void logDatabaseConnectionResult() {
        String url = System.getenv("DATABASE_URL");
        String username = System.getenv("SPRING_DATASOURCE_USERNAME");
        String password = System.getenv("SPRING_DATASOURCE_PASSWORD");
        if (url == null || url.isBlank()) {
            System.err.println("DATABASE_DIAGNOSTIC: no DATABASE_URL configured");
            return;
        }
        try (Connection ignored = (username == null || username.isBlank())
                ? DriverManager.getConnection(url)
                : DriverManager.getConnection(url, username, password)) {
            System.err.println("DATABASE_DIAGNOSTIC: PostgreSQL connection succeeded");
        } catch (SQLException exception) {
            System.err.println("DATABASE_DIAGNOSTIC: SQLState=" + exception.getSQLState()
                    + ", message=" + exception.getMessage());
        }
    }

}
