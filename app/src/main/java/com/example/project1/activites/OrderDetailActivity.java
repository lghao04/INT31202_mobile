package com.example.project1.activites;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;
import com.example.project1.adapter.OrderDetailItemAdapter;
import com.example.project1.database.Order;
import com.example.project1.database.OrderItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Ánh xạ View
        TextView tvOrderId = findViewById(R.id.tvOrderId);
        TextView tvOrderTime = findViewById(R.id.tvOrderTime);
        TextView tvNote = findViewById(R.id.tvNote);
        TextView tvSubtotal = findViewById(R.id.tvSubtotal);
        RecyclerView recyclerViewItems = findViewById(R.id.recyclerViewItems);
        Button btnConfirm = findViewById(R.id.btnConfirm);

        // Lấy Order từ Intent
        Order order = (Order) getIntent().getSerializableExtra("order");

        if (order != null) {
            // Hiển thị thông tin cơ bản
            tvOrderId.setText("Mã đơn: " + order.getId());

            if (order.getOrderTime() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                tvOrderTime.setText("Thời gian: " + sdf.format(order.getOrderTime()));
            } else {
                tvOrderTime.setText("Thời gian: Không có");
            }

            tvNote.setText("Ghi chú: " + (order.getNote() != null && !order.getNote().isEmpty() ? order.getNote() : "Không có"));
            tvSubtotal.setText("Tạm tính: " + order.getSubtotal() + " đ");

            //  hiển thị danh sách món ăn
            recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));


            List<OrderItem> items = order.getItems();
            if (items == null) {
                items = new ArrayList<>(); // Tránh null
            }
            OrderDetailItemAdapter adapter = new OrderDetailItemAdapter(items);
            recyclerViewItems.setAdapter(adapter);


        }
        btnConfirm.setOnClickListener(v -> {
            if (order != null) {
                String orderId = order.getId();
                DatabaseReference orderRef = FirebaseDatabase.getInstance()
                        .getReference("orders")
                        .child(orderId);

                orderRef.child("status").setValue("verified")
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(OrderDetailActivity.this, "Đơn hàng đã được xác thực", Toast.LENGTH_SHORT).show();
                            finish(); // Quay lại để cập nhật giao diện Fragment
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(OrderDetailActivity.this, "Lỗi xác thực đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

}
