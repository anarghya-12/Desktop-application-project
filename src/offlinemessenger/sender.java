package offlinemessenger;

import java.io.*;
import java.net.*;

public class Sender {
    public static void sendMessage(Message msg, int port) {
        try {
            Socket socket = new Socket(msg.getReceiverIP(), port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(msg);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
