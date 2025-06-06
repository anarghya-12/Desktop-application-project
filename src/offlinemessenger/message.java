package offlinemessenger;

import java.io.Serializable;

public class Message implements Serializable {
    private String senderName;
    private String receiverIP;
    private String messageBody;
    private boolean isBroadcast;

    public Message(String senderName, String receiverIP, String messageBody, boolean isBroadcast) {
        this.senderName = senderName;
        this.receiverIP = receiverIP;
        this.messageBody = messageBody;
        this.isBroadcast = isBroadcast;
    }

    public String getSenderName() { return senderName; }
    public String getReceiverIP() { return receiverIP; }
    public String getMessageBody() { return messageBody; }
    public boolean isBroadcast() { return isBroadcast; }
}
