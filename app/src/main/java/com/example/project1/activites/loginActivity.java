package com.example.project1.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class loginActivity extends AppCompatActivity {
    Button btnSignup,btnLogin;
    EditText etUsername, etPassword;

    DatabaseReference databaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        databaseRef = FirebaseDatabase.getInstance().getReference("restaurants");

        // Xử lý nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String inputUsername = etUsername.getText().toString().trim();
            String inputPassword = etPassword.getText().toString().trim();

            if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // B1: Tìm email từ username trong node "restaurants"
            databaseRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String matchedEmail = null;

                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        String dbUsername = snapshot.child("username").getValue(String.class);
                        String dbEmail = snapshot.child("email").getValue(String.class);

                        if (dbUsername != null && dbUsername.equals(inputUsername)) {
                            matchedEmail = dbEmail;
                            break;
                        }
                    }

                    if (matchedEmail != null) {
                        // B2: Đăng nhập bằng email đã tìm thấy
                        FirebaseAuth.getInstance()
                                .signInWithEmailAndPassword(matchedEmail, inputPassword)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(this, HomeActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Sai mật khẩu hoặc tài khoản", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Không tìm thấy username", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Lỗi kết nối tới database", Toast.LENGTH_SHORT).show();
                }
            });
        });





        btnSignup = findViewById(R.id.btnSignup);

        // Xử lý khi ấn nút "Đăng ký"
        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(loginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(loginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });

    }
}