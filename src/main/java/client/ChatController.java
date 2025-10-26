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

    public void initializeNetwork(String serverHost, int serverPort, String username) {
        this.clientUsername = username;
        
        try {
            handler = new ClientHandler(serverHost, serverPort, this);
            
            chatView.getItems().add("Povezan kao: " + clientUsername);
            
            // Send login message
            handler.sendObject(new Message(clientUsername, null, "LOGIN"));
            
            // Start handler thread
            new Thread(handler).start();
            
        } catch (IOException e) {
            System.err.println("Greška pri povezivanju na server: " + e.getMessage());
            chatView.getItems().add("NEUSPEŠNO POVEZIVANJE!");
        }
    }

    public void displayMessage(String message) {
        Platform.runLater(() -> chatView.getItems().add(message));
    }

    @FXML
    private void sendMessage() {
        String text = messageInput.getText().trim();
        String recipient = recipientInput.getText().trim();

        if (text.isEmpty() && selectedFile == null) {
            return;
        }

        try {
            // Send file if selected
            if (selectedFile != null) {
                sendFile(recipient);
            }
            
            // Send text message if provided
            if (!text.isEmpty()) {
                Message msg = new Message(clientUsername, recipient.isEmpty() ? null : recipient, text);
                handler.sendObject(msg);
                displayMessage("Ja (" + (recipient.isEmpty() ? "SVI" : recipient) + "): " + text);
            }
            
            messageInput.clear();
            
        } catch (IOException e) {
            displayMessage("Greška pri slanju poruke.");
            System.err.println("Greška pri slanju poruke/fajla: " + e.getMessage());
        }
    }

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

    private void sendFile(String recipient) throws IOException {
        if (selectedFile == null || !selectedFile.exists()) return;

        byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());

        FileTransfer fileTransfer = new FileTransfer(
                clientUsername,
                recipient.isEmpty() ? null : recipient,
                selectedFile.getName(),
                fileBytes
        );

        handler.sendObject(fileTransfer);
        
        String recipientInfo = recipient.isEmpty() ? "svim korisnicima" : "korisniku " + recipient;
        displayMessage(String.format("📎 FAJL POSLAT %s; %s (%d bajtova)", 
                                   recipientInfo, selectedFile.getName(), fileBytes.length));
        
        selectedFile = null;
        fileNameLabel.setText("Nema odabranog fajla");
    }
}