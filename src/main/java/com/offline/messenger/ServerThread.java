/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.offline.messenger;
/**
 *
 * @author ritid
 */

import java.io.*;
import java.net.*;

public class ServerThread extends Thread {
    private ChatFrame chatFrame;
    private int port;

    public ServerThread(ChatFrame chatFrame, int port) {
        this.chatFrame = chatFrame;
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            chatFrame.showMessage("Server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket, chatFrame).start();
            }
        } catch (IOException e) {
            chatFrame.showMessage("Server error: " + e.getMessage());
        }
    }
}

