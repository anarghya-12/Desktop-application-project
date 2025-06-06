package offlinemessenger;

import java.io.*;
import java.net.*;

public class Receiver extends Thread {
    private int port;
    private MessageListener listener;

    public interface MessageListener {
        void onMessageReceived(Message msg);
    }

    public Receiver(int port, MessageListener listener) {
        this.port = port;
        this.listener = listener;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Message msg = (Message) in.readObject();
                listener.onMessageReceived(msg);
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
