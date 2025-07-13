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
                String message = in.readUTF();  // read message header

                if (message.startsWith("@group:add:")) {
                    String groupName = message.substring("@group:add:".length()).trim();
                    chatFrame.showGroup(groupName);
                    continue;
                }

                if (message.startsWith("@file:")) {
                    String[] parts = message.split(":", 5);
                    if (parts.length == 5) {
                        String fileName = parts[1];
                        String sender = parts[2];
                        String recipient = parts[3];
                        int fileSize = Integer.parseInt(parts[4]);

                        if (fileSize < 0 || fileSize > 100_000_000) {
                            chatFrame.showMessage("❌ Invalid or too large file size received.");
                            continue;
                        }

                        chatFrame.showMessage("📥 Receiving file '" + fileName + "' from " + sender + "...");

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
                                    chatFrame.showMessage("✅ File saved as " + saveFile.getAbsolutePath());
                                } catch (IOException ex) {
                                    chatFrame.showMessage("❌ Failed to save file: " + ex.getMessage());
                                }
                            } else {
                                chatFrame.showMessage("⚠️ File saving cancelled.");
                            }
                        });
                    }
                    continue;
                }
                
                chatFrame.showMessage("Received: " + message);
            }

        } catch (EOFException e) {
            chatFrame.showMessage("🔌 Connection closed by server.");
        } catch (IOException e) {
            chatFrame.showMessage("❌ Receiver error: " + e.getMessage());
        }
    }
}
