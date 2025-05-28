package com.example.project1.activites;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project1.R;
import com.example.project1.database.Restaurants;
import com.example.project1.database.Restaurants;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText , restaurantNameEditText;
    Button registerButton;
    DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this); // Đảm bảo Firebase được khởi tạo

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        restaurantNameEditText = findViewById(R.id.restaurantNameEditText);
        registerButton = findViewById(R.id.registerButton);

        // Firebase Database path: "Users"
        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");

        registerButton.setOnClickListener(v -> registerRestaurant());
    }

    private void registerRestaurant() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String restaurantName = restaurantNameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || restaurantName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = auth.getCurrentUser().getUid();

                        // Tạo restaurant object
                        Map<String, Object> restaurant = new HashMap<>();
                        restaurant.put("name", restaurantName);
                        restaurant.put("username",username);
                        restaurant.put("email", email);
                        restaurant.put("isActive", true);
                        restaurant.put("rating", 0.0);
                        restaurant.put("totalRatings", 0);

                        // Lưu vào "restaurants/{uid}"
                        FirebaseDatabase.getInstance().getReference("restaurants")
                                .child(uid)
                                .setValue(restaurant)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, loginActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "Lỗi đăng ký: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


}



    // Viết hoa chữ cái đầu mỗi từ
    private String normalizeName(String name) {
        String[] parts = name.toLowerCase().split(" ");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1)).append(" ");
            }
        }
        return result.toString().trim();
    }

    // Xóa các ô nhập sau khi thành công
    private void clearFields() {
        usernameEditText.setText("");
        emailEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
    }
}
