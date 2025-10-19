package client;

import common.FileTransfer;
import common.Message;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientHandler implements Runnable {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ChatController controller;

    public ClientHandler(String host, int port, ChatController controller) throws IOException {
        this.socket = new Socket(host, port);
        // Prvo Output Stream, pa Input Stream - ovo je ključno za Socket komunikaciju!
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.controller = controller;
    }

    // Metoda za slanje bilo kog serijalizovanog objekta
    public void sendObject(Serializable object) throws IOException {
        outputStream.writeObject(object);
        outputStream.flush();
    }

    @Override
    public void run() {
        try {
            // Kontinuirano slušanje servera
            while (socket.isConnected()) {
                Object receivedObject = inputStream.readObject();

                if (receivedObject instanceof Message) {
                    handleMessage((Message) receivedObject);
                } else if (receivedObject instanceof FileTransfer) {
                    handleFileTransfer((FileTransfer) receivedObject);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // Došlo je do greške (server isključen, prekinuta konekcija, itd.)
            controller.displayMessage("Konekcija sa serverom prekinuta.");
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) { /* ignorisati */ }
        }
    }

    private void handleMessage(Message msg) {
        // Prikazivanje poruke koju je poslao server (ili je server prosledio od drugog klijenta)
        String display = String.format("[%s]: %s", msg.getSender(), msg.getContent());
        controller.displayMessage(display);
    }

    private void handleFileTransfer(FileTransfer ft) throws IOException {
        // Logika za primanje fajla:
        // 1. Kreiranje lokalnog fajla
        String newFileName = "primljeno_" + ft.getFileName();
        
        // 2. Upisivanje bajtova u fajl
        Files.write(Paths.get(newFileName), ft.getFileData());

        controller.displayMessage(
            String.format("--- FAJL PRIMLJEN od %s: %s (%d bajtova) - Sačuvan kao: %s ---", 
                           ft.getSender(), ft.getFileName(), ft.getFileSize(), newFileName)
        );
    }
}