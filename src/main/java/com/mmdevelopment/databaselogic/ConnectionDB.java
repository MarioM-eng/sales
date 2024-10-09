package com.mmdevelopment.databaselogic;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

@Slf4j
public class ConnectionDB {

    private static Connection conn = null;

    private ConnectionDB(){

    }

    public static Connection getConnection() {

        if (conn == null) {
            try {
                conn = DriverManager.getConnection("jdbc:h2:./src/main/java/com/mmdevelopment/databaselogic/database");
            } catch (Exception e) {
                log.error("{}: {}", e.getClass().getName(), e.getMessage());
                System.exit(0);
            }
            log.info("Opened database successfully");
        }
        return conn;
    }
}
