package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestClient extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/simple.fxml"));
        Parent root = loader.load();
        
        primaryStage.setTitle("Test Chat Client");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
        
        System.out.println("FXML aplikacija je pokrenuta!");
    }
}


