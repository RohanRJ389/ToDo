package com.example.todoappfx;

import java.util.Base64;

public class passwordEncoder {
    public String encodePassword(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public String decodePassword(String encodedInput) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedInput);
        return new String(decodedBytes);
    }

    public static void main(String[] args) {
        passwordEncoder encoder = new passwordEncoder();

        // Encoding a password
        String password = "helloworld";
        String encodedPassword = encoder.encodePassword(password);
        System.out.println("Encoded Password: " + encodedPassword);

        // Decoding a password
        String decodedPassword = encoder.decodePassword(encodedPassword);
        System.out.println("Decoded Password: " + decodedPassword);
    }
}
