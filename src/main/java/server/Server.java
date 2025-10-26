package server;

import common.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    
    // Sinhronizovana lista svih aktivnih niti (klijenata)
    private static List<ServerThread> clients = Collections.synchronizedList(new ArrayList<>());
    private static final int PORT = 5000;
    
    private static DatabaseHandler dbHandler; 

    public static void main(String[] args) {
        System.out.println("--- Server se pokreće... ---");
        
        dbHandler = new DatabaseHandler(); // Inicijalizacija DB Handlera

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server sluša na portu: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); 
                
                // Privremeno ime - biće promenjeno nakon uspešnog login-a
                String tempUsername = "TRENUTNI_KLIJENT_" + clientSocket.getPort(); 
                
                // Kreiranje nove niti. Klijent NIJE automatski dodat u listu `clients`.
                ServerThread clientThread = new ServerThread(clientSocket, tempUsername);
                
                new Thread(clientThread).start(); 
                
                System.out.println("Novi klijent pokušava konekciju: " + tempUsername);
            }
        } catch (IOException e) {
            System.err.println("Greška na serveru (Socket/IO): " + e.getMessage());
        }
    }
    
    // --- Javne metode za manipulaciju listom i podacima ---
    
    public static DatabaseHandler getDbHandler() {
        return dbHandler;
    }
    
    public static void addClient(ServerThread client) {
        clients.add(client);
    }
    
    public static void broadcastMessage(Object object, ServerThread sender) {
        // Koristimo kopiju liste ili sinhronizaciju zbog višenitnosti
        for (ServerThread client : clients) {
            if (client != sender) {
                client.sendObject(object);
            }
        }
    }
    
    public static void removeClient(ServerThread client) {
        if (clients.remove(client)) {
            System.out.println("Klijent " + client.getUsername() + " se diskonektovao.");
            
            // Obavesti ostale korisnike samo ako je bio prijavljen
            if (client.loggedIn) {
                Message systemMessage = new Message("SERVER", null, client.getUsername() + " je napustio chat.");
                broadcastMessage(systemMessage, null);
            }
        }
    }
    
    public static ServerThread findClient(String username) {
        for (ServerThread client : clients) {
            // Provera ignorisanjem velikih/malih slova (case-insensitive)
            if (client.getUsername().equalsIgnoreCase(username)) {
                return client;
            }
        }
        return null;
    }
    
    // Getter za listu klijenata (opciono, za napredne kontrole)
    public static List<ServerThread> getClients() {
        return clients;
    }
}