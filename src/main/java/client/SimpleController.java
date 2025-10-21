package client;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SimpleController {
    
    @FXML
    private TextField messageInput;
    
    @FXML
    private void sendMessage() {
        System.out.println("Poruka: " + messageInput.getText());
        messageInput.clear();
    }
}


