/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.offline.messenger;
/**
 *
 * @author ritid
 */

import javax.swing.*;
import java.awt.*;

public class WelcomeFrame extends JFrame {
    private DBHelper dbHelper;
    private JLabel userCountLabel;
    private JButton loginButton, signupButton;

    public WelcomeFrame() {
        dbHelper = new DBHelper();

        setTitle("Welcome to Offline Messenger");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        userCountLabel = new JLabel();
        userCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateUserCount();

        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        add(userCountLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> openLogin());
        signupButton.addActionListener(e -> openSignup());
    }

    private void updateUserCount() {
        int count = dbHelper.getUserCount();
        userCountLabel.setText("<html><h1>Welcome to Offline Messenger</h1><br>Total Registered Users: " + count + "</html>");
    }

    private void openLogin() {
        this.dispose();
        new LoginFrame().setVisible(true);
    }

    private void openSignup() {
        this.dispose();
        new SignupFrame().setVisible(true);
    }
}

