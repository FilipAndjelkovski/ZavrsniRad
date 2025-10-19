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
        
        // --- 1. Korak: Uzimanje korisničkog imena putem dijaloga ---
        String username = getUsernameFromUser();
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Klijent je otkazan/nije uneto ime.");
            return;
        }
        
        // --- 2. Korak: Pokretanje UI-ja ---
        // Učitavanje FXML-a iz resources/fxml/ChatView.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChatView.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("JavaFX Chat Klijent - " + username);
        primaryStage.setScene(new Scene(root, 600, 450));
        primaryStage.show();

        // --- 3. Korak: Inicijalizacija mreže ---
        ChatController controller = loader.getController();
        // Šaljemo dobijeno ime pri inicijalizaciji mreže
        controller.initializeNetwork("localhost", 5000, username); 
    }
    
    /**
     * Prikazuje dijalog za unos korisničkog imena.
     */
    private String getUsernameFromUser() {
        TextInputDialog dialog = new TextInputDialog("Korisnik_" + (int)(Math.random() * 100)); // Predloženo ime
        dialog.setTitle("Prijava na Chat");
        dialog.setHeaderText("Unesite željeno korisničko ime:");
        dialog.setContentText("Ime:");

        return dialog.showAndWait().orElse(null);
    }
}
// Potrebno je da ClientController.initializeNetwork() pozove handler.sendObject(loginMsg)
// da bi se serverThread prijavio.