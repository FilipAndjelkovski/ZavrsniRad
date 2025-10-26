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
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.controller = controller;
    }

    public void sendObject(Serializable object) throws IOException {
        outputStream.writeObject(object);
        outputStream.flush();
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                Object receivedObject = inputStream.readObject();

                if (receivedObject instanceof Message) {
                    handleMessage((Message) receivedObject);
                } else if (receivedObject instanceof FileTransfer) {
                    handleFileTransfer((FileTransfer) receivedObject);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            controller.displayMessage("Konekcija sa serverom prekinuta.");
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(Message msg) {
        String display = String.format("[%s]: %s", msg.getSender(), msg.getContent());
        controller.displayMessage(display);
    }

    private void handleFileTransfer(FileTransfer ft) throws IOException {
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String newFileName = "primljeno_" + timestamp + "_" + ft.getFileName();
        
        Files.write(Paths.get(newFileName), ft.getFileData());

        String senderInfo = ft.getSender().equals("SERVER") ? "servera" : "korisnika " + ft.getSender();
        controller.displayMessage(
            String.format("📎 FAJL PRIMLJEN od %s: %s (%d bajtova)", 
                         senderInfo, ft.getFileName(), ft.getFileSize())
        );
        controller.displayMessage(
            String.format("Fajl je sačuvan kao: %s", newFileName)
        );
    }
}