package com.example.project1.activites;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project1.R;
import com.google.firebase.database.Transaction;

public class introActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(introActivity.this, loginActivity.class);
            startActivity(intent);
            finish();

        }, 3000);

    }
}