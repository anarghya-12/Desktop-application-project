package com.offline.messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReceiverThread extends Thread {
    private Socket socket;
    private ChatFrame chatFrame;

    public ReceiverThread(Socket socket, ChatFrame chatFrame) {
        this.socket = socket;
        this.chatFrame = chatFrame;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("@group:add:")) {
                    String groupName = message.substring("@group:add:".length()).trim();
                    chatFrame.showGroup(groupName);
                    continue;
                }

                chatFrame.showMessage("Received: " + message);
            }

        } catch (IOException e) {
            chatFrame.showMessage("Receiver disconnected: " + e.getMessage());
        }
    }
}
