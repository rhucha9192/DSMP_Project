package com.chatapp;

import java.io.*;
import java.net.*;

public class client {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);
            System.out.println("Connected to the server.");

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Receive AES Key from Server
            String secretKey = dis.readUTF();
            AESutil.setSecretKey(secretKey);
            System.out.println("Received AES Secret Key from Server.");

            while (true) {
                // Send message
                System.out.print("You: ");
                String message = reader.readLine();
                String encryptedMessage = AESutil.encrypt(message);
                dos.writeUTF(encryptedMessage);

                // Receive response
                String encryptedResponse = dis.readUTF();
                String decryptedResponse = AESutil.decrypt(encryptedResponse);
                System.out.println("Server (Decrypted): " + decryptedResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
