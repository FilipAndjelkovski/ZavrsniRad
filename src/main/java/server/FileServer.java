package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileServer implements Runnable {
    private static final int HTTP_PORT = 8080;
    private static final String FILES_DIR = "server_files";
    
    @Override
    public void run() {
        try (ServerSocket httpServer = new ServerSocket(HTTP_PORT)) {
            System.out.println("HTTP File Server sluša na portu: " + HTTP_PORT);
            
            while (true) {
                Socket client = httpServer.accept();
                new Thread(() -> handleRequest(client)).start();
            }
        } catch (IOException e) {
            System.err.println("Greška u HTTP File Serveru: " + e.getMessage());
        }
    }
    
    private void handleRequest(Socket client) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream(), true);
             OutputStream outputStream = client.getOutputStream()) {
            
            String requestLine = in.readLine();
            if (requestLine == null) return;
            
            String[] parts = requestLine.split(" ");
            if (parts.length < 2 || !parts[0].equals("GET")) {
                send404(out);
                return;
            }
            
            String path = parts[1];
            if (!path.startsWith("/files/")) {
                send404(out);
                return;
            }
            
            String fileName = path.substring(7);
            if (fileName.contains("..") || fileName.contains("/")) {
                send404(out);
                return;
            }
            
            Path filePath = Paths.get(FILES_DIR, fileName);
            File file = filePath.toFile();
            
            if (!file.exists() || !file.isFile()) {
                send404(out);
                return;
            }
            
            byte[] fileBytes = Files.readAllBytes(filePath);
            String contentType = getContentType(fileName);
            
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + contentType);
            out.println("Content-Length: " + fileBytes.length);
            out.println("Content-Disposition: attachment; filename=\"" + fileName + "\"");
            out.println();
            out.flush();
            
            outputStream.write(fileBytes);
            outputStream.flush();
            
        } catch (IOException e) {
            System.err.println("Greška pri obradi HTTP zahteva: " + e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {}
        }
    }
    
    private void send404(PrintWriter out) {
        out.println("HTTP/1.1 404 Not Found\r\n\r\nFile not found");
        out.flush();
    }
    
    private String getContentType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".txt")) return "text/plain";
        return "application/octet-stream";
    }
}
