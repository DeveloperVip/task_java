package com.hrmanagement.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Properties;

/**
 * Database utility class for handling JDBC connections
 */
public class Database {
    private static final String DB_PROPERTIES_FILE = "db.properties";
    private static final String DB_INIT_SCRIPT_FILE = "db_init.sql";

    /**
     * Get database connection
     * 
     * @return Connection to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();

        try (InputStream is = Database.class.getClassLoader().getResourceAsStream(DB_PROPERTIES_FILE)) {
            if (is == null) {
                throw new IOException("Could not find " + DB_PROPERTIES_FILE);
            }
            props.load(is);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            return DriverManager.getConnection(url, user, password);
        } catch (IOException e) {
            throw new SQLException("Could not load database properties", e);
        }
    }

    /**
     * Closes JDBC resources silently
     * 
     * @param connection Database connection
     * @param statement  SQL statement
     * @param resultSet  Result set
     */
    public static void close(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing Statement: " + e.getMessage());
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing Connection: " + e.getMessage());
            }
        }
    }

    /**
     * Initialize database by executing SQL script
     */
    public static void initializeDatabase() {
        Connection conn = null;
        Statement stmt = null;

        try {
            // First, try to connect to MySQL server (without specifying a database)
            Properties props = getDatabaseProperties();
            if (props == null) {
                System.err.println("Could not load database properties");
                return;
            }

            // Try to create a direct connection to the hr_system database first
            try {
                conn = getConnection();
                System.out.println("Successfully connected to hr_system database. Executing initialization script...");
                executeSqlScript(conn);
                return;
            } catch (SQLException e) {
                // If we can't connect directly to hr_system, try connecting to MySQL server
                System.out
                        .println("Could not connect directly to hr_system database. Trying to create the database...");
            }

            // Get the base URL without the database name
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            // Extract base URL for MySQL connection (without database name)
            String baseUrl = extractBaseUrl(url);
            if (baseUrl != null) {
                conn = DriverManager.getConnection(baseUrl, user, password);
                executeSqlScript(conn);
            } else {
                System.err.println("Could not determine base database URL.");
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        } finally {
            close(conn, stmt, null);
        }
    }

    /**
     * Get database properties from properties file
     * 
     * @return Properties object containing database configuration or null if error
     */
    private static Properties getDatabaseProperties() {
        try (InputStream is = Database.class.getClassLoader().getResourceAsStream(DB_PROPERTIES_FILE)) {
            if (is == null) {
                System.err.println("Could not find " + DB_PROPERTIES_FILE);
                return null;
            }

            Properties props = new Properties();
            props.load(is);
            return props;
        } catch (IOException e) {
            System.err.println("Error loading database properties: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract the base URL from the database URL (everything before the database
     * name)
     * 
     * @param dbUrl the complete database URL
     * @return the base URL without database name
     */
    private static String extractBaseUrl(String dbUrl) {
        if (dbUrl == null || dbUrl.isEmpty()) {
            return null;
        }

        // Handle JDBC URLs more robustly
        int questionMarkIndex = dbUrl.indexOf('?');
        String urlWithoutParams = questionMarkIndex > 0 ? dbUrl.substring(0, questionMarkIndex) : dbUrl;

        int lastSlashIndex = urlWithoutParams.lastIndexOf('/');
        if (lastSlashIndex > 0) {
            return urlWithoutParams.substring(0, lastSlashIndex);
        }
        return null;
    }

    /**
     * Execute SQL script from a file
     * 
     * @param conn database connection
     */
    private static void executeSqlScript(Connection conn) {
        Statement stmt = null;
        boolean hasErrors = false;

        try (InputStream is = Database.class.getClassLoader().getResourceAsStream(DB_INIT_SCRIPT_FILE)) {
            if (is == null) {
                System.err.println("Could not find SQL initialization script: " + DB_INIT_SCRIPT_FILE);
                return;
            }

            stmt = conn.createStatement();

            // Set auto-commit to false to allow rollback if needed
            boolean autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            // Read SQL script line by line
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder command = new StringBuilder();
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                // Skip comments and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }

                command.append(line);

                if (line.endsWith(";")) {
                    // Execute SQL command
                    String sql = command.toString();
                    try {
                        stmt.execute(sql);
                    } catch (SQLException e) {
                        hasErrors = true;
                        System.err.println("Error executing SQL at line " + lineNumber + ": " + sql);
                        System.err.println("Error message: " + e.getMessage());

                        // For CREATE DATABASE and USE statements, we don't want to stop
                        if (!sql.toUpperCase().contains("CREATE DATABASE") && !sql.toUpperCase().contains("USE ")) {
                            // Critical error that should stop execution
                            if (sql.toUpperCase().contains("CREATE TABLE")) {
                                throw new SQLException("Failed to create required table: " + e.getMessage(), e);
                            }
                        }
                    }
                    command.setLength(0);
                } else {
                    command.append(" ");
                }
            }

            // If everything went well, commit the transaction
            if (!hasErrors) {
                conn.commit();
                System.out.println("SQL script executed successfully.");
            } else {
                System.out.println("SQL script executed with some errors. See logs for details.");
                conn.commit(); // Still commit what worked
            }

            // Restore original auto-commit setting
            conn.setAutoCommit(autoCommit);

        } catch (IOException e) {
            System.err.println("Error reading SQL script: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException se) {
                System.err.println("Error rolling back transaction: " + se.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Critical error executing SQL script: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException se) {
                System.err.println("Error rolling back transaction: " + se.getMessage());
            }
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Error closing statement: " + e.getMessage());
                }
            }
        }
    }
}