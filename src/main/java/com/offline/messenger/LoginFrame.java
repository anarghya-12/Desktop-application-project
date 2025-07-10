///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//
//
//
//package com.offline.messenger;
//
///**
// *
// * @author ritid
// */
//
//import javax.swing.*;
//import java.awt.*;
//
//public class LoginFrame extends JFrame {
//    private JTextField usernameField;
//    private JPasswordField passwordField;
//    private JButton loginButton, signupButton;
//    private DBHelper dbHelper;
//
//    public LoginFrame() {
//        dbHelper = new DBHelper();
//        setTitle("Login");
//        setSize(300, 200);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
//        loginButton = new JButton("Login");
//        signupButton = new JButton("Sign Up");
//
//        add(loginButton);
//        add(signupButton);
//
//        loginButton.addActionListener(e -> login());
//        signupButton.addActionListener(e -> openSignup());
//    }
//
//    private void login() {
//        String username = usernameField.getText();
//        String password = String.valueOf(passwordField.getPassword());
//
//        if (dbHelper.validateLogin(username, password)) {
//            JOptionPane.showMessageDialog(this, "Login successful!");
//            this.dispose();
//            new ChatFrame(username).setVisible(true);
//        } else {
//            JOptionPane.showMessageDialog(this, "Invalid username or password!");
//        }
//    }
//
//    private void openSignup() {
//        this.dispose();
//        new SignupFrame().setVisible(true);
//    }
//}

//
//package com.offline.messenger;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class LoginFrame extends JFrame {
//    private JTextField usernameField;
//    private JPasswordField passwordField;
//    private JButton loginButton, signupButton;
//    private DBHelper dbHelper;
//
//    public LoginFrame() {
//        dbHelper = new DBHelper();
//        setTitle("Login");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        // Fullscreen scaling
//        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
//        int width = (int) (screen.width * 0.6);
//        int height = (int) (screen.height * 0.6);
//        double scale = screen.width / 1280.0;
//
//        setSize(width, height);
//        setLocationRelativeTo(null);
//
//        // Layout setup
//        JPanel mainPanel = new JPanel(new GridBagLayout());
//        mainPanel.setBackground(new Color(34, 34, 34));
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets((int)(30 * scale), (int)(30 * scale), (int)(30 * scale), (int)(30 * scale));
//
//        Font labelFont = new Font("SansSerif", Font.PLAIN, (int)(26 * scale));
//        Font fieldFont = new Font("SansSerif", Font.PLAIN, (int)(20 * scale));
//        Font buttonFont = new Font("SansSerif", Font.BOLD, (int)(18 * scale));
//
//        // Username
//        JLabel userLabel = new JLabel("Username:");
//        userLabel.setForeground(Color.WHITE);
//        userLabel.setFont(labelFont);
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.anchor = GridBagConstraints.LINE_END;
//        mainPanel.add(userLabel, gbc);
//
//        usernameField = new JTextField(18);
//        styleTextField(usernameField, fieldFont, scale);
//        gbc.gridx = 1;
//        gbc.anchor = GridBagConstraints.LINE_START;
//        mainPanel.add(usernameField, gbc);
//
//        // Password
//        JLabel passLabel = new JLabel("Password:");
//        passLabel.setForeground(Color.WHITE);
//        passLabel.setFont(labelFont);
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        gbc.anchor = GridBagConstraints.LINE_END;
//        mainPanel.add(passLabel, gbc);
//
//        passwordField = new JPasswordField(18);
//        styleTextField(passwordField, fieldFont, scale);
//        gbc.gridx = 1;
//        gbc.anchor = GridBagConstraints.LINE_START;
//        mainPanel.add(passwordField, gbc);
//
//        // Buttons
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, (int)(40 * scale), (int)(20 * scale)));
//        buttonPanel.setBackground(new Color(34, 34, 34));
//
//        signupButton = createStyledButton("Sign Up", buttonFont, Color.WHITE, true, scale);
//        loginButton = createStyledButton("Login", buttonFont, new Color(151, 117, 255), false, scale);
//
//        signupButton.addActionListener(e -> openSignup());
//        loginButton.addActionListener(e -> login());
//
//        buttonPanel.add(signupButton);
//        buttonPanel.add(loginButton);
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
//    private void login() {
//        String username = usernameField.getText();
//        String password = String.valueOf(passwordField.getPassword());
//
//        if (dbHelper.validateLogin(username, password)) {
//            JOptionPane.showMessageDialog(this, "Login successful!");
//            this.dispose();
//            new ChatFrame(username).setVisible(true);
//        } else {
//            JOptionPane.showMessageDialog(this, "Invalid username or password!");
//        }
//    }
//
//    private void openSignup() {
//        this.dispose();
//        new SignupFrame().setVisible(true);
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
//    private JButton createStyledButton(String text, Font font, Color borderColor, boolean isWhiteBorder, double scale) {
//        JButton button = new JButton(text);
//        button.setFont(font);
//        button.setFocusPainted(false);
//        button.setBackground(new Color(34, 34, 34));
//        button.setForeground(Color.WHITE);
//        button.setPreferredSize(new Dimension((int)(180 * scale), (int)(50 * scale)));
//        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//
//        if (isWhiteBorder) {
//            button.setBorder(BorderFactory.createLineBorder(borderColor));
//        } else {
//            button.setBorder(BorderFactory.createLineBorder(borderColor));
//        }
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




package com.offline.messenger;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton;
    private DBHelper dbHelper;

    public LoginFrame() {
        dbHelper = new DBHelper();
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ✅ Fullscreen and responsive behavior
        setExtendedState(JFrame.MAXIMIZED_BOTH);   // Start maximized
        setResizable(true);                        // Allow resize
        setMinimumSize(new Dimension(800, 600));   // Minimum size

        // Fullscreen scaling
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        double scale = screen.width / 1280.0;

        // Layout setup
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(34, 34, 34));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets((int)(30 * scale), (int)(30 * scale), (int)(30 * scale), (int)(30 * scale));

        Font labelFont = new Font("SansSerif", Font.PLAIN, (int)(26 * scale));
        Font fieldFont = new Font("SansSerif", Font.PLAIN, (int)(20 * scale));
        Font buttonFont = new Font("SansSerif", Font.BOLD, (int)(18 * scale));

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(userLabel, gbc);

        usernameField = new JTextField(18);
        styleTextField(usernameField, fieldFont, scale);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(usernameField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(18);
        styleTextField(passwordField, fieldFont, scale);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(passwordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, (int)(40 * scale), (int)(20 * scale)));
        buttonPanel.setBackground(new Color(34, 34, 34));

        signupButton = createStyledButton("Sign Up", buttonFont, Color.WHITE, true, scale);
        loginButton = createStyledButton("Login", buttonFont, new Color(151, 117, 255), false, scale);

        signupButton.addActionListener(e -> openSignup());
        loginButton.addActionListener(e -> login());

        buttonPanel.add(signupButton);
        buttonPanel.add(loginButton);

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

    private void login() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if (dbHelper.validateLogin(username, password)) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            this.dispose();
            new ChatFrame(username).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!");
        }
    }

    private void openSignup() {
        this.dispose();
        new SignupFrame().setVisible(true);
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

    private JButton createStyledButton(String text, Font font, Color borderColor, boolean isWhiteBorder, double scale) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBackground(new Color(34, 34, 34));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension((int)(180 * scale), (int)(50 * scale)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.setBorder(BorderFactory.createLineBorder(borderColor));

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

