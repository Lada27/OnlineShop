package com.example.onlineshop.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlineshop.R;
import com.example.onlineshop.database.DatabaseHelper;
import com.example.onlineshop.database.DatabaseManager;
import com.example.onlineshop.model.CurrentUser;
import com.example.onlineshop.model.Product;

import java.util.List;

public class CatalogActivity extends AppCompatActivity {

    private Button buttonProfile;
    private Button buttonCart;
    private DatabaseManager dbManager;
    private LinearLayout linearLayoutProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Initialize DatabaseManager singleton instance
        dbManager = DatabaseManager.getInstance(this);

        buttonProfile = findViewById(R.id.buttonProfile);
        buttonCart = findViewById(R.id.buttonCart);
        linearLayoutProducts = findViewById(R.id.linearLayoutProducts);

        // Get the list of products from the database
        List<Product> productList = dbManager.getAllProducts();

        // Create a card for each product and add it to linearLayoutProducts
        for (final Product product : productList) {
            View productView = LayoutInflater.from(this).inflate(R.layout.product_item_layout, null);
            TextView textViewProductName = productView.findViewById(R.id.textViewProductName);
            TextView textViewDescription = productView.findViewById(R.id.textViewDescription);
            TextView textViewPrice = productView.findViewById(R.id.textViewPrice);
            Button buttonAddToCart = productView.findViewById(R.id.buttonAddToCart);

            textViewProductName.setText(product.productName);
            textViewDescription.setText(product.description);
            textViewPrice.setText(String.valueOf(product.price));

            // Click listener for the "Add to Cart" button
            buttonAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clientID = dbManager.getClientID(CurrentUser.name);
                    int productID = dbManager.getProoductID(product.productName);
                    if (!dbManager.isProductInCart(clientID, productID)) {
                        dbManager.addProductToCart(clientID, productID);
                        Toast.makeText(getApplicationContext(), "Продукт добавлен в корзину", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Продукт уже есть в корзине" , Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Add the product card to linearLayoutProducts
            linearLayoutProducts.addView(productView);
        }

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this, Profile.class);
                startActivity(intent);
            }
        });

        buttonCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }
}
