package com.example.onlineshop.model;

public class Product {
    public int productID;
    public String productName;
    public String description;
    public double price;
    public String imagePath;

    public Product(int productID, String productName, String description, double price, String imagePath) {
        this.productID = productID;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.imagePath = imagePath;
    }

    public Product() {

    }
}
