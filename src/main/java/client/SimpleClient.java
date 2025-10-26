package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimpleClient extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        
        Label titleLabel = new Label("Chat Aplikacija - Test");
        TextField messageField = new TextField();
        messageField.setPromptText("Unesite poruku...");
        Button sendButton = new Button("Pošalji");
        
        root.getChildren().addAll(titleLabel, messageField, sendButton);
        
        primaryStage.setTitle("Simple Chat Client");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
        
        System.out.println("JavaFX aplikacija je pokrenuta!");
    }
}


