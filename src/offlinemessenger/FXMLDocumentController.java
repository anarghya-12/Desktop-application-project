package offlinemessenger;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FXMLDocumentController {

    @FXML private TextArea messageArea;
    @FXML private TextField messageField;
    @FXML private Label recipientLabel;
    @FXML private CheckBox broadcastCheckBox;

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
}



