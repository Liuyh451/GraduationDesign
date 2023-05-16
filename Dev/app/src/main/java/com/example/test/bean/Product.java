package com.example.test.bean;

public class Product {
    private String userId; // 用户id
    private String name; // 商品名
    private String image; // 图片
    private String price; // 价格
    private int quantity; // 数量
    private String bookId;

    public Product(String userId, String name, String image, String price, int quantity, String bookId) {
        this.userId = userId;
        this.name = name;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

