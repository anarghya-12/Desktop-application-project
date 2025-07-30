package com.offline.messenger;

import java.io.*;
import java.net.Socket;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReceiverThread extends Thread {
    private Socket socket;
    private ChatFrame chatFrame;

    public ReceiverThread(Socket socket, ChatFrame chatFrame) {
        this.socket = socket;
        this.chatFrame = chatFrame;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
            while (true) {
                String message = in.readUTF();

                if (message.startsWith("@group:add:")) {
                    String groupName = message.substring("@group:add:".length()).trim();
                    chatFrame.showGroup(groupName);
                    continue;
                }

                if (message.startsWith("@sent:")) {
                    String[] parts = message.split(":");
                    if (parts.length == 2) {
                        String messageId = parts[1];
                        System.out.println("📤 Sent ACK received for " + messageId);
                        chatFrame.updateMessageStatus(messageId, "Sent");
                    } else {
                        System.out.println("⚠️ Malformed @sent message: " + message);
                    }
                    continue;
                }

                if (message.startsWith("@msg:")) {
                    String[] parts = message.split(":", 5);
                    if (parts.length == 5) {
                        String messageId = parts[1];
                        String sender = parts[2];
                        String receiver = parts[3];
                        String body = parts[4];

                        String display = receiver.startsWith("group_") 
                            ? "[" + receiver + "] " + sender + ": " + body 
                            : sender + ": " + body;

                        chatFrame.showMessage(sender, display, messageId, "Delivered");
                        chatFrame.getMessageSenderMap().put(messageId, sender);

                        String activeChat = chatFrame.getCurrentChat().trim().toLowerCase();
                        String actualChat = receiver.startsWith("group_")
                            ? receiver.trim().toLowerCase()
                            : sender.trim().toLowerCase();

//                        if (chatFrame.getOut() != null) {
//                            try {
//                                chatFrame.getOut().writeUTF("@delivered:" + messageId);
//                                chatFrame.getOut().flush();
//                                System.out.println("📬 Delivered sent for " + messageId);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }

                        if (actualChat.equals(activeChat)) {
                            if (chatFrame.getOut() != null) {
                                try {
                                    // Send Delivered ACK
                                    chatFrame.getOut().writeUTF("@delivered:" + messageId);
                                    chatFrame.getOut().flush();

                                    // Send Seen ACK with delay
                                    new Thread(() -> {
                                        try {
                                            Thread.sleep(700); // Optional delay
                                            SwingUtilities.invokeLater(() -> {
                                                try {
                                                    chatFrame.getOut().writeUTF("@seen:" + messageId);
                                                    chatFrame.getOut().flush();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            });
                                        } catch (InterruptedException ex) {
                                            ex.printStackTrace();
                                        }
                                    }).start();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    } else {
                        chatFrame.showSystemMessage("⚠️ Malformed message received: " + message, false);
                    }
                    continue;
                }
                
                if (message.startsWith("FILE_TRANSFER:")) {
                    String[] parts = message.split(":", 4);
                    if (parts.length == 4) {
                        String sender = parts[1];
                        String recipient = parts[2];
                        String fileName = parts[3];

                        int fileSize = in.readInt();
                        if (fileSize < 0 || fileSize > 100_000_000) {
                            chatFrame.showSystemMessage("❌ Invalid file size.", false);
                            return;
                        }

                        byte[] fileBytes = new byte[fileSize];
                        in.readFully(fileBytes);

                        SwingUtilities.invokeLater(() -> {
                            JFileChooser fileChooser = new JFileChooser();
                            fileChooser.setSelectedFile(new File(fileName));
                            int choice = fileChooser.showSaveDialog(null);

                            if (choice == JFileChooser.APPROVE_OPTION) {
                                File saveFile = fileChooser.getSelectedFile();
                                try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                                    fos.write(fileBytes);
                                    chatFrame.showSystemMessage("✅ File saved: " + saveFile.getAbsolutePath(), false);
                                } catch (IOException e) {
                                    chatFrame.showSystemMessage("❌ Failed to save file: " + e.getMessage(), false);
                                }
                            } else {
                                chatFrame.showSystemMessage("⚠️ File saving cancelled.", false);
                            }
                        });
                    } else {
                        chatFrame.showSystemMessage("⚠️ Malformed FILE_TRANSFER header.", false);
                    }
                    continue;
                }

                if (message.startsWith("@delivered:")) {
                    String messageId = message.substring("@delivered:".length());
                    System.out.println("✅ Delivered ACK received for " + messageId);
                    chatFrame.updateMessageStatus(messageId, "Delivered");
                    continue;
                }

                if (message.startsWith("@seen:")) {
                    String messageId = message.substring("@seen:".length());
                    String sender = chatFrame.getSenderForMessage(messageId);
                    if (sender != null && sender.equalsIgnoreCase(chatFrame.getUsername())) {
                        chatFrame.updateMessageStatus(messageId, "Seen");
                    }
                    continue;
                }

                chatFrame.showSystemMessage("📩 " + message, false);
            }

        } catch (EOFException e) {
            chatFrame.showSystemMessage("🔌 Connection closed by server.", false);
        } catch (IOException e) {
            chatFrame.showSystemMessage("❌ Receiver error: " + e.getMessage(), false);
        }
    }
}
