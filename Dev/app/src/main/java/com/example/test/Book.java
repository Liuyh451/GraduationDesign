package com.example.test;

//这个类是为六宫格服务的
public class Book {
    private String title;
    private String author;
    private String coverUrl;
    private String price;
    private String rating;
    private String bookId;
    private int pages;

    public Book(String title, String author, String coverUrl,String price,String bookId,String rating) {
        this.title = title;
        this.author = author;
        this.coverUrl = coverUrl;
        this.price=price;
        this.rating=rating;
        this.bookId=bookId;

    }

    // getters and setters

    public String getBookId() {
        return bookId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }
    public String getPrice(){return price;}
    public void setPrice(String price) {
        this.price = price;
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

}