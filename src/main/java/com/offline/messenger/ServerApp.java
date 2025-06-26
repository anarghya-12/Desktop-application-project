package com.offline.messenger;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServerApp {
    private static final int PORT = 12345;
    private static final Map<String, PrintWriter> userWriters = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("[Server] Starting server on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("[Server] Error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String clientName = in.readLine();
            System.out.println("[Server] Connected: " + clientName);
            userWriters.put(clientName, out);

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("[Server] Received: " + message);
                
                // Format: sender to recipient: message text
                if (message.contains(" to ")) {
                    String[] parts = message.split(" to ", 2);
                    if (parts.length == 2 && parts[1].contains(":")) {
                        String sender = parts[0].trim();
                        String[] toParts = parts[1].split(":", 2);
                        String recipient = toParts[0].trim();
                        String msgText = toParts[1].trim();

                        PrintWriter recipientOut = userWriters.get(recipient);
                        if (recipientOut != null) {
                            recipientOut.println(sender + " to you: " + msgText);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Server] Client error: " + e.getMessage());
        }
    }
}
