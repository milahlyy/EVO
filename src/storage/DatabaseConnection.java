package storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:55432/evo_db";
    private static final String DEFAULT_USER = "evo_user";
    private static final String DEFAULT_PASSWORD = "evo_pass";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        loadDriver();
        return DriverManager.getConnection(getUrl(), getUser(), getPassword());
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("PostgreSQL JDBC driver tidak ditemukan di folder lib.", e);
        }
    }

    private static String getUrl() {
        return getEnvOrDefault("EVO_DB_URL", DEFAULT_URL);
    }

    private static String getUser() {
        return getEnvOrDefault("EVO_DB_USER", DEFAULT_USER);
    }

    private static String getPassword() {
        return getEnvOrDefault("EVO_DB_PASSWORD", DEFAULT_PASSWORD);
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value;
    }
}
