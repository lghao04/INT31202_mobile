package com.example.project1.activites;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project1.R;
import com.example.project1.database.Restaurants;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;


public class WithdrawActivity extends AppCompatActivity {

    private TextView txtCurrentWallet;
    private Button btnWithdraw;
    private double walletAmount;
    private String restaurantEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);


        txtCurrentWallet = findViewById(R.id.txtCurrentWallet);
        btnWithdraw = findViewById(R.id.btnWithdraw);

        String restaurantId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurantId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Restaurants restaurant = snapshot.getValue(Restaurants.class);
                if (restaurant != null) {
                    walletAmount = restaurant.getWallet();
                    restaurantEmail = restaurant.getEmail();  // Lấy email từ database thay vì từ intent
                    txtCurrentWallet.setText(String.format("Số dư hiện tại: %,.0fđ", walletAmount));
                    btnWithdraw.setEnabled(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WithdrawActivity.this, "Không thể lấy dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });

        btnWithdraw.setOnClickListener(v -> {



            if (restaurantEmail == null || restaurantEmail.isEmpty()) {
                Toast.makeText(this, "Đang tải dữ liệu email, vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                return;
            }
            String randomCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            btnWithdraw.setEnabled(false);
            Map<String, Object> data = new HashMap<>();
            data.put("content", "Mã QR cho ngày 6.6");
            data.put("code", "XYZ12345"); // nếu muốn

            FirebaseFunctions.getInstance()
                    .getHttpsCallable("sendWithdrawQR")
                    .call(data)
                    .addOnSuccessListener(task -> {
                        Toast.makeText(WithdrawActivity.this, "Đã gửi mã QR vào email!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("WithdrawActivity", "Lỗi gửi email:", e);

                        // Nếu là FirebaseFunctionsException thì lấy thêm thông tin mã lỗi và message
                        if (e instanceof FirebaseFunctionsException) {
                            FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                            FirebaseFunctionsException.Code code = ffe.getCode();
                            String details = ffe.getDetails() != null ? ffe.getDetails().toString() : "no details";
                            Log.e("WithdrawActivity", "FirebaseFunctionsException code: " + code + ", details: " + details);
                            Toast.makeText(WithdrawActivity.this, "Lỗi gửi email: " + code.name(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(WithdrawActivity.this, "Không thể gửi email: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        btnWithdraw.setEnabled(true);
                    });




            // Gọi Firebase Cloud Function sendWithdrawQR
//            Map<String, Object> data = new HashMap<>();
//            data.put("email", restaurantEmail);
//            data.put("content", "mã QR cho ngày 6.6");
//            Log.d("WithdrawActivity", "Gửi email tới: " + restaurantEmail);
//
//            FirebaseFunctions.getInstance()
//                    .getHttpsCallable("sendWithdrawQR")
//                    .call(data)
//                    .addOnSuccessListener(httpsCallableResult -> {
//                        Toast.makeText(WithdrawActivity.this, "Đã gửi mã QR vào email!", Toast.LENGTH_SHORT).show();
//                    })
//                    .addOnFailureListener(e -> {
//                        String message = "Gửi email thất bại!";
//                        if (e instanceof com.google.firebase.functions.FirebaseFunctionsException) {
//                            com.google.firebase.functions.FirebaseFunctionsException ffe =
//                                    (com.google.firebase.functions.FirebaseFunctionsException) e;
//
//                            String code = ffe.getCode().name(); // e.g. "INTERNAL", "INVALID_ARGUMENT"
//                            String details = String.valueOf(ffe.getDetails());
//
//                            switch (code) {
//                                case "INVALID_ARGUMENT":
//                                    message = "Email không hợp lệ hoặc thiếu thông tin.";
//                                    break;
//                                case "INTERNAL":
//                                    message = "Lỗi hệ thống khi gửi email.";
//                                    break;
//                                default:
//                                    message = "Lỗi: " + code + ". Chi tiết: " + details;
//                                    break;
//                            }
//
//                            Log.e("WithdrawActivity", "FirebaseFunctionsException: " + code + " | " + details);
//                        } else {
//                            Log.e("WithdrawActivity", "Lỗi không xác định: ", e);
//                        }
//
//                        Toast.makeText(WithdrawActivity.this, message, Toast.LENGTH_LONG).show();
//
//                        btnWithdraw.setEnabled(true);  // Mở lại nút
//                    });

        });

    }






}
