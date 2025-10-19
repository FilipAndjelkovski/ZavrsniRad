package client;

import common.FileTransfer;
import common.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ChatController {

    @FXML private ListView<String> chatView;
    @FXML private TextField messageInput;
    @FXML private TextField recipientInput;
    @FXML private Label fileNameLabel;

    private ClientHandler handler;
    private String clientUsername;
    private File selectedFile;

    // Metoda za inicijalizaciju mrežnog dela, poziva se iz Client.java
    public void initializeNetwork(String serverHost, int serverPort, String username) {
        this.clientUsername = username;
        
        try {
            // Kreiranje Handler-a (Niti za mrežnu komunikaciju)
            handler = new ClientHandler(serverHost, serverPort, this);
            new Thread(handler).start(); // Pokreće mrežnu komunikaciju u novoj niti
            
            chatView.getItems().add("Povezan kao: " + clientUsername);
            
        } catch (IOException e) {
            System.err.println("Greška pri povezivanju na server: " + e.getMessage());
            chatView.getItems().add("NEUSPEŠNO POVEZIVANJE!");
        }
    }

    // Dodavanje poruke u chat prozor (poziva ga ClientHandler)
    public void displayMessage(String message) {
        // Platform.runLater je neophodan jer se ovo poziva iz ClientHandler niti, a UI mora da se menja u JavaFX Aplikacionoj niti
        Platform.runLater(() -> chatView.getItems().add(message));
    }

    // Akcija na klik 'Pošalji Poruku'
    @FXML
    private void sendMessage() {
        String text = messageInput.getText().trim();
        String recipient = recipientInput.getText().trim();

        if (text.isEmpty()) {
            return;
        }

        try {
            if (selectedFile != null) {
                // Logika za slanje fajla (ako je fajl odabran)
                sendFile(recipient);
                // Nastavljamo sa slanjem tekstualne poruke kao prateće poruke
            }
            
            // Slanje tekstualne poruke
            Message msg = new Message(clientUsername, recipient.isEmpty() ? null : recipient, text);
            handler.sendObject(msg); // Delegiramo slanje handleru

            // Ažuriranje UI-ja
            displayMessage("Ja (" + (recipient.isEmpty() ? "SVI" : recipient) + "): " + text);
            messageInput.clear();
            
        } catch (IOException e) {
            displayMessage("Greška pri slanju poruke.");
            System.err.println("Greška pri slanju poruke/fajla: " + e.getMessage());
        }
    }

    // Akcija na klik 'Odaberi Fajl'
    @FXML
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            fileNameLabel.setText("Odabrano: " + selectedFile.getName());
        } else {
            fileNameLabel.setText("Nema odabranog fajla");
        }
    }

    // Pomoćna metoda za slanje fajla
    private void sendFile(String recipient) throws IOException {
        if (selectedFile == null || !selectedFile.exists()) return;

        // 1. Čitanje fajla u niz bajtova (Ovo je jednostavno za male fajlove, za velike fajlove treba koristiti tokove!)
        byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());

        // 2. Kreiranje objekta za prenos
        FileTransfer fileTransfer = new FileTransfer(
                clientUsername,
                recipient.isEmpty() ? null : recipient,
                selectedFile.getName(),
                fileBytes
        );

        // 3. Slanje objekta
        handler.sendObject(fileTransfer);
        displayMessage("--- FAJL POSLAT: " + selectedFile.getName() + " (" + fileBytes.length + " bajtova) ---");
        
        // Resetovanje fajla
        selectedFile = null;
        fileNameLabel.setText("Nema odabranog fajla");
    }
}