//    /*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.offline.messenger;
//
//import java.io.*;
//import java.net.*;
//
//public class ClientHandler extends Thread {
//    private Socket socket;
//    private ChatFrame chatFrame;
//
//    public ClientHandler(Socket socket, ChatFrame chatFrame) {
//        this.socket = socket;
//        this.chatFrame = chatFrame;
//    }
//
//    @Override
//    public void run() {
//        try (BufferedReader in = new BufferedReader(
//                new InputStreamReader(socket.getInputStream()))) {
//
//            String message;
//            while ((message = in.readLine()) != null) {
//                chatFrame.showMessage(message);
//            }
//        } catch (IOException e) {
//            chatFrame.showMessage("Connection closed: " + e.getMessage());
//        }
//    }
//}