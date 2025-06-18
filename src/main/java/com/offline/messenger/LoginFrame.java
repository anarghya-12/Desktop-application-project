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

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton;
    private DBHelper dbHelper;

    public LoginFrame() {
        dbHelper = new DBHelper();
        setTitle("Login");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");

        add(loginButton);
        add(signupButton);

        loginButton.addActionListener(e -> login());
        signupButton.addActionListener(e -> openSignup());
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
}
