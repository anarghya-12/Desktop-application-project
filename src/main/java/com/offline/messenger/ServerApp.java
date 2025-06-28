package com.offline.messenger;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServerApp {
    private static final int PORT = 12345;
    private static final Map<String, PrintWriter> userWriters = new ConcurrentHashMap<>();
    private static final Map<String, Set<String>> groupMembers = new ConcurrentHashMap<>();

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

                // Handle group creation
                if (message.startsWith("@group:create:")) {
                    String[] parts = message.split(":", 4);
                    if (parts.length == 4) {
                        String groupName = parts[2];
                        String[] members = parts[3].split(",");
                        groupMembers.put(groupName, new HashSet<>(Arrays.asList(members)));
                        System.out.println("[Server] Group created: " + groupName + " with members " + Arrays.toString(members));
                        
                        // Notify all group members (except sender) to add the group to their dropdown
                        for (String member : members) {
                            PrintWriter memberOut = userWriters.get(member);
                                if (memberOut != null) {
                                    memberOut.println("@group:add:" + groupName);
                                }
                        }
                    }
                    continue;
                }
                
                // Handle adding user to an existing group
                if (message.startsWith("@group:adduser:")) {
                    String[] parts = message.split(":", 4);  // Format: @group:adduser:groupName:username
                    if (parts.length == 3) {
                        String groupName = parts[1];
                        String addedUser = parts[2];

                        // Refresh from DB
                        DBHelper dbHelper = new DBHelper();
                        List<String> freshMembers = dbHelper.getGroupMembers(groupName);
                        groupMembers.put(groupName, new HashSet<>(freshMembers));

                        // Notify the added user if they're online
                        PrintWriter addedOut = userWriters.get(addedUser);
                        if (addedOut != null) {
                            addedOut.println("@group:add:" + groupName);
                        }

                        System.out.println("[Server] " + addedUser + " re-added to group " + groupName);
                    }
                    continue;
                }


                // Normal or group messaging
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
                                PrintWriter senderOut = userWriters.get(sender);
                                if (senderOut != null) {
                                    senderOut.println("You are not a member of group '" + recipient + "'. Message not sent.");
                                }
                                continue;
                            }

                            for (String member : members) {
                                if (!member.equals(sender)) {
                                    PrintWriter pw = userWriters.get(member);
                                    if (pw != null) {
                                        pw.println("[Group: " + recipient + "] " + sender + ": " + msgText);
                                    }
                                }
                            }
                        } else {
                            // One-to-one message
                            PrintWriter recipientOut = userWriters.get(recipient);
                            if (recipientOut != null) {
                                recipientOut.println(sender + " to you: " + msgText);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Server] Client error: " + e.getMessage());
        }
    }
}
