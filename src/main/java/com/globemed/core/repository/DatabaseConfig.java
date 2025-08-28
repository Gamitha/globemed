package com.globemed.core.repository;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final String DB_FILE = "data/db/globemed.db";
    private static DB db;

    public static synchronized DB getDatabase() {
        if (db == null || db.isClosed()) {
            try {
                // Ensure directory exists
                Path dbPath = Path.of("data/db");
                Files.createDirectories(dbPath);

                // Create database with more reliable configuration
                db = DBMaker.fileDB(new File(DB_FILE))
                        .transactionEnable()        // Enable transactions
                        .closeOnJvmShutdown()       // Close the database automatically on JVM shutdown
                        .make();

                logger.info("Database initialized successfully");
            } catch (Exception e) {
                logger.error("Failed to initialize database", e);
                throw new RuntimeException("Database initialization failed", e);
            }
        }
        return db;
    }

    public static void closeDatabase() {
        if (db != null && !db.isClosed()) {
            try {
                db.commit();  // Commit any pending changes
                db.close();
                logger.info("Database closed successfully");
            } catch (Exception e) {
                logger.error("Error while closing database", e);
            }
        }
    }
}
