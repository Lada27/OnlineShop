package com.example.onlineshop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "onlineShop.db";
    private static final int DATABASE_VERSION = 5;

    // SQL запросы для создания таблиц
    private static final String CREATE_TABLE_CLIENTS =
            "CREATE TABLE Clients (" +
                    "ClientID INTEGER PRIMARY KEY, " +
                    "ClientName NCHAR(100), " +
                    "Password NCHAR(100), " +
                    "PhoneNumber NCHAR(15))";

    private static final String CREATE_TABLE_PRODUCTS =
            "CREATE TABLE Products (" +
                    "ProductID INTEGER PRIMARY KEY, " +
                    "ProductName NCHAR(100), " +
                    "Description NCHAR(255), " +
                    "Price DECIMAL(10, 2), " +
                    "ImageUrl NCHAR(255))";

    private static final String CREATE_TABLE_CLIENT_CART =
            "CREATE TABLE ClientCart (" +
                    "CartID INTEGER PRIMARY KEY, " +
                    "ClientID INTEGER, " +
                    "ProductID INTEGER, " +
                    "Quantity INTEGER, " +
                    "FOREIGN KEY(ClientID) REFERENCES Clients(ClientID), " +
                    "FOREIGN KEY(ProductID) REFERENCES Products(ProductID))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблиц
        db.execSQL(CREATE_TABLE_CLIENTS);
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_CLIENT_CART);

        fillProductsTable(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Здесь можно определить логику обновления базы данных, если структура изменится
        db.execSQL("DROP TABLE IF EXISTS ClientCart");
        db.execSQL("DROP TABLE IF EXISTS Products");
        db.execSQL("DROP TABLE IF EXISTS Clients");
        onCreate(db);
    }

    public void fillProductsTable(SQLiteDatabase db) {
        // Проверяем, есть ли в таблице продуктов записи
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Products", null);
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            cursor.close();
            // Если таблица пустая, заполняем ее данными
            if (count == 0) {
                // Создаем массив с названиями продуктов, описанием и ценой
                String[] productNames = {"Гаечный ключ", "Пила", "Отвертка", "Молоток", "Ножницы", "Мерный стакан", "Отбойный молоток", "Лобзик", "Клещи", "Электродрель"};
                String[] descriptions = {"Гаечный ключ для работы с гайками и болтами", "Пила для резки древесины", "Отвертка с разными насадками", "Молоток для забивания гвоздей", "Ножницы для резки", "Мерный стакан для измерения объема", "Отбойный молоток для разрушения бетона", "Лобзик для резки материалов", "Клещи для захвата и удержания предметов", "Электродрель для сверления отверстий"};
                double[] prices = {10.99, 15.49, 8.99, 12.79, 5.99, 3.99, 24.99, 18.99, 9.49, 39.99};

                // Вставляем данные в таблицу продуктов
                ContentValues values = new ContentValues();
                for (int i = 0; i < productNames.length; i++) {
                    values.clear();
                    values.put("ProductName", productNames[i]);
                    values.put("Description", descriptions[i]);
                    values.put("Price", prices[i]);
                    values.put("ProductId", i+1);
                    db.insert("Products", null, values);
                }
            }
        }
    }
}
