package com.offline.messenger;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
//import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.nio.file.Files;

public class ChatFrame extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton, createGroupButton, broadcastButton, logoutButton;
    private JButton leaveGroupButton, viewMembersButton, editGroupButton;
    private JComboBox<String> userSelector;


    private String username;
    private List<String> userGroups;
    private Socket socket;
    private DataOutputStream out;

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public ChatFrame(String username) {
        this.username = username;
        setTitle("Offline Messenger - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // 🖥️ Main panel setup
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(34, 34, 34));

        // 🎯 Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 18));
        chatArea.setBackground(new Color(245, 245, 245));
        chatArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane chatScroll = new JScrollPane(chatArea);
        mainPanel.add(chatScroll, BorderLayout.CENTER);

        // ⬅️ Sidebar (users + buttons)
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(new Color(50, 50, 50));
        sidePanel.setPreferredSize(new Dimension(300, getHeight()));

        JLabel selectLabel = new JLabel("Send Message To:");
        selectLabel.setForeground(Color.WHITE);
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        DBHelper dbHelper = new DBHelper();
        List<String> users = dbHelper.getAllUsernamesExcept(username);
        userSelector = new JComboBox<>(users.toArray(String[]::new));

        userGroups = dbHelper.getGroupsForUser(username);
        for (String group : userGroups) {
            userSelector.addItem(group);
        }

        userSelector.setMaximumSize(new Dimension(250, 30));
        userSelector.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ⏺️ Buttons
        createGroupButton = createStyledButton("Create Group");
        broadcastButton = createStyledButton("Broadcast");
        logoutButton = createStyledButton("Logout");
        leaveGroupButton = createStyledButton("Leave Group");
        viewMembersButton = createStyledButton("View Members");
        editGroupButton = createStyledButton("Edit Group");

        leaveGroupButton.setVisible(false);
        viewMembersButton.setVisible(false);
        editGroupButton.setVisible(false);

        sidePanel.add(Box.createVerticalStrut(20));
        sidePanel.add(selectLabel);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(userSelector);
        sidePanel.add(Box.createVerticalStrut(20));
        sidePanel.add(createGroupButton);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(broadcastButton);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(logoutButton);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(leaveGroupButton);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(viewMembersButton);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(editGroupButton);

        mainPanel.add(sidePanel, BorderLayout.WEST);

        // ⌨️ Input + Send
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.DARK_GRAY);

//        inputField = new JTextField();
//        inputField.setFont(new Font("SansSerif", Font.PLAIN, 16));
            inputField = new JTextField("Enter message here");
            inputField.setFont(new Font("SansSerif", Font.PLAIN, 16));
            inputField.setForeground(Color.GRAY);  // Placeholder color

            inputField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (inputField.getText().equals("Enter message here")) {
                    inputField.setText("");
                    inputField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (inputField.getText().isEmpty()) {
                    inputField.setText("Enter message here");
                    inputField.setForeground(Color.GRAY);
                }
            }
        });

        sendButton = createStyledButton("Send");
        JButton attachFileButton = createStyledButton("📎 Attach File");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(attachFileButton, BorderLayout.WEST);


        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
        setVisible(true);

        // 🎯 Event Listeners
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        createGroupButton.addActionListener(e -> createGroup());
        broadcastButton.addActionListener(e -> openBroadcastDialog());
        logoutButton.addActionListener(e -> logout());
        leaveGroupButton.addActionListener(e -> leaveGroup());
        viewMembersButton.addActionListener(e -> viewGroupMembers());
        editGroupButton.addActionListener(e -> editGroup());
        attachFileButton.addActionListener(e -> sendFile());


        userSelector.addActionListener(e -> {
            String selected = (String) userSelector.getSelectedItem();
            boolean isGroup = userGroups.contains(selected);
            leaveGroupButton.setVisible(isGroup);
            viewMembersButton.setVisible(isGroup);
            editGroupButton.setVisible(isGroup);
        });

        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(username);
            new ReceiverThread(socket, this).start();
        } catch (IOException ex) {
            chatArea.append("❌ Could not connect to server: " + ex.getMessage() + "\n");
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBackground(new Color(124, 77, 255));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(220, 40));
        button.setMaximumSize(new Dimension(220, 40));
        return button;
    }

    private void logout() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
        }
        this.dispose();
        new LoginFrame().setVisible(true);
    }

    // ✅ Methods below are unchanged from your implementation:

    private void sendMessage() {
        String message = inputField.getText().trim();
        String targetUser = (String) userSelector.getSelectedItem();

        if (!message.isEmpty() && out != null) {
            String fullMessage = username + " to " + targetUser + ": " + message;
             try {
                out.writeUTF(fullMessage);
                chatArea.append(fullMessage + "\n");
                inputField.setText("");
            } catch (IOException ex) {
                chatArea.append("❌ Message send failed: " + ex.getMessage() + "\n");
            }        
        }
    }
    
    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String fileName = file.getName();
            String recipient = (String) userSelector.getSelectedItem();

            try {
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                int fileSize = fileBytes.length;

                // 1. Send header with file metadata
                out.writeUTF("FILE_TRANSFER:" + username + ":" + recipient + ":" + fileName);

                // 2. Send file size and file content
                out.writeInt(fileSize);
                out.write(fileBytes);
                out.flush();

                chatArea.append("📤 Sent file '" + fileName + "' to " + recipient + "\n");

            } catch (IOException ex) {
                ex.printStackTrace();
                chatArea.append("❌ Failed to send file: " + ex.getMessage() + "\n");
            }
        }
    }



    private void createGroup() {
        String groupName = JOptionPane.showInputDialog(this, "Enter group name:");
        if (groupName == null || groupName.trim().isEmpty()) return;

        List<String> allUsers = new DBHelper().getAllUsernamesExcept(username);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        List<JCheckBox> checkBoxes = new ArrayList<>();

        for (String user : allUsers) {
            JCheckBox cb = new JCheckBox(user);
            checkBoxes.add(cb);
            panel.add(cb);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(200, 150));
        int result = JOptionPane.showConfirmDialog(this, scrollPane,
                "Select users to add to group", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            List<String> selectedUsers = new ArrayList<>();
            for (JCheckBox cb : checkBoxes) {
                if (cb.isSelected()) selectedUsers.add(cb.getText());
            }
            if (!selectedUsers.isEmpty()) {
                selectedUsers.add(username);
                String members = String.join(",", selectedUsers);
                try {
                    out.writeUTF("@group:create:" + groupName + ":" + members);
                } catch (IOException e) {
                    chatArea.append("❌ Failed to send group creation message: " + e.getMessage() + "\n");
                }

                userSelector.addItem(groupName);
                userGroups.add(groupName);
                new DBHelper().saveGroup(groupName, selectedUsers);
                for (String user : selectedUsers) {
                    if (!user.equals(username)) {
                        try {
                            out.writeUTF("@group:adduser:" + groupName + ":" + user);
                        } catch (IOException e) {
                            chatArea.append("❌ Failed to add user to group: " + user + "\n");
                        }
                    }
                }
                chatArea.append("Group '" + groupName + "' created!\n");
            }
        }
    }

    private void openBroadcastDialog() {
        DBHelper dbHelper = new DBHelper();
        List<String> users = dbHelper.getAllUsernamesExcept(username);
        List<String> groups = dbHelper.getGroupsForUser(username);

        JPanel panel = new JPanel(new BorderLayout());
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JLabel usersLabel = new JLabel("Select Users:");
        listPanel.add(usersLabel);

        List<JCheckBox> userCheckBoxes = new ArrayList<>();
        for (String user : users) {
            JCheckBox checkBox = new JCheckBox(user);
            userCheckBoxes.add(checkBox);
            listPanel.add(checkBox);
        }

        JLabel groupsLabel = new JLabel("Select Groups:");
        listPanel.add(groupsLabel);

        List<JCheckBox> groupCheckBoxes = new ArrayList<>();
        for (String group : groups) {
            JCheckBox checkBox = new JCheckBox(group);
            groupCheckBoxes.add(checkBox);
            listPanel.add(checkBox);
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(250, 200));
        JTextField messageField = new JTextField();

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(messageField, BorderLayout.SOUTH);

        int result = JOptionPane.showOptionDialog(
                this, panel, "Broadcast Message",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new Object[]{"Broadcast Now", "Cancel"}, "Broadcast Now");

        if (result == JOptionPane.OK_OPTION) {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                for (JCheckBox cb : userCheckBoxes) {
                    if (cb.isSelected()) {
                         try {
                            out.writeUTF(username + " to " + cb.getText() + ": " + message);
                        } catch (IOException ex) {
                            chatArea.append("❌ Failed to send to user " + cb.getText() + ": " + ex.getMessage() + "\n");
                        }
                    }
                }
                for (JCheckBox cb : groupCheckBoxes) {
                    if (cb.isSelected()) {
                        try {
                            out.writeUTF(username + " to " + cb.getText() + ": " + message);
                        } catch (IOException ex) {
                            chatArea.append("❌ Failed to send to group " + cb.getText() + ": " + ex.getMessage() + "\n");
                        }
                    }
                }
                chatArea.append("Broadcasted: " + message + "\n");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a message to broadcast.");
            }
        }
    }

    private void leaveGroup() {
        String selected = (String) userSelector.getSelectedItem();
        if (selected == null || !userGroups.contains(selected)) {
            JOptionPane.showMessageDialog(this, "Please select a group to leave.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to leave group '" + selected + "'?",
                "Confirm Leave", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DBHelper db = new DBHelper();
            if (db.removeUserFromGroup(username, selected)) {
                userSelector.removeItem(selected);
                chatArea.append("You left the group: " + selected + "\n");
            } else {
                chatArea.append("Failed to leave the group.\n");
            }
        }
    }

    private void editGroup() {
        String groupName = (String) userSelector.getSelectedItem();
        if (groupName == null || !userGroups.contains(groupName)) return;

        DBHelper db = new DBHelper();
        List<String> allUsers = db.getAllUsernamesExcept(username);
        List<String> currentMembers = db.getGroupMembers(groupName);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        List<JCheckBox> checkBoxes = new ArrayList<>();

        for (String user : allUsers) {
            JCheckBox cb = new JCheckBox(user);
            cb.setSelected(currentMembers.contains(user));
            checkBoxes.add(cb);
            panel.add(cb);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(200, 150));
        int result = JOptionPane.showConfirmDialog(this, scrollPane,
                "Modify group members", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            List<String> updatedMembers = new ArrayList<>();
            for (JCheckBox cb : checkBoxes) {
                if (cb.isSelected()) updatedMembers.add(cb.getText());
            }

            if (!updatedMembers.contains(username)) {
                updatedMembers.add(username);
            }

            db.saveGroup(groupName, updatedMembers);
            chatArea.append("Group '" + groupName + "' updated!\n");

            Set<String> newMembers = new HashSet<>(updatedMembers);
            newMembers.removeAll(currentMembers);

            for (String newUser : newMembers) {
                if (out != null) {
                    try {
                        out.writeUTF("@group:adduser:" + groupName + ":" + newUser);
                    } catch (IOException ex) {
                        chatArea.append("❌ Failed to notify " + newUser + ": " + ex.getMessage() + "\n");
                    }
                }
            }
        }
    }

    private void viewGroupMembers() {
        String selectedGroup = (String) userSelector.getSelectedItem();
        if (selectedGroup != null && userGroups.contains(selectedGroup)) {
            DBHelper db = new DBHelper();
            List<String> members = db.getGroupMembers(selectedGroup);
            JOptionPane.showMessageDialog(this,
                    "Members of " + selectedGroup + ":\n" + String.join(", ", members),
                    "Group Members", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showMessage(String message) {
        chatArea.append(message + "\n");
    }

    public void showGroup(String groupName) {
        SwingUtilities.invokeLater(() -> {
            if (((DefaultComboBoxModel<String>) userSelector.getModel()).getIndexOf(groupName) == -1) {
                userSelector.addItem(groupName);
                userGroups.add(groupName);
                chatArea.append("You were added to group: " + groupName + "\n");
            }
        });
    }
}
