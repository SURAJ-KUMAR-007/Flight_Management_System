
package com.fms.util;

import java.sql.Connection;
import java.sql.DriverManager;

public final class DBConnection {
    private DBConnection() {}

    public static Connection getConnection() {
        try {
            // Optional with modern JDBC, but safe:
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(
                    Config.dbUrl(),
                    Config.dbUser(),
                    Config.dbPass()
            );
        } catch (Exception e) {
            // Never return null; fail fast with context
            throw new IllegalStateException(
                    "Failed to obtain DB connection. Check .env (DB_URL/DB_USER/DB_PASS) and MySQL is running.",
                    e
            );
        }
    }
}
