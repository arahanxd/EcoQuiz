package com.example.ecoquiz.model;

/**
 * Model class representing a User entity.
 */
public class User {
    private int userId;
    private String name;
    private String username;
    private String email;
    private String passwordHash;
    private String salt;

    public User() {
    }

    public User(int userId, String name, String username, String email, String passwordHash, String salt) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
