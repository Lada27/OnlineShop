package com.example.onlineshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.onlineshop.R;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlineshop.database.DatabaseManager;
import com.example.onlineshop.model.Client;
import com.example.onlineshop.model.CurrentUser;

public class Profile extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextPhoneNumber;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        Button buttonLogout = findViewById(R.id.buttonLogout);
        Button buttonCatalog = findViewById(R.id.buttonCatalog);
        Button buttonCart = findViewById(R.id.buttonCart);

        // Initialize DatabaseManager as a singleton
        databaseManager = DatabaseManager.getInstance(this);

        // Load user data
        loadUserData(CurrentUser.name);

        buttonLogout.setOnClickListener(view -> showLogoutConfirmationDialog());

        buttonCatalog.setOnClickListener(view -> {
            Intent intent = new Intent(Profile.this, CatalogActivity.class);
            startActivity(intent);
        });

        buttonCart.setOnClickListener(view -> {
            Intent intent = new Intent(Profile.this, CartActivity.class);
            startActivity(intent);
        });

    }

    private void loadUserData(String username) {
        // Get user data from the database and populate the fields
        Client user = databaseManager.getUserData(username);
        if (user != null) {
            editTextUsername.setText(user.clientName);
            editTextPassword.setText(user.password);
            editTextPhoneNumber.setText(user.phone);
        }
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите выйти?")
                .setPositiveButton("Да", (dialog, which) -> {
                    Intent intent = new Intent(Profile.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
