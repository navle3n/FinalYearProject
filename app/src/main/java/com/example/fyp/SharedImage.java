package com.example.fyp;

public class SharedImage {
    private String imageUrl; // URL to the image stored in Firebase Storage
    private String fromUser; // Email of the user who shared the image
    private String toUser; // Email of the recipient
    private String timestamp; // When the image was shared

    // Default constructor is required for Firebase's automatic data mapping
    public SharedImage() {
    }

    public SharedImage(String imageUrl, String fromUser, String toUser, String timestamp) {
        this.imageUrl = imageUrl;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.timestamp = timestamp;
    }

    // Getters
    public String getImageUrl() {
        return imageUrl;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public String getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
