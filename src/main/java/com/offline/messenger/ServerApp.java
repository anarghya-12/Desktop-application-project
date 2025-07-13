package com.offline.messenger;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServerApp {
    private static final int PORT = 12345;
    private static final Map<String, DataOutputStream> userWriters = new ConcurrentHashMap<>();
    private static final Map<String, Set<String>> groupMembers = new ConcurrentHashMap<>();
    private static final Map<String, Socket> userSockets = new ConcurrentHashMap<>();

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
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            String clientName = in.readUTF();
            System.out.println("[Server] Connected: " + clientName);
            userWriters.put(clientName, out);
            userSockets.put(clientName, socket);

            while (true) {
                String message = in.readUTF(); // read message or header

                // 📁 Handle file transfer
                if (message.startsWith("FILE_TRANSFER:")) {
                    String[] parts = message.split(":", 4);
                    if (parts.length == 4) {
                        String sender = parts[1];
                        String recipient = parts[2];
                        String fileName = parts[3];

                        int fileSize = in.readInt();
                        byte[] fileData = new byte[fileSize];
                        in.readFully(fileData);  // ensure exact size
                        

                        Socket recipientSocket = userSockets.get(recipient);
                        DataOutputStream recipientOut = userWriters.get(recipient);

                        if (recipientSocket != null && recipientOut != null) {
                            recipientOut.writeUTF("@file:" + fileName + ":" + sender + ":" + recipient + ":" + fileSize);
                            recipientOut.write(fileData);
                            recipientOut.flush();
                            System.out.println("[Server] Forwarded file '" + fileName + "' from " + sender + " to " + recipient);
                        } else {
                            System.out.println("[Server] Recipient " + recipient + " not available.");
                        }
                        continue;
                    }
                }

                // 📦 Handle group creation
                if (message.startsWith("@group:create:")) {
                    String[] parts = message.split(":", 4);
                    if (parts.length == 4) {
                        String groupName = parts[2];
                        String[] members = parts[3].split(",");
                        groupMembers.put(groupName, new HashSet<>(Arrays.asList(members)));

                        System.out.println("[Server] Group created: " + groupName + " with members " + Arrays.toString(members));

                        for (String member : members) {
                            DataOutputStream memberOut = userWriters.get(member);
                            if (memberOut != null) {
                                memberOut.writeUTF("@group:add:" + groupName);
                            }
                        }
                    }
                    continue;
                }

                // 👤 Add user to existing group
                if (message.startsWith("@group:adduser:")) {
                    String[] parts = message.split(":", 3);
                    if (parts.length == 3) {
                        String groupName = parts[1];
                        String addedUser = parts[2];

                        DBHelper dbHelper = new DBHelper();
                        List<String> freshMembers = dbHelper.getGroupMembers(groupName);
                        groupMembers.put(groupName, new HashSet<>(freshMembers));

                        DataOutputStream addedOut = userWriters.get(addedUser);
                        if (addedOut != null) {
                            addedOut.writeUTF("@group:add:" + groupName);
                        }

                        System.out.println("[Server] " + addedUser + " re-added to group " + groupName);
                    }
                    continue;
                }

                // 🗣️ Handle group or direct message
                if (message.contains(" to ")) {
                    String[] parts = message.split(" to ", 2);
                    if (parts.length == 2 && parts[1].contains(":")) {
                        String sender = parts[0].trim();
                        String[] toParts = parts[1].split(":", 2);
                        String recipient = toParts[0].trim();
                        String msgText = toParts[1].trim();

                        if (groupMembers.containsKey(recipient)) {
                            DBHelper dbHelper = new DBHelper();
                            List<String> freshMembers = dbHelper.getGroupMembers(recipient);
                            groupMembers.put(recipient, new HashSet<>(freshMembers));

                            Set<String> members = groupMembers.get(recipient);
                            if (!members.contains(sender)) {
                                DataOutputStream senderOut = userWriters.get(sender);
                                if (senderOut != null) {
                                    senderOut.writeUTF("You are not a member of group '" + recipient + "'. Message not sent.");
                                }
                                continue;
                            }

                            for (String member : members) {
                                if (!member.equals(sender)) {
                                    DataOutputStream memberOut = userWriters.get(member);
                                    if (memberOut != null) {
                                        memberOut.writeUTF("[Group: " + recipient + "] " + sender + ": " + msgText);
                                    }
                                }
                            }
                        } else {
                            DataOutputStream recipientOut = userWriters.get(recipient);
                            if (recipientOut != null) {
                                recipientOut.writeUTF(sender + " to you: " + msgText);
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("[Server] Client error: " + e.getMessage());
        }
    }

    private static Socket findSocketForUser(String username) {
        return userSockets.get(username);
    }
}
