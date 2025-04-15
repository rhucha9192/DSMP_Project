package com.chatapp;

import java.io.*;
import java.net.*;

public class server {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Server started. Waiting for client...");

            Socket socket = serverSocket.accept();
            System.out.println("Client connected.");

            // Initialize AES Encryption
            AESutil.generateKey();
            String secretKey = AESutil.getSecretKey();
            System.out.println("Generated AES Secret Key: " + secretKey);

            // Send secret key to client
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(secretKey);

            // Setup streams
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                // Receive encrypted message from client
                String encryptedMessage = dis.readUTF();
                String decryptedMessage = AESutil.decrypt(encryptedMessage);
                System.out.println("Client (Decrypted): " + decryptedMessage);

                // Send response
                System.out.print("You: ");
                String message = reader.readLine();
                String encryptedResponse = AESutil.encrypt(message);
                dos.writeUTF(encryptedResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
