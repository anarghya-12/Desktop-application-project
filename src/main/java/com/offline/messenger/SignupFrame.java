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

public class SignupFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signupButton, backButton;
    private DBHelper dbHelper;

    public SignupFrame() {
        dbHelper = new DBHelper();
        setTitle("Sign Up");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        signupButton = new JButton("Sign Up");
        backButton = new JButton("Back to Login");

        add(signupButton);
        add(backButton);

        signupButton.addActionListener(e -> register());
        backButton.addActionListener(e -> backToLogin());
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
}

