package com.example.onlineshop.model;

public class Client {
    public int clientID;
    public String clientName;
    public String password;
    public String phone;
    public static int countUser = 0;

    public Client(int clientID, String clientName, String password, String phone) {
        this.clientID = clientID;
        this.clientName = clientName;
        this.password = password;
        this.phone = phone;
    }

    public Client() {

    }
}
