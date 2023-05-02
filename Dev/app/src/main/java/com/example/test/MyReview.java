package com.example.test;

import android.graphics.drawable.Drawable;

public class MyReview {
    private String title;
    private String author;
    private String bookCover;

    private String reviewContent;

    public MyReview(String title, String author, String bookCover, String reviewContent) {
        this.title = title;
        this.author = author;
        this.bookCover = bookCover;
        this.reviewContent = reviewContent;
    }

    // Getter for title property
    public String getTitle() {
        return title;
    }

    // Setter for title property
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for author property
    public String getAuthor() {
        return author;
    }

    // Setter for author property
    public void setAuthor(String author) {
        this.author = author;
    }

    // Getter for bookCover property
    public String getBookCover() {
        return bookCover;
    }

    // Setter for bookCover property
    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    // Getter for reviewContent property
    public String getReviewContent() {
        return reviewContent;
    }

    // Setter for reviewContent property
    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }
}