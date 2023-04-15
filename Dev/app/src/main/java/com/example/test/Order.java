package com.example.test;
public class Order {
    private String orderNumber;
    private String bookCover;
    private String bookAuthor;
    private String bookTitle;
    private double price;
    private String buyerName;
    private int quantity;
    private double totalPrice;
    private String address;

    public Order(String orderNumber, String bookCover, String bookAuthor, String bookTitle, double price,
                 String buyerName, int quantity, double totalPrice, String address) {
        this.orderNumber = orderNumber;
        this.bookCover = bookCover;
        this.bookAuthor = bookAuthor;
        this.bookTitle = bookTitle;
        this.price = price;
        this.buyerName = buyerName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.address = address;
    }

    // getters and setters

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
