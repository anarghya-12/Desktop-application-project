//package com.offline.messenger;
//
//
///**
// *
// * @author ritid
// */
//
//import javax.swing.*;
//import java.awt.*;
//import java.io.*;
//import java.net.*;
//import java.util.List;
//import java.util.ArrayList;
//import java.util.Set;
//import java.util.HashSet;
//
//public class ChatFrame extends JFrame {
//    private JTextArea chatArea;
//    private JTextField inputField;
//    private JButton sendButton;
//    private JButton leaveGroupButton;
//    private JComboBox<String> userSelector;
//    private String username;
//    private List<String> userGroups;
//
//
//    private ServerThread serverThread;
//    private Socket socket;
//    private PrintWriter out;
//
//    private static final String SERVER_IP = "localhost"; // for testing on same PC
//    private static final int SERVER_PORT = 12345;
//
//    public ChatFrame(String username) {
//        this.username = username;
//
//        setTitle("Chat - User: " + username);
//        setSize(500, 400);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        chatArea = new JTextArea();
//        chatArea.setEditable(false);
//        JScrollPane scrollPane = new JScrollPane(chatArea);
//
//        inputField = new JTextField();
//        sendButton = new JButton("Send");
//        
//        //button to create groups
//        JButton createGroupButton = new JButton("Create Group");
//        
//        //button to broadcast message
//        JButton broadcastButton = new JButton("Broadcast Message");
//        
//        //button to log out
//        JButton logoutButton = new JButton("Log Out");
//
//
//        
//        //button to leave a group
//        leaveGroupButton = new JButton("Leave Group");
//        leaveGroupButton.setVisible(false); // hide it by default
//
//        DBHelper dbHelper = new DBHelper();
//        List<String> users = dbHelper.getAllUsernamesExcept(username);
//        userSelector = new JComboBox<>(users.toArray(new String[0]));
// 
//        // Load existing groups from DB
//        userGroups = new DBHelper().getGroupsForUser(username);
//        for (String group : userGroups) {
//            userSelector.addItem(group);
//        }
//        
//        //button to view group members
//        JButton viewMembersButton = new JButton("View Members");
//        viewMembersButton.setVisible(false);
//        
//        //button to add/remove group participants
//        JButton editGroupButton = new JButton("Edit Group");
//        editGroupButton.setVisible(false);  // only visible in groups 
//
//        JPanel inputPanel = new JPanel(new BorderLayout());
//        inputPanel.add(inputField, BorderLayout.CENTER);
//        JPanel buttonPanelTop = new JPanel(new FlowLayout());
//        buttonPanelTop.add(sendButton);
//        buttonPanelTop.add(createGroupButton);
//        buttonPanelTop.add(broadcastButton);
//        buttonPanelTop.add(logoutButton);
//        
//        JPanel buttonPanelBottom = new JPanel(new FlowLayout());
//        buttonPanelBottom.add(leaveGroupButton);
//        buttonPanelBottom.add(viewMembersButton);
//        buttonPanelBottom.add(editGroupButton);
//
//        JPanel buttonContainer = new JPanel(new BorderLayout());
//        buttonContainer.add(buttonPanelTop, BorderLayout.NORTH);
//        buttonContainer.add(buttonPanelBottom, BorderLayout.SOUTH);
//
//        inputPanel.add(buttonContainer, BorderLayout.EAST);
//
//        add(userSelector, BorderLayout.NORTH);
//        add(scrollPane, BorderLayout.CENTER);
//        add(inputPanel, BorderLayout.SOUTH);
//
//        sendButton.addActionListener(e -> sendMessage());
//        createGroupButton.addActionListener(e -> createGroup());
//        broadcastButton.addActionListener(e -> openBroadcastDialog());
//        leaveGroupButton.addActionListener(e -> leaveGroup());
//        inputField.addActionListener(e -> sendMessage());
//        
//         logoutButton.addActionListener(e -> {
//    try {
//        if (socket != null && !socket.isClosed()) {
//            socket.close();  // close socket connection
//        }
//    } catch (IOException ex) {
//        ex.printStackTrace();
//    }
//
//    this.dispose();  // close the ChatFrame
//    new LoginFrame().setVisible(true);  // show Login screen
//});
//
//        
//        userSelector.addActionListener(e -> {
//            String selected = (String) userSelector.getSelectedItem();
//            if (selected != null && userGroups.contains(selected)) {
//                leaveGroupButton.setVisible(true);
//                viewMembersButton.setVisible(true);
//                editGroupButton.setVisible(true);
//            } else {
//                leaveGroupButton.setVisible(false);
//                viewMembersButton.setVisible(false);
//                editGroupButton.setVisible(false);
//            }
//        });
//        
//        viewMembersButton.addActionListener(e -> {
//            String selectedGroup = (String) userSelector.getSelectedItem();
//            if (selectedGroup != null && userGroups.contains(selectedGroup)) {
//                DBHelper db = new DBHelper();
//                List<String> members = db.getGroupMembers(selectedGroup);
//                JOptionPane.showMessageDialog(this,
//                    "Members of " + selectedGroup + ":\n" + String.join(", ", members),
//                    "Group Members", JOptionPane.INFORMATION_MESSAGE);
//            }
//        });
//        
//        editGroupButton.addActionListener(e -> editGroup());
//        
//        
//
//        try {
//            socket = new Socket(SERVER_IP, SERVER_PORT);
//            out = new PrintWriter(socket.getOutputStream(), true);
//            //prints username to server
//            out.println(username);
//            
//            // Start listening for incoming messages
//            new ReceiverThread(socket, this).start();
//    
//        } catch (IOException ex) {
//            chatArea.append("Could not connect to server: " + ex.getMessage() + "\n");
//        }
//    }
//
//    private void sendMessage() {
//        String message = inputField.getText().trim();
//        String targetUser = (String) userSelector.getSelectedItem();
//        if (!message.isEmpty() && out != null) {
//            String fullMessage = username + " to " + targetUser + ": " + message;
//            out.println(fullMessage);
//            chatArea.append(fullMessage + "\n");
//            inputField.setText("");
//        }
//    }
//    
//    private void createGroup() {
//        String groupName = JOptionPane.showInputDialog(this, "Enter group name:");
//        if (groupName == null || groupName.trim().isEmpty()) return;
//
//        List<String> allUsers = new DBHelper().getAllUsernamesExcept(username);
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        List<JCheckBox> checkBoxes = new ArrayList<>();
//
//        for (String user : allUsers) {
//            JCheckBox cb = new JCheckBox(user);
//            checkBoxes.add(cb);
//            panel.add(cb);
//        }
//
//        JScrollPane scrollPane = new JScrollPane(panel);
//        scrollPane.setPreferredSize(new Dimension(200, 150));
//        int result = JOptionPane.showConfirmDialog(this, scrollPane, 
//                "Select users to add to group", JOptionPane.OK_CANCEL_OPTION);
//
//        if (result == JOptionPane.OK_OPTION) {
//            List<String> selectedUsers = new ArrayList<>();
//            for (JCheckBox cb : checkBoxes) {
//                if (cb.isSelected()) selectedUsers.add(cb.getText());
//            }
//            if (!selectedUsers.isEmpty()) {
//                selectedUsers.add(username); // Add the group creator
//                String members = String.join(",", selectedUsers);
//                String groupCommand = "@group:create:" + groupName + ":" + members;
//              //  out.println(username);  // required by server
//                out.println(groupCommand);
//                userSelector.addItem(groupName);
//                userGroups.add(groupName); // Update the runtime group list
//
//                // Save group to DB
//                new DBHelper().saveGroup(groupName, selectedUsers);
//                
//                for (String user : selectedUsers) {
//                    if (!user.equals(username)) { // Don't notify the creator
//                        out.println("@group:adduser:" + groupName + ":" + user);
//                    }
//                }
//                
//                chatArea.append("Group '" + groupName + "' created!\n");
//            }                                                               
//        }
//    }
//
//    public void showMessage(String message) {
//        chatArea.append(message + "\n");
//    }
//    
//    public void showGroup(String groupName) {
//        SwingUtilities.invokeLater(() -> {
//            ComboBoxModel<String> model = userSelector.getModel();
//            boolean found = false;
//
//            for (int i = 0; i < model.getSize(); i++) {
//                if (model.getElementAt(i).equals(groupName)) {
//                    found = true;
//                    break;
//                }
//            }
//
//            if (!found) {
//                ((DefaultComboBoxModel<String>) userSelector.getModel()).addElement(groupName);
//                userGroups.add(groupName);
//                chatArea.append("You were added to group: " + groupName + "\n");
//            } else if (!userGroups.contains(groupName)) {
//                userGroups.add(groupName);
//                chatArea.append("You were re-added to group: " + groupName + "\n");
//            }
//        });
//    }
//
//    
//    private void leaveGroup() {
//        String selected = (String) userSelector.getSelectedItem();
//        if (selected == null || !selected.startsWith("Group:") && !new DBHelper().getGroupsForUser(username).contains(selected)) {
//            JOptionPane.showMessageDialog(this, "Please select a group to leave.");
//            return;
//        }
//
//        int confirm = JOptionPane.showConfirmDialog(this,
//            "Are you sure you want to leave group '" + selected + "'?",
//            "Confirm Leave", JOptionPane.YES_NO_OPTION);
//
//        if (confirm == JOptionPane.YES_OPTION) {
//            DBHelper db = new DBHelper();
//            if (db.removeUserFromGroup(username, selected)) {
//                userSelector.removeItem(selected);
//                chatArea.append("You left the group: " + selected + "\n");
//            } else {
//                chatArea.append("Failed to leave the group.\n");
//            }
//        }
//    }
//    
//    private void editGroup() {
//        String groupName = (String) userSelector.getSelectedItem();
//        if (groupName == null || !userGroups.contains(groupName)) return;
//
//        DBHelper db = new DBHelper();
//        List<String> allUsers = db.getAllUsernamesExcept(username);
//        List<String> currentMembers = db.getGroupMembers(groupName);
//
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        List<JCheckBox> checkBoxes = new ArrayList<>();
//
//        for (String user : allUsers) {
//            JCheckBox cb = new JCheckBox(user);
//            cb.setSelected(currentMembers.contains(user));
//            checkBoxes.add(cb);
//            panel.add(cb);
//        }
//
//        JScrollPane scrollPane = new JScrollPane(panel);
//        scrollPane.setPreferredSize(new Dimension(200, 150));
//        int result = JOptionPane.showConfirmDialog(this, scrollPane,
//                "Modify group members", JOptionPane.OK_CANCEL_OPTION);
//
//        if (result == JOptionPane.OK_OPTION) {
//            List<String> updatedMembers = new ArrayList<>();
//            for (JCheckBox cb : checkBoxes) {
//                if (cb.isSelected()) updatedMembers.add(cb.getText());
//            }
//
//            // Always keep the current user in the group
//            if (!updatedMembers.contains(username)) {
//                updatedMembers.add(username);
//            }
//
//            db.saveGroup(groupName, updatedMembers);
//            chatArea.append("Group '" + groupName + "' updated!\n");
//
//            // Broadcast to newly added members
//            Set<String> newMembers = new HashSet<>(updatedMembers);
//            newMembers.removeAll(currentMembers); // only the ones who were NOT already there
//
//            for (String newUser : newMembers) {
//                if (out != null) {
//                    out.println("@group:adduser:" + groupName + ":" + newUser);
//                }
//            }
//        }
//    }
//    
//    
////    private void openBroadcastDialog() {
////    DBHelper dbHelper = new DBHelper();
////    List<String> users = dbHelper.getAllUsernamesExcept(username);
////
////    JPanel panel = new JPanel(new BorderLayout());
////    JPanel listPanel = new JPanel();
////    listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
////
////    List<JCheckBox> checkBoxes = new ArrayList<>();
////    for (String user : users) 
////    {
////        JCheckBox checkBox = new JCheckBox(user);
////        checkBoxes.add(checkBox);
////        listPanel.add(checkBox);
////    }
////
////    JScrollPane scrollPane = new JScrollPane(listPanel);
////    scrollPane.setPreferredSize(new Dimension(200, 150));
////
////    JTextField messageField = new JTextField();
////
////    panel.add(new JLabel("Select users:"), BorderLayout.NORTH);
////    panel.add(scrollPane, BorderLayout.CENTER);
////    panel.add(messageField, BorderLayout.SOUTH);
////
////    int result = JOptionPane.showOptionDialog(
////        this,
////        panel,
////        "Broadcast Message",
////        JOptionPane.OK_CANCEL_OPTION,
////        JOptionPane.PLAIN_MESSAGE,
////        null,
////        new Object[]{"Broadcast Now", "Cancel"},
////        "Broadcast Now"
////    );
////
////    if (result == JOptionPane.OK_OPTION) 
////    {
////        String message = messageField.getText().trim();
////        if (!message.isEmpty()) 
////        {
////            for (JCheckBox cb : checkBoxes) 
////            {
////                if (cb.isSelected()) 
////                {
////                    String recipient = cb.getText();
////                    String fullMessage = username + " to " + recipient + ": " + message;
////                    out.println(fullMessage);
////                }
////            }
////            chatArea.append("Broadcasted: " + message + "\n");
////        } 
////        else 
////        {
////            JOptionPane.showMessageDialog(this, "Please enter a message to broadcast.");
////        }
////    }
////}
//    
//    private void openBroadcastDialog() {
//    DBHelper dbHelper = new DBHelper();
//    List<String> users = dbHelper.getAllUsernamesExcept(username);
//    List<String> groups = dbHelper.getGroupsForUser(username);
//
//    JPanel panel = new JPanel(new BorderLayout());
//
//    JPanel listPanel = new JPanel();
//    listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
//
//    JLabel usersLabel = new JLabel("Select Users:");
//    listPanel.add(usersLabel);
//
//    List<JCheckBox> userCheckBoxes = new ArrayList<>();
//    for (String user : users) {
//        JCheckBox checkBox = new JCheckBox(user);
//        userCheckBoxes.add(checkBox);
//        listPanel.add(checkBox);
//    }
//
//    JLabel groupsLabel = new JLabel("Select Groups:");
//    listPanel.add(groupsLabel);
//
//    List<JCheckBox> groupCheckBoxes = new ArrayList<>();
//    for (String group : groups) {
//        JCheckBox checkBox = new JCheckBox(group);
//        groupCheckBoxes.add(checkBox);
//        listPanel.add(checkBox);
//    }
//
//    JScrollPane scrollPane = new JScrollPane(listPanel);
//    scrollPane.setPreferredSize(new Dimension(250, 200));
//
//    JTextField messageField = new JTextField();
//
//    panel.add(scrollPane, BorderLayout.CENTER);
//    panel.add(messageField, BorderLayout.SOUTH);
//
//    int result = JOptionPane.showOptionDialog(
//        this,
//        panel,
//        "Broadcast Message",
//        JOptionPane.OK_CANCEL_OPTION,
//        JOptionPane.PLAIN_MESSAGE,
//        null,
//        new Object[]{"Broadcast Now", "Cancel"},
//        "Broadcast Now"
//    );
//
//    if (result == JOptionPane.OK_OPTION) {
//        String message = messageField.getText().trim();
//        if (!message.isEmpty()) {
//            // ✅ Send to selected users
//            for (JCheckBox cb : userCheckBoxes) {
//                if (cb.isSelected()) {
//                    String recipient = cb.getText();
//                    String fullMessage = username + " to " + recipient + ": " + message;
//                    out.println(fullMessage);
//                }
//            }
//
//            // ✅ Send to selected groups
//            for (JCheckBox cb : groupCheckBoxes) {
//                if (cb.isSelected()) {
//                    String groupName = cb.getText();
//                    String fullMessage = username + " to " + groupName + ": " + message;
//                    out.println(fullMessage);  // server will route to group members
//                }
//            }
//
//            chatArea.append("Broadcasted: " + message + "\n");
//        } else {
//            JOptionPane.showMessageDialog(this, "Please enter a message to broadcast.");
//        }
//    }
//}
//
//
//}
//
//
//
//




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

public class ChatFrame extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton, createGroupButton, broadcastButton, logoutButton;
    private JButton leaveGroupButton, viewMembersButton, editGroupButton;
    private JComboBox<String> userSelector;


    private String username;
    private List<String> userGroups;
    private Socket socket;
    private PrintWriter out;

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

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

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

        userSelector.addActionListener(e -> {
            String selected = (String) userSelector.getSelectedItem();
            boolean isGroup = userGroups.contains(selected);
            leaveGroupButton.setVisible(isGroup);
            viewMembersButton.setVisible(isGroup);
            editGroupButton.setVisible(isGroup);
        });

        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(username);
            new ReceiverThread(socket, this).start();
        } catch (IOException ex) {
            chatArea.append("❌ Could not connect to server: " + ex.getMessage() + "\n");
        }
    }
//           try {
//            socket = new Socket(SERVER_IP, SERVER_PORT);
//            out = new PrintWriter(socket.getOutputStream(), true);
//            out.println(username);
//            new ReceiverThread(socket, this).start();
//        } catch (IOException ex) {
//            chatArea.append("❌ Could not connect to server: " + ex.getMessage() + "\n");
//        }
//    }



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
            out.println(fullMessage);
            chatArea.append(fullMessage + "\n");
            inputField.setText("");
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
                out.println("@group:create:" + groupName + ":" + members);
                userSelector.addItem(groupName);
                userGroups.add(groupName);
                new DBHelper().saveGroup(groupName, selectedUsers);
                for (String user : selectedUsers) {
                    if (!user.equals(username)) {
                        out.println("@group:adduser:" + groupName + ":" + user);
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
                        out.println(username + " to " + cb.getText() + ": " + message);
                    }
                }
                for (JCheckBox cb : groupCheckBoxes) {
                    if (cb.isSelected()) {
                        out.println(username + " to " + cb.getText() + ": " + message);
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
                    out.println("@group:adduser:" + groupName + ":" + newUser);
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
    
//    private void viewGroupMembers() {
//    String selectedGroup = (String) userSelector.getSelectedItem();
//    if (selectedGroup != null && userGroups.contains(selectedGroup)) {
//        DBHelper db = new DBHelper();  // ✅ Fix: add this
//        List<String> members = db.getGroupMembers(selectedGroup);
//        JOptionPane.showMessageDialog(this,
//            "Members of " + selectedGroup + ":\n" + String.join(", ", members),
//            "Group Members", JOptionPane.INFORMATION_MESSAGE);
//    }
//}

}




