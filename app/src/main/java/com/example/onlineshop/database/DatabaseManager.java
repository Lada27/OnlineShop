package com.example.onlineshop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.onlineshop.model.Client;
import com.example.onlineshop.model.CurrentUser;
import com.example.onlineshop.model.Product;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static DatabaseManager instance;
    private static DatabaseHelper dbHelper;
    private static SQLiteDatabase database;

    private DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context.getApplicationContext());
            instance.open();
        }
        return instance;
    }

    public static int getProoductID(String productName) {
        int productID = -1;
        Cursor cursor = null;
        try {
            String query = "SELECT ProductID FROM Products WHERE ProductName = ?";
            cursor = database.rawQuery(query, new String[]{productName});
            if (cursor != null && cursor.moveToFirst()) {
                int productIdIndex = cursor.getColumnIndex("ProductID");
                if (productIdIndex != -1) {
                    productID = cursor.getInt(productIdIndex);
                } else {
                    throw new SQLException("Столбец 'ProductID' не найден в результирующем наборе.");
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return productID;
    }

    public void open() {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    public void addUser(String username, String password, String phoneNumber) {
        ContentValues values = new ContentValues();
        values.put("ClientName", username);
        values.put("Password", password);
        values.put("PhoneNumber", phoneNumber);
        database.insert("Clients", null, values);
    }

    public boolean checkUser(String username, String password) {
        Cursor cursor = null;
        try {
            String query = "SELECT COUNT(*) FROM Clients WHERE ClientName = ? AND Password = ?";
            cursor = database.rawQuery(query, new String[]{username, password});
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                return count > 0;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    public Client getUserData(String username) {
        Client user = null;
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM Clients WHERE ClientName = ?";
            cursor = database.rawQuery(query, new String[]{username});
            if (cursor != null && cursor.moveToFirst()) {
                int clientNameIndex = cursor.getColumnIndex("ClientName");
                int passwordIndex = cursor.getColumnIndex("Password");
                int phoneNumberIndex = cursor.getColumnIndex("PhoneNumber");

                if (clientNameIndex != -1 && passwordIndex != -1 && phoneNumberIndex != -1) {
                    user = new Client();
                    user.clientName = cursor.getString(clientNameIndex);
                    user.password = cursor.getString(passwordIndex);
                    user.phone = cursor.getString(phoneNumberIndex);
                } else {
                    throw new SQLException("Столбцы не найдены в результирующем наборе.");
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return user;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM Products";
            cursor = database.rawQuery(query, null);
            if (cursor != null) {
                int productIdIndex = cursor.getColumnIndex("ProductID");
                int productNameIndex = cursor.getColumnIndex("ProductName");
                int descriptionIndex = cursor.getColumnIndex("Description");
                int priceIndex = cursor.getColumnIndex("Price");

                while (cursor.moveToNext()) {
                    Product product = new Product();
                    product.productID = cursor.getInt(productIdIndex);
                    product.productName = cursor.getString(productNameIndex);
                    product.description = cursor.getString(descriptionIndex);
                    product.price = cursor.getDouble(priceIndex);
                    productList.add(product);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return productList;
    }

    public static int getClientID(String username) {
        int clientID = -1;
        Cursor cursor = null;
        try {
            String query = "SELECT ClientID FROM Clients WHERE ClientName = ?";
            cursor = database.rawQuery(query, new String[]{username});
            if (cursor != null && cursor.moveToFirst()) {
                int clientIdIndex = cursor.getColumnIndex("ClientID");
                if (clientIdIndex != -1) {
                    clientID = cursor.getInt(clientIdIndex);
                } else {
                    throw new SQLException("Столбец 'ClientID' не найден в результирующем наборе.");
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return clientID;
    }

    public boolean isProductInCart(int clientID, int productID) {
        boolean isInCart = false;
        Cursor cursor = null;
        try {
            String query = "SELECT COUNT(*) FROM ClientCart WHERE ClientID = ? AND ProductID = ?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(clientID), String.valueOf(productID)});
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                isInCart = count > 0;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isInCart;
    }

    public void addProductToCart(int clientID, int productID) {
        ContentValues values = new ContentValues();
        values.put("ClientID", clientID);
        values.put("ProductID", productID);
        values.put("Quantity", 1);
        database.insert("ClientCart", null, values);
    }

    public List<Product> getUserProducts(String username) {
        List<Product> userProducts = new ArrayList<>();
        Cursor cursor = null;
        try {
            int clientID = getClientID(username);
            String query = "SELECT * FROM Products WHERE ProductID IN (SELECT ProductID FROM ClientCart WHERE ClientID = ?)";
            cursor = database.rawQuery(query, new String[]{String.valueOf(clientID)});
            if (cursor != null) {
                int productIDIndex = cursor.getColumnIndex("ProductID");
                int productNameIndex = cursor.getColumnIndex("ProductName");
                int descriptionIndex = cursor.getColumnIndex("Description");
                int priceIndex = cursor.getColumnIndex("Price");

                while (cursor.moveToNext()) {
                    Product product = new Product();
                    product.productID = cursor.getInt(productIDIndex);
                    product.productName = cursor.getString(productNameIndex);
                    product.description = cursor.getString(descriptionIndex);
                    product.price = cursor.getDouble(priceIndex);
                    userProducts.add(product);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return userProducts;
    }

    public boolean removeProductFromCart(int productID) {
        int clientID = getClientID(CurrentUser.name);
        try {
            database.beginTransaction();
            int rowsAffected = database.delete("ClientCart", "ClientID = ? AND ProductID = ?", new String[]{String.valueOf(clientID), String.valueOf(productID)});
            if (rowsAffected > 0) {
                database.setTransactionSuccessful();
                return true;
            }
        } finally {
            database.endTransaction();
        }
        return false;
    }

    public boolean increaseProductQuantity(int productID) {
        int clientID = getClientID(CurrentUser.name);
        try {
            database.beginTransaction();
            int currentQuantity = getProductQuantityInCart(clientID, productID);
            int newQuantity = currentQuantity + 1;
            ContentValues values = new ContentValues();
            values.put("Quantity", newQuantity);
            int rowsAffected = database.update("ClientCart", values, "ClientID = ? AND ProductID = ?", new String[]{String.valueOf(clientID), String.valueOf(productID)});
            if (rowsAffected > 0) {
                database.setTransactionSuccessful();
                return true;
            }
        } finally {
            database.endTransaction();
        }
        return false;
    }

    public boolean decreaseProductQuantityInCart(int productID) {
        int clientID = getClientID(CurrentUser.name);
        try {
            database.beginTransaction();
            int currentQuantity = getProductQuantityInCart(clientID, productID);
            if (currentQuantity > 1) {
                int newQuantity = currentQuantity - 1;
                ContentValues values = new ContentValues();
                values.put("Quantity", newQuantity);
                int rowsAffected = database.update("ClientCart", values, "ClientID = ? AND ProductID = ?", new String[]{String.valueOf(clientID), String.valueOf(productID)});
                if (rowsAffected > 0) {
                    database.setTransactionSuccessful();
                    return true;
                }
            }
        } finally {
            database.endTransaction();
        }
        return false;
    }

    public static int getProductQuantityInCart(int clientID, int productID) {
        Cursor cursor = null;
        int quantity = 0;
        try {
            String query = "SELECT Quantity FROM ClientCart WHERE ClientID = ? AND ProductID = ?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(clientID), String.valueOf(productID)});
            if (cursor != null && cursor.moveToFirst()) {
                int quantityIndex = cursor.getColumnIndex("Quantity");
                if (quantityIndex != -1) {
                    quantity = cursor.getInt(quantityIndex);
                } else {
                    throw new SQLException("Столбец 'Quantity' не найден в результирующем наборе.");
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return quantity;
    }

    public double getTotalPriceForUser(String username) {
        double totalPrice = 0.0;
        int clientID = getClientID(username);
        Cursor cursor = null;
        int quantity = 0;
        double price = 0.0;
        try {
            String query = "SELECT Products.Price, ClientCart.Quantity " +
                    "FROM Products " +
                    "INNER JOIN ClientCart ON Products.ProductID = ClientCart.ProductID " +
                    "WHERE ClientCart.ClientID = ?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(clientID)});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int quantityIndex = cursor.getColumnIndex("Quantity");
                    if (quantityIndex != -1) {
                        quantity = cursor.getInt(quantityIndex);
                    }
                    int priceIndex = cursor.getColumnIndex("Price");
                    if (priceIndex != -1) {
                        price = cursor.getDouble(priceIndex);
                    }
                    totalPrice += price * quantity;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return totalPrice;
    }


    public void clearCartForUser(String username) {
        int clientID = getClientID(username);
        database.delete("ClientCart", "ClientID = ?", new String[]{String.valueOf(clientID)});
    }

}
