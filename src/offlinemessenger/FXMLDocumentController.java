package offlinemessenger;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;


public class FXMLDocumentController {

    @FXML private TextArea messageArea;
    @FXML private TextField messageField;
    @FXML private Label recipientLabel;
    @FXML private CheckBox broadcastCheckBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;


    @FXML
    private void handleSend() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            if (broadcastCheckBox.isSelected()) {
                messageArea.appendText("Broadcast: " + message + "\n");
                // TODO: Implement broadcast logic
            } else {
                String recipient = recipientLabel.getText();
                messageArea.appendText("To " + recipient + ": " + message + "\n");
                // TODO: Implement individual message sending
            }
            messageField.clear();
        }
    }
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        if (DBHelper.loginUser(username, password)) {
            statusLabel.setText("Login successful!");
        } else {
            statusLabel.setText("Invalid credentials.");
        }
    }
    
    @FXML
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        if (DBHelper.registerUser(username, password)) {
            statusLabel.setText("Signup successful!");
        } else {
            statusLabel.setText("Signup failed.");
        }
    }
}



