package com.example.test;

import java.util.TreeMap;

public class Novel {
    private  String novelId;
    private String title;
    private String imageUrl;
    private String author;
    private String description;

    public Novel(String novelId,String title, String imageUrl, String author, String description) {
        this.novelId=novelId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.author = author;
        this.description = description;
    }

    public String getNovelId() {
        return novelId;
    }

    public void setNovelId(String novelId) {
        this.novelId = novelId;
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