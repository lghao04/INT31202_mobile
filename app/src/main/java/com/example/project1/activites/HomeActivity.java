package com.example.project1.activites;



import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.project1.R;
import com.example.project1.fragments.AccountFragment;
import com.example.project1.fragments.FeedbackFragment;
import com.example.project1.fragments.HomeFragment;
import com.example.project1.fragments.MenuFragment;
import com.example.project1.fragments.OrdersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);// Gọi đúng file layout của bạn

        bottomNavigation = findViewById(R.id.bottom_navigation);


        // Hiển thị fragment mặc định khi mới vào
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // Xử lý chuyển đổi giữa các fragment
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            Fragment selectedFragment;

            if (id == R.id.nav_food) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_feedback) {
                selectedFragment = new FeedbackFragment();
            } else if (id == R.id.nav_orders) {
                selectedFragment = new OrdersFragment();
            }
            else if (id == R.id.nav_account) {
                selectedFragment = new AccountFragment();
            }
            else {
                return false;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            return true;
        });
    }
}
