package com.example.test;
//这个类是为RecyclerView服务的，区别于book
//我把bookId从int改为了String类型的，相关联的还有对应的适配器
public class Books {
    private String title;
    private String author;
    private String coverUrl;
    private String bookId;

    public Books(String title, String author, String coverUrl,String bookId) {
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

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}

