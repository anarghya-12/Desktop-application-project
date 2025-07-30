package com.offline.messenger;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
//import java.util.*;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.nio.file.Files;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class ChatFrame extends JFrame {
    private JPanel messagePanel;
    private JScrollPane chatScroll;
    private JTextField inputField;
    private JButton sendButton, createGroupButton, broadcastButton, logoutButton;
    private JButton leaveGroupButton, viewMembersButton, editGroupButton;
    private JComboBox<String> userSelector;
    private String username;
    private String chatWithUser; //for window management
    private final Map<String, ChatBubble> bubbleMap = new HashMap<>();
    public static Map<String, JLabel> statusLabelMap = new HashMap<>();
    private Map<String, String> messageSenderMap = new HashMap<>();
    public static final Map<String, String> currentStatusMap = new HashMap<>();
    
    // Stores the list of messages per user (receiver)
    private final Map<String, List<JPanel>> chatHistory = new HashMap<>();

    // Track message status bubbles to update (if needed, same as before)
    private final Map<String, JLabel> messageStatusLabels = new HashMap<>();
    
    private Map<String, JPanel> userChatPanels = new HashMap<>();
    private Map<String, JScrollPane> chatScrollPanes = new HashMap<>();

    
    public String getSenderForMessage(String messageId) {
        return messageSenderMap.get(messageId);
    }
    
    public String getUsername() {
        return username;
    }
    
    public Map<String, String> getMessageSenderMap() {
        return messageSenderMap;
    }

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

        // 🖥 Main panel setup
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(34, 34, 34));

        // 🎯 Chat area updated
        messagePanel = new GradientMessagePanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Color.WHITE);

        chatScroll = new JScrollPane(messagePanel);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(chatScroll, BorderLayout.CENTER);


        // ⬅ Sidebar (users + buttons)
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

        // ⏺ Buttons
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

        // ⌨ Input + Send
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
            String newlySelected = (String) userSelector.getSelectedItem();

            // ✅ Save current chat before switching
            if (chatWithUser != null && !chatWithUser.equals(newlySelected)) {
                saveChatHistory(chatWithUser);
            }

            // ✅ Clear message panel
            messagePanel.removeAll();

            // ✅ Load previous messages (if any)
            if (chatHistory.containsKey(newlySelected)) {
                for (JPanel bubble : chatHistory.get(newlySelected)) {
                    messagePanel.add(bubble);
                }
            }

            // ✅ Refresh UI
            messagePanel.revalidate();
            messagePanel.repaint();

            // ✅ Mark this as the currently active chat
            chatWithUser = newlySelected;
            setCurrentChat(newlySelected);

            // ✅ Update group buttons visibility
            boolean isGroup = userGroups.contains(newlySelected);
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
            addMessageBubble(UUID.randomUUID().toString(), "❌ Could not connect to server: " + ex.getMessage(), false);
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
    
    // Store the panel at the given index for a specific user
    private void storeMessageBubble(String user, int index) {
        Component comp = messagePanel.getComponent(index);
        chatHistory.computeIfAbsent(user, k -> new ArrayList<>()).add((JPanel) comp);
    }

    // Save full current chat to map
    private void saveChatHistory(String user) {
        List<JPanel> list = new ArrayList<>();
        for (Component c : messagePanel.getComponents()) {
            if (c instanceof JPanel) list.add((JPanel) c);
        }
        chatHistory.put(user, list);
    }
    
    private void loadChatHistory(String user) {
        messagePanel.removeAll();

        List<JPanel> messages = chatHistory.getOrDefault(user, new ArrayList<>());
        for (JPanel msgBubble : messages) {
            messagePanel.add(msgBubble);
        }

        messagePanel.revalidate();
        messagePanel.repaint();
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        String targetUser = (String) userSelector.getSelectedItem();

        if (!message.isEmpty() && out != null) {
            String messageId = UUID.randomUUID().toString(); // unique per message

            try {
                // Track who sent the message
                messageSenderMap.put(messageId, username);

                // Send as: @msg:id:sender:receiver:body
                out.writeUTF("@msg:" + messageId + ":" + username + ":" + targetUser + ":" + message);

                // Show the sent message bubble (right side)
                addMessageBubble(messageId, "You: " + message, true);
                
                storeMessageBubble(targetUser, messagePanel.getComponentCount() - 1);

                // ✅ Immediately show "Sent" icon (📡)
                updateMessageStatus(messageId, "Sent");

                inputField.setText("");
                
                saveChatHistory(targetUser);

            } catch (IOException ex) {
                addMessageBubble(UUID.randomUUID().toString(), "❌ Message failed: " + ex.getMessage(), false);
                addMessageBubble(messageId, "You: " + message, true);
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

               addMessageBubble(UUID.randomUUID().toString(), "📤 Sent file '" + fileName + "' to " + recipient, true);

            } catch (IOException ex) {
                ex.printStackTrace();
                addMessageBubble(UUID.randomUUID().toString(), "❌ Failed to send file: " + ex.getMessage(), false);
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
                    addMessageBubble(UUID.randomUUID().toString(), "❌ Failed to send group creation message: " + e.getMessage(), false);
                }

                userSelector.addItem(groupName);
                userGroups.add(groupName);
                new DBHelper().saveGroup(groupName, selectedUsers);
                for (String user : selectedUsers) {
                    if (!user.equals(username)) {
                        try {
                            out.writeUTF("@group:adduser:" + groupName + ":" + user);
                        } catch (IOException e) {
                            addMessageBubble(UUID.randomUUID().toString(), "❌ Failed to add user to group: " + user, false);
                        }
                    }
                }
                addMessageBubble(UUID.randomUUID().toString(), "Group '" + groupName + "' created!", true);
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
                            addMessageBubble(UUID.randomUUID().toString(), "❌ Failed to send to user " + cb.getText() + ": " + ex.getMessage(), false);
                        }
                    }
                }
                for (JCheckBox cb : groupCheckBoxes) {
                    if (cb.isSelected()) {
                        try {
                            out.writeUTF(username + " to " + cb.getText() + ": " + message);
                        } catch (IOException ex) {
                            addMessageBubble(UUID.randomUUID().toString(), "❌ Failed to send to group " + cb.getText() + ": " + ex.getMessage(), false);
                        }
                    }
                }
                addMessageBubble(UUID.randomUUID().toString(), "📢 Broadcasted: " + message, true);
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
                addMessageBubble(UUID.randomUUID().toString(), "You left the group: " + selected, true);
            } else {
                addMessageBubble(UUID.randomUUID().toString(), "❌ Failed to leave the group.", false);
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
            addMessageBubble(UUID.randomUUID().toString(), "Group '" + groupName + "' updated!", true);

            Set<String> newMembers = new HashSet<>(updatedMembers);
            newMembers.removeAll(currentMembers);

            for (String newUser : newMembers) {
                if (out != null) {
                    try {
                        out.writeUTF("@group:adduser:" + groupName + ":" + newUser);
                    } catch (IOException ex) {
                        addMessageBubble(UUID.randomUUID().toString(), "❌ Failed to notify " + newUser + ": " + ex.getMessage(), false);
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
    
    public void addMessageBubble(String messageId, String message, boolean isSentByMe) {
        ChatBubble bubble = new ChatBubble(message, isSentByMe);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel statusLabel = null;

        if (isSentByMe) {
            java.net.URL iconURL = getClass().getClassLoader().getResource("icons/sent.png");

            if (iconURL != null) {
                ImageIcon sentIcon = new ImageIcon(iconURL);
                Image scaledImage = sentIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                ImageIcon finalIcon = new ImageIcon(scaledImage);

                statusLabel = new JLabel(finalIcon);

                // ⏺ Animate this icon
                animateStatusIcon(statusLabel, finalIcon);
            } else {
                statusLabel = new JLabel("📡");
            }

            statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            wrapper.add(Box.createHorizontalGlue());
            wrapper.add(bubble);
            wrapper.add(statusLabel);

        } else {
            // Add an empty label to allow status update for received messages too
            statusLabel = new JLabel(" ");  // initially blank
            statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            wrapper.add(bubble);
            wrapper.add(statusLabel);  // allow receiver-side status update
            wrapper.add(Box.createHorizontalGlue());
        }

        // 🔑 Always track the label, regardless of direction
        statusLabelMap.put(messageId, statusLabel);
        bubbleMap.put(messageId, bubble);  // 🟢 Add this to track the ChatBubble

        messagePanel.add(wrapper);
        messagePanel.revalidate();
        messagePanel.repaint();


        SwingUtilities.invokeLater(() ->
            chatScroll.getVerticalScrollBar().setValue(chatScroll.getVerticalScrollBar().getMaximum())
        );
    }


    public void updateMessageStatus(String messageId, String status) {
        JLabel label = statusLabelMap.get(messageId);

        if (label == null) {
            System.out.println("🔁 Deferring status update for messageId: " + messageId + " → " + status);

            // Try again in 200ms (repeat max 3 times)
            new Timer(200, new ActionListener() {
                int retries = 2;
                public void actionPerformed(ActionEvent e) {
                    JLabel retryLabel = statusLabelMap.get(messageId);
                    if (retryLabel != null) {
                        ((Timer) e.getSource()).stop();
                        updateMessageStatus(messageId, status);
                    } else if (retries-- <= 0) {
                        System.out.println("❌ Still no label found after retries for messageId: " + messageId);
                        ((Timer) e.getSource()).stop();
                    }
                }
            }).start();

            return;
        }


        // Skip regressive updates
        String previousStatus = currentStatusMap.getOrDefault(messageId, "None");
        List<String> order = Arrays.asList("Sent", "Delivered", "Seen");

        if (order.indexOf(status) <= order.indexOf(previousStatus)) {
            System.out.println("⏭ Skipping regressive or duplicate update: " + previousStatus + " → " + status);
            return;
        }

        // Update current status
        currentStatusMap.put(messageId, status);

        System.out.println("🔄 Updating message " + messageId + " to status: " + status);
        System.out.println("🧩 All known messageIds: " + statusLabelMap.keySet());

        // Icon file based on status
        String iconName;
        switch (status) {
            case "Delivered": iconName = "delivered.png"; break;
            case "Seen": iconName = "seen.png"; break;
            default: iconName = "sent.png"; break;
        }

        java.net.URL iconURL = getClass().getClassLoader().getResource("icons/" + iconName);
        System.out.println("🔍 Looking for icon at: icons/" + iconName + " | Found: " + (iconURL != null));

        if (iconURL != null) {
            ImageIcon originalIcon = new ImageIcon(iconURL);
            Image scaledImage = originalIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImage);

            int delay = status.equals("Delivered") || status.equals("Seen") ? 400 : 0;

            Timer timer = new Timer(delay, e -> {
                animateStatusIcon(label, icon);
                label.setText("");  // remove emoji fallback
            });
            timer.setRepeats(false);
            timer.start();

            System.out.println("✅ Queued icon update for status: " + status + " after " + delay + "ms");
        } else {
            // Fallback
            Timer timer = new Timer(400, e -> {
                switch (status) {
                    case "Delivered": label.setText("✉"); break;
                    case "Seen": label.setText("👁"); break;
                    default: label.setText("📡"); break;
                }
                label.setIcon(null);
            });
            timer.setRepeats(false);
            
            timer.start();
        }
    }
    
    private void animateStatusIcon(JLabel statusLabel, ImageIcon newIcon) {
        ImageIcon oldIcon = (ImageIcon) statusLabel.getIcon();
        if (oldIcon == null) {
            statusLabel.setIcon(newIcon);
            return;
        }

        final Image oldImage = oldIcon.getImage();
        final Image newImage = newIcon.getImage();

        Timer timer = new Timer(30, null);  // ~33 FPS for smooth transition

        final float[] alpha = {1.0f};
        final boolean[] fadingOut = {true};

        timer.addActionListener(e -> {
            float currentAlpha = alpha[0];
            Image baseImage;

            if (fadingOut[0]) {
                alpha[0] -= 0.1f;
                baseImage = oldImage;

                if (alpha[0] <= 0.0f) {
                    alpha[0] = 0.0f;
                    fadingOut[0] = false;
                }
            } else {
                alpha[0] += 0.1f;
                baseImage = newImage;

                if (alpha[0] >= 1.0f) {
                    alpha[0] = 1.0f;
                    timer.stop();
                }
            }

            Image faded = BounceAnimationUtil.applyAlpha(baseImage, alpha[0]);
            statusLabel.setIcon(new ImageIcon(faded));
        });

        timer.start();
    }

    
    public DataOutputStream getOut() {
        return out;
    }
    
    private String currentChat;
    
    public void setCurrentChat(String chatId) {
        this.currentChat = chatId;
        loadChatHistory(chatId);

        // Check and update Delivered → Seen on chat switch
        for (Map.Entry<String, String> entry : messageSenderMap.entrySet()) {
            String messageId = entry.getKey();
            String sender = entry.getValue();

            if (sender.equalsIgnoreCase(chatId)) {
                String status = currentStatusMap.get(messageId);
                if ("Delivered".equals(status)) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(500); // Optional delay
                            if (out != null) {
                                out.writeUTF("@seen:" + messageId);
                                out.flush();
                                System.out.println("👁️ Seen sent for " + messageId + " on chat switch.");
                            }
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        }
    }


    public String getCurrentChat() {
        return (String) userSelector.getSelectedItem();
    }

    public void showMessage(String senderId, String messageText, String messageId, String status) {
        // Store the bubble in chat history regardless of current view
        ChatBubble bubble = new ChatBubble(messageText, false, messageId, status);
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel statusLabel = new JLabel(" ");  // for future status updates
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        wrapper.add(bubble);
        wrapper.add(statusLabel);
        wrapper.add(Box.createHorizontalGlue());

        // Track it for status updates
        statusLabelMap.put(messageId, statusLabel);
        bubbleMap.put(messageId, bubble);

        // Store in chatHistory
        chatHistory.computeIfAbsent(senderId, k -> new ArrayList<>()).add(wrapper);

        // ✅ Only show on screen if current chat matches
        if (senderId.equals(chatWithUser)) {
            messagePanel.add(wrapper);
            messagePanel.revalidate();
            messagePanel.repaint();

            // Auto-scroll
            SwingUtilities.invokeLater(() ->
                chatScroll.getVerticalScrollBar().setValue(chatScroll.getVerticalScrollBar().getMaximum())
            );
        }
    }

    public void showGroup(String groupName) {
        SwingUtilities.invokeLater(() -> {
            if (((DefaultComboBoxModel<String>) userSelector.getModel()).getIndexOf(groupName) == -1) {
                userSelector.addItem(groupName);
                userGroups.add(groupName);
                addMessageBubble(UUID.randomUUID().toString(), "You were added to group: " + groupName, false);
            }
        });
    }
    
    public void showSystemMessage(String message, boolean isSentByMe) {
        String messageId = java.util.UUID.randomUUID().toString();
        addMessageBubble(messageId, message, isSentByMe);
    }
}
