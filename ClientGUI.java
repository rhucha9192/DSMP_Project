package com.chatapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class ClientGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public ClientGUI() {
        setTitle("Client Chat");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        messageField = new JTextField();
        sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        setVisible(true);
        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 1234);
            chatArea.append("Connected to server.\n");

            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            String secretKey = input.readUTF();
            AESutil.setSecretKey(secretKey);

            new Thread(() -> receiveMessages()).start();
        } catch (Exception e) {
            chatArea.append("Error: " + e.getMessage() + "\n");
        }
    }

    private void sendMessage() {
        try {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                String encryptedMessage = AESutil.encrypt(message);
                output.writeUTF(encryptedMessage);
                chatArea.append("You: " + message + "\n");
                messageField.setText("");
            }
        } catch (Exception e) {
            chatArea.append("Error: " + e.getMessage() + "\n");
        }
    }

    private void receiveMessages() {
        try {
            while (true) {
                String encryptedMessage = input.readUTF();
                String decryptedMessage = AESutil.decrypt(encryptedMessage);
                chatArea.append("Server: " + decryptedMessage + "\n");
            }
        } catch (Exception e) {
            chatArea.append("Connection closed.\n");
        }
    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}
