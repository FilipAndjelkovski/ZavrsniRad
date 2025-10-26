package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;

public class Client extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        
        String username = getUsernameFromUser();
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Klijent je otkazan/nije uneto ime.");
            return;
        }
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/fxml/ui.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("JavaFX Chat Klijent - " + username);
        primaryStage.setScene(new Scene(root, 600, 450));
        primaryStage.show();

        ChatController controller = loader.getController();
        controller.initializeNetwork("localhost", 5000, username); 
    }
    
    private String getUsernameFromUser() {
        TextInputDialog dialog = new TextInputDialog("Korisnik_" + (int)(Math.random() * 100));
        dialog.setTitle("Prijava na Chat");
        dialog.setHeaderText("Unesite željeno korisničko ime:");
        dialog.setContentText("Ime:");

        return dialog.showAndWait().orElse(null);
    }
}