package com.example.onlineshop.model;

public class ClientCart {
    public int cartID;
    public int clientID;
    public int productID;
    public int quantity;

    public ClientCart(int cartID, int clientID, int productID, int quantity) {
        this.cartID = cartID;
        this.clientID = clientID;
        this.productID = productID;
        this.quantity = quantity;
    }
}
