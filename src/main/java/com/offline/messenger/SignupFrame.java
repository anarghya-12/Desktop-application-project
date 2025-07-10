///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//
//package com.offline.messenger;
//
//
///**
// *
// * @author ritid
// */
//
//
//
//import javax.swing.*;
//import java.awt.*;
//
//public class SignupFrame extends JFrame {
//    private JTextField usernameField;
//    private JPasswordField passwordField;
//    private JButton signupButton, backButton;
//    private DBHelper dbHelper;
//
//    public SignupFrame() {
//        dbHelper = new DBHelper();
//        setTitle("Sign Up");
//        setSize(300, 200);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLayout(new GridLayout(4, 2, 10, 10));
//
//        add(new JLabel("Username:"));
//        usernameField = new JTextField();
//        add(usernameField);
//
//        add(new JLabel("Password:"));
//        passwordField = new JPasswordField();
//        add(passwordField);
//
//        signupButton = new JButton("Sign Up");
//        backButton = new JButton("Back to Login");
//
//        add(signupButton);
//        add(backButton);
//
//        signupButton.addActionListener(e -> register());
//        backButton.addActionListener(e -> backToLogin());
//    }
//
//    private void register() {
//        String username = usernameField.getText();
//        String password = String.valueOf(passwordField.getPassword());
//
//        if (username.isEmpty() || password.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Please fill all fields.");
//            return;
//        }
//
//        if (dbHelper.registerUser(username, password)) {
//            JOptionPane.showMessageDialog(this, "Registration successful! Go to Login.");
//        } else {
//            JOptionPane.showMessageDialog(this, "Username already exists!");
//        }
//    }
//
//    private void backToLogin() {
//        this.dispose();
//        new LoginFrame().setVisible(true);
//    }
//}
//////
//package com.offline.messenger;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class SignupFrame extends JFrame {
//    private JTextField usernameField;
//    private JPasswordField passwordField;
//    private JButton signupButton, backButton;
//    private DBHelper dbHelper;
//
//    public SignupFrame() {
//        dbHelper = new DBHelper();
//        setTitle("Sign Up");
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        // Get screen size and calculate scale factor
//        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
//        int width = (int) (screen.width * 0.6);
//        int height = (int) (screen.height * 0.6);
//        double scale = screen.width / 1280.0;
//
//        setSize(width, height);
//        setLocationRelativeTo(null);
//
//        // Main layout panel
//        JPanel mainPanel = new JPanel(new GridBagLayout());
//        mainPanel.setBackground(new Color(34, 34, 34));
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets((int)(30 * scale), (int)(30 * scale), (int)(30 * scale), (int)(30 * scale));
//
//        Font labelFont = new Font("SansSerif", Font.PLAIN, (int)(26 * scale));
//        Font fieldFont = new Font("SansSerif", Font.PLAIN, (int)(20 * scale));
//        Font buttonFont = new Font("SansSerif", Font.BOLD, (int)(18 * scale));
//
//        // Username label
//        JLabel userLabel = new JLabel("Username:");
//        userLabel.setForeground(Color.WHITE);
//        userLabel.setFont(labelFont);
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.anchor = GridBagConstraints.LINE_END;
//        mainPanel.add(userLabel, gbc);
//
//        // Username field
//        usernameField = new JTextField(18);
//        styleTextField(usernameField, fieldFont, scale);
//        gbc.gridx = 1;
//        gbc.anchor = GridBagConstraints.LINE_START;
//        mainPanel.add(usernameField, gbc);
//
//        // Password label
//        JLabel passLabel = new JLabel("Password:");
//        passLabel.setForeground(Color.WHITE);
//        passLabel.setFont(labelFont);
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        gbc.anchor = GridBagConstraints.LINE_END;
//        mainPanel.add(passLabel, gbc);
//
//        // Password field
//        passwordField = new JPasswordField(18);
//        styleTextField(passwordField, fieldFont, scale);
//        gbc.gridx = 1;
//        gbc.anchor = GridBagConstraints.LINE_START;
//        mainPanel.add(passwordField, gbc);
//
//        // Button panel
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, (int)(40 * scale), (int)(20 * scale)));
//        buttonPanel.setBackground(new Color(34, 34, 34));
//
//        signupButton = createStyledButton("Sign Up", buttonFont, scale);
//        backButton = createStyledButton("Back to Login", buttonFont, scale);
//
//        signupButton.addActionListener(e -> register());
//        backButton.addActionListener(e -> backToLogin());
//
//        buttonPanel.add(signupButton);
//        buttonPanel.add(backButton);
//
//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        gbc.gridwidth = 2;
//        gbc.anchor = GridBagConstraints.CENTER;
//        mainPanel.add(buttonPanel, gbc);
//
//        setContentPane(mainPanel);
//        setVisible(true);
//    }
//
//    private void register() {
//        String username = usernameField.getText();
//        String password = String.valueOf(passwordField.getPassword());
//
//        if (username.isEmpty() || password.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Please fill all fields.");
//            return;
//        }
//
//        if (dbHelper.registerUser(username, password)) {
//            JOptionPane.showMessageDialog(this, "Registration successful! Go to Login.");
//        } else {
//            JOptionPane.showMessageDialog(this, "Username already exists!");
//        }
//    }
//
//    private void backToLogin() {
//        this.dispose();
//        new LoginFrame().setVisible(true);
//    }
//
//    private void styleTextField(JTextField field, Font font, double scale) {
//        field.setFont(font);
//        field.setBackground(new Color(245, 245, 245));
//        field.setForeground(Color.BLACK);
//        field.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createEmptyBorder((int)(10 * scale), (int)(14 * scale), (int)(10 * scale), (int)(14 * scale)),
//                BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
//        ));
//    }
//
//    private JButton createStyledButton(String text, Font font, double scale) {
//        JButton button = new JButton(text);
//        button.setFont(font);
//        button.setFocusPainted(false);
//        button.setBackground(new Color(34, 34, 34));
//        button.setForeground(Color.WHITE);
//        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
//        button.setPreferredSize(new Dimension((int)(180 * scale), (int)(50 * scale)));
//        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//
//        // Hover effect
//        button.addMouseListener(new java.awt.event.MouseAdapter() {
//            public void mouseEntered(java.awt.event.MouseEvent e) {
//                button.setBackground(Color.DARK_GRAY);
//            }
//
//            public void mouseExited(java.awt.event.MouseEvent e) {
//                button.setBackground(new Color(34, 34, 34));
//            }
//        });
//
//        return button;
//    }
//}
//


package com.offline.messenger;

import javax.swing.*;
import java.awt.*;

public class SignupFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signupButton, backButton;
    private DBHelper dbHelper;

    public SignupFrame() {
        dbHelper = new DBHelper();
        setTitle("Sign Up");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ✅ Open in full screen and allow resizing
        setExtendedState(JFrame.MAXIMIZED_BOTH);      // Start maximized
        setResizable(true);                           // Allow resize
        setMinimumSize(new Dimension(800, 600));      // Optional minimum size

        // Get screen size and calculate scale factor
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        double scale = screen.width / 1280.0;

        // Main layout panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(34, 34, 34));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets((int)(30 * scale), (int)(30 * scale), (int)(30 * scale), (int)(30 * scale));

        Font labelFont = new Font("SansSerif", Font.PLAIN, (int)(26 * scale));
        Font fieldFont = new Font("SansSerif", Font.PLAIN, (int)(20 * scale));
        Font buttonFont = new Font("SansSerif", Font.BOLD, (int)(18 * scale));

        // Username label
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(userLabel, gbc);

        // Username field
        usernameField = new JTextField(18);
        styleTextField(usernameField, fieldFont, scale);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(usernameField, gbc);

        // Password label
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(passLabel, gbc);

        // Password field
        passwordField = new JPasswordField(18);
        styleTextField(passwordField, fieldFont, scale);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(passwordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, (int)(40 * scale), (int)(20 * scale)));
        buttonPanel.setBackground(new Color(34, 34, 34));

        signupButton = createStyledButton("Sign Up", buttonFont, scale);
        backButton = createStyledButton("Back to Login", buttonFont, scale);

        signupButton.addActionListener(e -> register());
        backButton.addActionListener(e -> backToLogin());

        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        setContentPane(mainPanel);
        setVisible(true);

        // ✅ Responsive layout on window resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                SwingUtilities.invokeLater(() -> {
                    mainPanel.revalidate();
                    mainPanel.repaint();
                });
            }
        });
    }

    private void register() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        if (dbHelper.registerUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Registration successful! Go to Login.");
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists!");
        }
    }

    private void backToLogin() {
        this.dispose();
        new LoginFrame().setVisible(true);
    }

    private void styleTextField(JTextField field, Font font, double scale) {
        field.setFont(font);
        field.setBackground(new Color(245, 245, 245));
        field.setForeground(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder((int)(10 * scale), (int)(14 * scale), (int)(10 * scale), (int)(14 * scale)),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
        ));
    }

    private JButton createStyledButton(String text, Font font, double scale) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBackground(new Color(34, 34, 34));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        button.setPreferredSize(new Dimension((int)(180 * scale), (int)(50 * scale)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(Color.DARK_GRAY);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(34, 34, 34));
            }
        });

        return button;
    }
}
