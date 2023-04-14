package com.example.test;

public class Novel {
    private String title;
    private String imageUrl;
    private String author;
    private String description;

    public Novel(String title, String imageUrl, String author, String description) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.author = author;
        this.description = description;
    }

    // getter方法
    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    // setter方法
    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}