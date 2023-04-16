package com.example.test;


public class Books {
    private String title;
    private String author;
    private String coverUrl;
    private int bookId;

    public Books(String title, String author, String coverUrl,int bookId) {
        this.title = title;
        this.author = author;
        this.coverUrl = coverUrl;
        this.bookId=bookId;

    }

    // getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
}

