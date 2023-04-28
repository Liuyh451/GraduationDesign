package com.example.test;

public class Order {
    private int orderNumber;
    private String bookCover;
    private String bookAuthor;
    private String bookTitle;
    private double price;
    private String buyerName;
    private int quantity;
    private double totalPrice;
    private String address;
    private String phone;

    public Order(int orderNumber, String bookCover, String bookAuthor, String bookTitle, double price,
                 String buyerName, int quantity, double totalPrice, String address, String phone) {
        this.orderNumber = orderNumber;
        this.bookCover = bookCover;
        this.bookAuthor = bookAuthor;
        this.bookTitle = bookTitle;
        this.price = price;
        this.buyerName = buyerName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.address = address;
        this.phone = phone;
    }

    // getters and setters

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
