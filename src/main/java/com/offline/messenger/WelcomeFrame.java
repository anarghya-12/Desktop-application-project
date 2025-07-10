///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.offline.messenger;
///**
// *
// * @author ritid
// */
//
//import javax.swing.*;
//import java.awt.*;
//
//public class WelcomeFrame extends JFrame {
//    private DBHelper dbHelper;
//    private JLabel userCountLabel;
//    private JButton loginButton, signupButton;
//
//    public WelcomeFrame() {
//        dbHelper = new DBHelper();
//
//        setTitle("Welcome to Offline Messenger");
//        setSize(400, 200);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLayout(new BorderLayout());
//
//        userCountLabel = new JLabel();
//        userCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        updateUserCount();
//
//        loginButton = new JButton("Login");
//        signupButton = new JButton("Sign Up");
//
//        JPanel buttonPanel = new JPanel();
//        buttonPanel.add(loginButton);
//        buttonPanel.add(signupButton);
//
//        add(userCountLabel, BorderLayout.CENTER);
//        add(buttonPanel, BorderLayout.SOUTH);
//
//        loginButton.addActionListener(e -> openLogin());
//        signupButton.addActionListener(e -> openSignup());
//    }
//
//    private void updateUserCount() {
//        int count = dbHelper.getUserCount();
//        userCountLabel.setText("<html><h1>Welcome to Offline Messenger</h1><br>Total Registered Users: " + count + "</html>");
//    }
//
//    private void openLogin() {
//        this.dispose();
//        new LoginFrame().setVisible(true);
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
//public class WelcomeFrame extends JFrame {
//
//    public WelcomeFrame() {
//        setTitle("Offline Messenger");
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//
//        // Fullscreen-like size for large displays
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        setSize((int)(screenSize.width * 0.8), (int)(screenSize.height * 0.8));
//        setLocationRelativeTo(null);
//
//        // Set root layout
//        JPanel rootPanel = new JPanel(new GridBagLayout());
//        rootPanel.setBackground(new Color(34, 34, 34)); // Dark mode
//
//        // Content panel with vertical layout
//        JPanel contentPanel = new JPanel();
//        contentPanel.setBackground(new Color(34, 34, 34));
//        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
//        contentPanel.setPreferredSize(new Dimension(600, 500));
//
//        // Determine dynamic scaling factor
//        int baseWidth = 1280;
//        double scale = screenSize.width / (double) baseWidth;
//
//        // Components
//        JLabel welcomeLabel = new JLabel("Welcome to");
//        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, (int)(28 * scale)));
//        welcomeLabel.setForeground(new Color(151, 117, 255));
//        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        JLabel titleLabel = new JLabel("Offline");
//        titleLabel.setFont(new Font("SansSerif", Font.BOLD, (int)(72 * scale)));
//        titleLabel.setForeground(Color.WHITE);
//        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        JLabel subtitleLabel = new JLabel("Messenger!!");
//        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, (int)(36 * scale)));
//        subtitleLabel.setForeground(Color.LIGHT_GRAY);
//        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
//        buttonPanel.setBackground(new Color(34, 34, 34));
//
//        JButton signupButton = createStyledButton("Sign Up", Color.BLACK, Color.WHITE, true, scale);
//        JButton loginButton = createStyledButton("Log in", new Color(124, 77, 255), Color.WHITE, false, scale);
//
//        signupButton.addActionListener(e -> {
//            dispose();
//            new SignupFrame().setVisible(true);
//        });
//
//        loginButton.addActionListener(e -> {
//            dispose();
//            new LoginFrame().setVisible(true);
//        });
//
//        buttonPanel.add(signupButton);
//        buttonPanel.add(loginButton);
//
//        // Add spacing + components
//        contentPanel.add(Box.createVerticalGlue());
//        contentPanel.add(welcomeLabel);
//        contentPanel.add(Box.createVerticalStrut((int)(10 * scale)));
//        contentPanel.add(titleLabel);
//        contentPanel.add(Box.createVerticalStrut((int)(10 * scale)));
//        contentPanel.add(subtitleLabel);
//        contentPanel.add(Box.createVerticalStrut((int)(30 * scale)));
//        contentPanel.add(buttonPanel);
//        contentPanel.add(Box.createVerticalGlue());
//
//        // Center content panel
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.anchor = GridBagConstraints.CENTER;
//        rootPanel.add(contentPanel, gbc);
//
//        setContentPane(rootPanel);
//        setVisible(true);
//    }
//
//    private JButton createStyledButton(String text, Color bgColor, Color fgColor, boolean bordered, double scale) {
//        JButton button = new JButton(text);
//        button.setFont(new Font("SansSerif", Font.BOLD, (int)(18 * scale)));
//        button.setBackground(bgColor);
//        button.setForeground(fgColor);
//        button.setFocusPainted(false);
//        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//
//        if (bordered) {
//            button.setBorder(BorderFactory.createLineBorder(fgColor));
//        } else {
//            button.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
//        }
//
//        button.setPreferredSize(new Dimension((int)(150 * scale), (int)(50 * scale)));
//
//        // Hover effect
//        button.addMouseListener(new java.awt.event.MouseAdapter() {
//            public void mouseEntered(java.awt.event.MouseEvent evt) {
//                button.setBackground(bgColor.darker());
//            }
//
//            public void mouseExited(java.awt.event.MouseEvent evt) {
//                button.setBackground(bgColor);
//            }
//        });
//
//        return button;
//    }
//}




package com.offline.messenger;

import javax.swing.*;
import java.awt.*;

public class WelcomeFrame extends JFrame {

    public WelcomeFrame() {
        setTitle("Offline Messenger");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ✅ Open in full screen and allow resizing
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Start maximized
        setResizable(true);                       // Allow resize
        setMinimumSize(new Dimension(800, 600));  // Optional minimum size

        // Get screen size and scaling factor
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int baseWidth = 1280;
        double scale = screenSize.width / (double) baseWidth;

        // Root layout
        JPanel rootPanel = new JPanel(new GridBagLayout());
        rootPanel.setBackground(new Color(34, 34, 34)); // Dark theme

        // Content panel with vertical layout
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(34, 34, 34));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setPreferredSize(new Dimension((int)(600 * scale), (int)(500 * scale)));

        // Labels
        JLabel welcomeLabel = new JLabel("Welcome to");
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, (int)(28 * scale)));
        welcomeLabel.setForeground(new Color(151, 117, 255));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Offline");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, (int)(72 * scale)));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Messenger!!");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, (int)(36 * scale)));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        buttonPanel.setBackground(new Color(34, 34, 34));

        JButton signupButton = createStyledButton("Sign Up", Color.BLACK, Color.WHITE, true, scale);
        JButton loginButton = createStyledButton("Log in", new Color(124, 77, 255), Color.WHITE, false, scale);

        signupButton.addActionListener(e -> {
            dispose();
            new SignupFrame().setVisible(true);
        });

        loginButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        buttonPanel.add(signupButton);
        buttonPanel.add(loginButton);

        // Assemble content
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(welcomeLabel);
        contentPanel.add(Box.createVerticalStrut((int)(10 * scale)));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut((int)(10 * scale)));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut((int)(30 * scale)));
        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalGlue());

        // Center content
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        rootPanel.add(contentPanel, gbc);

        setContentPane(rootPanel);
        setVisible(true);

        // ✅ Responsive behavior when window is resized
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                SwingUtilities.invokeLater(() -> {
                    contentPanel.revalidate();
                    contentPanel.repaint();
                });
            }
        });
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor, boolean bordered, double scale) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, (int)(18 * scale)));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (bordered) {
            button.setBorder(BorderFactory.createLineBorder(fgColor));
        } else {
            button.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }

        button.setPreferredSize(new Dimension((int)(150 * scale), (int)(50 * scale)));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }
}
