package com.offline.messenger;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerApp {

    private static final int PORT = 12345;
    private static final Map<String, DataOutputStream> userStreams = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, String> messageSenderMap = Collections.synchronizedMap(new HashMap<>());
    private static final Set<String> deliveredMessageIds = Collections.synchronizedSet(new HashSet<>());


    public static void main(String[] args) {
        System.out.println("🚀 Server started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("❌ Server error: " + e.getMessage());
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private String username;
        private DataInputStream in;
        private DataOutputStream out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                username = in.readUTF();
                userStreams.put(username, out);
                System.out.println("✅ User connected: " + username);

                while (true) {
                    String msg = in.readUTF();
                    handleMessage(msg);
                }
            } catch (IOException e) {
                System.err.println("❗ " + username + " disconnected.");
            } finally {
                userStreams.remove(username);
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }

        private void handleMessage(String msg) {
            try {
                if (msg.startsWith("@msg:")) {
                    // Format: @msg:id:sender:receiver:body
                    String[] parts = msg.split(":", 5);
                    if (parts.length < 5) return;

                    String messageId = parts[1];
                    String sender = parts[2];
                    String receiver = parts[3];
                    String body = parts[4];

                    messageSenderMap.put(messageId, sender);

                    DataOutputStream receiverOut = userStreams.get(receiver);

                    if (receiverOut != null) {
                        try {
                            receiverOut.writeUTF("@msg:" + messageId + ":" + sender + ":" + receiver + ":" + body);
                            // ✅ Only mark as delivered *after* successful write
                            deliveredMessageIds.add(messageId); 

                            // ✅ Only send delivered if the above write succeeded
                            DataOutputStream senderOut = userStreams.get(sender);
                            if (senderOut != null) {
                                senderOut.writeUTF("@delivered:" + messageId);
                            }
                        } catch (IOException e) {
                            System.err.println("❌ Could not deliver message to " + receiver + ": " + e.getMessage());
                        }
                    } else {
                        System.out.println("⚠️ User " + receiver + " not online. Message not delivered.");
                    }

                } else if (msg.startsWith("@delivered:")) {
                    // Forward to original sender
                    String messageId = msg.substring("@delivered:".length());
                    String sender = findSenderByMessageId(messageId);
                    if (sender != null) {
                        DataOutputStream senderOut = userStreams.get(sender);
                        if (senderOut != null) {
                            senderOut.writeUTF("@delivered:" + messageId);
                        }
                    }
                } else if (msg.startsWith("@seen:")) {
                    String messageId = msg.substring("@seen:".length());
                    System.out.println("👁️ Seen received from: " + username + " for msg ID: " + messageId);

                    // 🔒 Only allow 'seen' if this message was delivered first
                    if (!deliveredMessageIds.contains(messageId)) {
                        System.out.println("⛔ Seen ignored: Message " + messageId + " was never delivered.");
                        return;
                    }

                    String sender = findSenderByMessageId(messageId);
                    if (sender != null && userStreams.containsKey(sender)) {
                        DataOutputStream senderOut = userStreams.get(sender);
                        if (senderOut != null) {
                            senderOut.writeUTF("@seen:" + messageId);
                        }
                    } else {
                        System.out.println("⚠️ Could not forward seen ack for msg ID " + messageId + " — sender not available.");
                    }
                } else if (msg.startsWith("@group:")) {
                    // Optional: group message handling
                    System.out.println("🔧 Group message received: " + msg);

                } else if (msg.startsWith("FILE_TRANSFER:")) {
                    // Already handled well
                    String[] parts = msg.split(":", 4);
                    String sender = parts[1];
                    String receiver = parts[2];
                    String fileName = parts[3];

                    int fileSize = in.readInt();
                    byte[] fileData = new byte[fileSize];
                    in.readFully(fileData);

                    DataOutputStream receiverOut = userStreams.get(receiver);
                    if (receiverOut != null) {
                        receiverOut.writeUTF("FILE_TRANSFER:" + sender + ":" + receiver + ":" + fileName);
                        receiverOut.writeInt(fileSize);
                        receiverOut.write(fileData);
                    }

                } else {
                    // Generic fallback forwarding (optional)
                    if (msg.contains(" to ")) {
                        String[] parts = msg.split(" to ");
                        if (parts.length >= 2) {
                            String sender = parts[0].trim();
                            String rest = parts[1];
                            String[] toSplit = rest.split(":", 2);
                            if (toSplit.length == 2) {
                                String recipient = toSplit[0].trim();
                                String content = toSplit[1].trim();
                                DataOutputStream recipientOut = userStreams.get(recipient);
                                if (recipientOut != null) {
                                    recipientOut.writeUTF(sender + ": " + content);
                                }
                            }
                        }
                    }
                }

            } catch (IOException e) {
                System.err.println("💥 Error handling message from " + username + ": " + e.getMessage());
            }
        }
        private String findSenderByMessageId(String messageId) {
            return messageSenderMap.get(messageId);
        }
    }
}
