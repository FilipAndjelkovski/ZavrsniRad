package server;

import common.Message;
import common.FileTransfer;

import java.sql.*;

public class DatabaseHandler {

    private static final String DB_URL = "jdbc:sqlite:chat_history.db";
    
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_FILES = "file_records";

    public DatabaseHandler() {
        initializeDatabase();
    }

    private Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC"); 
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println("DB Connection Error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found. Make sure the JAR is in the classpath.");
        }
        return conn;
    }

    private void initializeDatabase() {
        String createMessagesTable = "CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "sender TEXT NOT NULL,"
                + "recipient TEXT,"
                + "content TEXT NOT NULL,"
                + "timestamp DATETIME NOT NULL"
                + ");";

        String createFilesTable = "CREATE TABLE IF NOT EXISTS " + TABLE_FILES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "sender TEXT NOT NULL,"
                + "recipient TEXT,"
                + "filename TEXT NOT NULL,"
                + "filesize INTEGER NOT NULL,"
                + "timestamp DATETIME NOT NULL,"
                + "server_path TEXT"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createMessagesTable);
            stmt.execute(createFilesTable);
            System.out.println("Baza podataka inicijalizovana (tabele kreirane/proverene).");
        } catch (SQLException e) {
            System.err.println("DB Initialization Error: " + e.getMessage());
        }
    }

    public void saveMessage(Message message) {
        String sql = "INSERT INTO " + TABLE_MESSAGES + "(sender, recipient, content, timestamp) VALUES(?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, message.getSender());
            pstmt.setString(2, message.getRecipient());
            pstmt.setString(3, message.getContent());
            pstmt.setString(4, message.getTimestamp().toString()); 
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("DB Save Message Error: " + e.getMessage());
        }
    }

    public void saveFileRecord(FileTransfer ft, String serverFilePath) {
        String sql = "INSERT INTO " + TABLE_FILES + "(sender, recipient, filename, filesize, timestamp, server_path) VALUES(?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ft.getSender());
            pstmt.setString(2, ft.getRecipient());
            pstmt.setString(3, ft.getFileName());
            pstmt.setLong(4, ft.getFileSize());
            pstmt.setString(5, java.time.LocalDateTime.now().toString()); 
            pstmt.setString(6, serverFilePath);
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("DB Save File Record Error: " + e.getMessage());
        }
    }
    
}