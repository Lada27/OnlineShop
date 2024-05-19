package com.example.onlineshop.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.onlineshop.R;
import com.example.onlineshop.database.DatabaseManager;
import com.example.onlineshop.model.Client;
import com.example.onlineshop.model.CurrentUser;
import com.example.onlineshop.model.Product;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private Button buttonProfile;
    private Button buttonCatalog;
    private DatabaseManager databaseManager;
    private TextView totalPriceTextView;
    private Button buttonOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        databaseManager = DatabaseManager.getInstance(this);
        totalPriceTextView = findViewById(R.id.totalPrice);
        buttonProfile = findViewById(R.id.buttonProfile);
        buttonCatalog = findViewById(R.id.buttonCatalog);
        buttonOrder = findViewById(R.id.buttonOrder);


        buttonProfile.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, Profile.class);
            startActivity(intent);
        });

        buttonCatalog.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CatalogActivity.class);
            startActivity(intent);
        });

        List<Product> userProducts = databaseManager.getUserProducts(CurrentUser.name);

        LinearLayout linearLayoutProducts = findViewById(R.id.linearLayoutProducts);
        linearLayoutProducts.removeAllViews();

        // Для каждого продукта создаем карточку и добавляем её в LinearLayout
        for (final Product product : userProducts) {
            // Создаем карточку продукта на основе макета item_cart.xml
            View productCard = getLayoutInflater().inflate(R.layout.item_cart, null);

            // Находим в карточке элементы для заполнения данными о продукте
            TextView textViewProductName = productCard.findViewById(R.id.textViewProductName);
            TextView textViewDescription = productCard.findViewById(R.id.textViewDescription);
            TextView textViewPrice = productCard.findViewById(R.id.textViewPrice);
            Button buttonMinus = productCard.findViewById(R.id.buttonMinus);
            Button buttonPlus = productCard.findViewById(R.id.buttonPlus);
            Button buttonDelete = productCard.findViewById(R.id.buttonDelete);
            TextView textViewQuantity = productCard.findViewById(R.id.textViewQuantity);

            textViewProductName.setText(product.productName);
            textViewDescription.setText(product.description);
            textViewPrice.setText(String.valueOf(product.price));
            updateTotalPrice();


            int productID = DatabaseManager.getProoductID(product.productName);

            buttonDelete.setOnClickListener(v -> {
                if (databaseManager.removeProductFromCart(productID)) {
                    recreate();
                }
            });


            textViewQuantity.setText(String.valueOf(DatabaseManager.getProductQuantityInCart(CurrentUser.id, productID)));

            buttonPlus.setOnClickListener(v -> {

                if (databaseManager.increaseProductQuantity(productID)) {
                    recreate();
                }
            });

            buttonMinus.setOnClickListener(v -> {

                if (databaseManager.decreaseProductQuantityInCart(productID)) {
                    recreate();
                }
            });

            buttonOrder.setOnClickListener(v -> placeOrder());

            linearLayoutProducts.addView(productCard);
        }
    }

    private void updateTotalPrice() {
        double totalPrice = databaseManager.getTotalPriceForUser(CurrentUser.name);
        String formattedPrice = String.format("Цена: %.2f", totalPrice);
        totalPriceTextView.setText(formattedPrice);
    }

    private void placeOrder() {
        Client currentUser = databaseManager.getUserData(CurrentUser.name);
        if (currentUser != null) {
            // Удаление всех продуктов из корзины
            databaseManager.clearCartForUser(CurrentUser.name);

            // Отображение сообщения о заказе и реквизитах
            String message = "Заказ оформлен";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            message = "Реквизиты для оплаты отправлены на тел. " + currentUser.phone;
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            recreate();
        } else {
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show();
        }
    }
}
