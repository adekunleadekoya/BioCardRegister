package com.example.utils;

public class UserCredentials {
    private static String username;
    private static String password;

    // Getter for username
    public static String getUsername() {
        return username;
    }

    // Setter for username
    public static void setUsername(String username) {
        UserCredentials.username = username;
    }

    // Getter for password
    public static String getPassword() {
        return password;
    }

    // Setter for password
    public static void setPassword(String password) {
        UserCredentials.password = password;
    }

      // Method to clear credentials
    public static void clearCredentials() {
        username = null;
        password = null;
    }
}

  
 