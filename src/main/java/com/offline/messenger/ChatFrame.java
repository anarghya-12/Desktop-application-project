package com.offline.messenger;


/**
 *
 * @author ritid
 */

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.List;

public class ChatFrame extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JComboBox<String> userSelector;
    private String username;

    private ServerThread serverThread;
    private Socket socket;
    private PrintWriter out;

    private static final String SERVER_IP = "localhost"; // for testing on same PC
    private static final int SERVER_PORT = 12345;

    public ChatFrame(String username) {
        this.username = username;

        setTitle("Chat - User: " + username);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("Send");

        DBHelper dbHelper = new DBHelper();
        List<String> users = dbHelper.getAllUsernamesExcept(username);
        userSelector = new JComboBox<>(users.toArray(new String[0]));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(userSelector, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

//        serverThread = new ServerThread(this, SERVER_PORT);
//        serverThread.start();

        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            //prints username to server
            out.println(username);
            
            // Start listening for incoming messages
            new ReceiverThread(socket, this).start();
    
        } catch (IOException ex) {
            chatArea.append("Could not connect to server: " + ex.getMessage() + "\n");
        }
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        String targetUser = (String) userSelector.getSelectedItem();
        if (!message.isEmpty() && out != null) {
            String fullMessage = username + " to " + targetUser + ": " + message;
            out.println(fullMessage);
            chatArea.append(fullMessage + "\n");
            inputField.setText("");
        }
    }

    public void showMessage(String message) {
        chatArea.append(message + "\n");
    }
}

    public void showMessage(String message) {
        chatArea.append(message + "\n");
    }
}
