package server;

import common.FileTransfer;
import common.Message;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerThread implements Runnable {

    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String username;
    private boolean loggedIn = false; // NOVA PROMENLJIVA ZA STATUS PRIJAVE

    public ServerThread(Socket socket, String username) {
        this.clientSocket = socket;
        this.username = username; 
        try {
            // Prvo Output Stream, pa Input Stream
            this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Greška pri kreiranju streamova za klijenta " + username + ": " + e.getMessage());
            closeEverything();
        }
    }

    public String getUsername() {
        return username;
    }
    
    /**
     * Metoda za promenu korisničkog imena (koristi se pri loginu).
     */
    public synchronized boolean setUsername(String newUsername) {
        if (Server.findClient(newUsername) == null) {
            // Ime nije zauzeto, menjamo ga i dodajemo klijenta u listu
            this.username = newUsername;
            this.loggedIn = true;
            
            Server.addClient(this); // Dodavanje u listu servera
            
            System.out.println("Klijent uspešno prijavljen kao: " + newUsername);
            
            // Obaveštavamo sve o novom korisniku
            Server.broadcastMessage(new Message("SERVER", null, newUsername + " se priključio chatu!"), null);
            
            return true;
        }
        return false; // Ime je zauzeto
    }

    public void sendObject(Object object) {
        try {
            outputStream.writeObject(object);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Greška pri slanju objekta klijentu " + username);
            closeEverything();
        }
    }

    @Override
    public void run() {
        Object receivedObject;
        while (clientSocket.isConnected()) {
            try {
                receivedObject = inputStream.readObject();

                if (!loggedIn) {
                    // Logika za PRVU PORUKU (LOGIN)
                    if (receivedObject instanceof Message) {
                        Message loginMsg = (Message) receivedObject;
                        String desiredUsername = loginMsg.getSender(); 
                        
                        if (setUsername(desiredUsername)) {
                            sendObject(new Message("SERVER", desiredUsername, "Dobrodošao/la! Možeš da kreneš sa chatom."));
                        } else {
                            sendObject(new Message("SERVER", desiredUsername, "Korisničko ime " + desiredUsername + " je zauzeto."));
                            // Opciono: Prekid konekcije nakon neuspelog logina (zbog završnog rada, ostavljamo da klijent pokuša ponovo)
                        }
                    }
                } else {
                    // Logika za standardne poruke (nakon uspešnog logina)
                    if (receivedObject instanceof Message) {
                        handleMessage((Message) receivedObject);
                    } else if (receivedObject instanceof FileTransfer) {
                        handleFileTransfer((FileTransfer) receivedObject);
                    } 
                }

            } catch (EOFException e) {
                break;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Greška u komunikaciji sa klijentom " + username + ": " + e.getMessage());
                break;
            }
        }
        closeEverything();
    }

    private void handleMessage(Message msg) {
        // 1. Čuvanje u bazi
        Server.getDbHandler().saveMessage(msg); 

        System.out.printf("PRIMLJENA PORUKA od %s (za %s): %s\n", 
                          msg.getSender(), msg.getRecipient() == null ? "SVE" : msg.getRecipient(), msg.getContent());

        // 2. Logika Rutiranja
        if (msg.getRecipient() == null || msg.getRecipient().isEmpty()) {
            // Broadcast
            Server.broadcastMessage(msg, this); 
        } else {
            // Privatna poruka
            ServerThread recipientThread = Server.findClient(msg.getRecipient());
            if (recipientThread != null) {
                recipientThread.sendObject(msg); 
                this.sendObject(new Message("SERVER", msg.getSender(), "Poruka uspešno poslata korisniku " + msg.getRecipient()));
            } else {
                this.sendObject(new Message("SERVER", msg.getSender(), "Korisnik " + msg.getRecipient() + " nije pronađen na mreži."));
            }
        }
    }

    private void handleFileTransfer(FileTransfer ft) {
        System.out.printf("PRIMLJEN FAJL od %s: %s (%d bajtova)\n", 
                          ft.getSender(), ft.getFileName(), ft.getFileSize());

        // **Čuvanje fajla na disku servera**
        String serverFilePath = "server_files/" + ft.getSender() + "_" + ft.getFileName();
        try {
            java.nio.file.Path dirPath = Paths.get("server_files");
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            Files.write(Paths.get(serverFilePath), ft.getFileData());
            
            // 1. Čuvanje meta-podataka u bazi
            Server.getDbHandler().saveFileRecord(ft, serverFilePath);
            
            System.out.println("Fajl sačuvan na serveru: " + serverFilePath);
            
        } catch (IOException e) {
            System.err.println("Greška pri čuvanju fajla na disku: " + e.getMessage());
            serverFilePath = "ERROR";
        }

        // 2. Prosleđivanje/Obaveštavanje (Trenutno se šalje samo obaveštenje pošiljaocu)
        this.sendObject(new Message("SERVER", ft.getSender(), 
                                    String.format("Fajl '%s' je primljen i sačuvan na serveru.", ft.getFileName())));
        
        // **OPCIONO: Ako želiš da fajl pošalješ primaocu (za to je potrebna logika u klijentu da prihvati fajl)**
        /*
        if (ft.getRecipient() != null && !ft.getRecipient().isEmpty()) {
             ServerThread recipientThread = Server.findClient(ft.getRecipient());
             if (recipientThread != null) {
                 recipientThread.sendObject(ft); 
             }
        }
        */
    }

    private void closeEverything() {
        Server.removeClient(this);
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}