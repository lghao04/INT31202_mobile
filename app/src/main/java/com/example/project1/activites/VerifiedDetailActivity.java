package com.example.project1.activites;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

public class VerifiedDetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verified_detail);

        // Ánh xạ View
        TextView tvOrderId = findViewById(R.id.tvOrderId);
        TextView tvOrderTime = findViewById(R.id.tvOrderTime);
        TextView tvNote = findViewById(R.id.tvNote);
        TextView tvSubtotal = findViewById(R.id.tvSubtotal);
        RecyclerView recyclerViewItems = findViewById(R.id.recyclerViewItems);
        Button btnCancelOrder = findViewById(R.id.btnCancelOrder);
        Button btnContactShipper = findViewById(R.id.btnContactShipper);

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
        btnContactShipper.setOnClickListener(v -> {
            if (order != null) {
                String orderId = order.getId();
                DatabaseReference orderRef = FirebaseDatabase.getInstance()
                        .getReference("orders")
                        .child(orderId);

                orderRef.child("status").setValue("contacted")
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(VerifiedDetailActivity.this, "Đã liên hệ shipper", Toast.LENGTH_SHORT).show();
                            finish(); // Quay lại để cập nhật giao diện Fragment
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(VerifiedDetailActivity.this, "Lỗi liên hệ shipper: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        btnCancelOrder.setOnClickListener(v -> {
            showCancelReasonDialog(order);
        });

    }


    private void showCancelReasonDialog(Order order) {
        final String[] reasons = {"Đến giờ đóng cửa", "Hết món", "Khách không phản hồi", "Lý do khác"};
        final int[] selectedReason = {-1};

        // Tạo layout custom cho dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 0);

        RadioGroup radioGroup = new RadioGroup(this);
        EditText editTextOtherReason = new EditText(this);
        editTextOtherReason.setHint("Nhập lý do...");
        editTextOtherReason.setVisibility(EditText.GONE);

        // Thêm các lựa chọn radio
        for (int i = 0; i < reasons.length; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(reasons[i]);
            int finalI = i;
            rb.setOnClickListener(v -> {
                selectedReason[0] = finalI;
                editTextOtherReason.setVisibility(
                        reasons[finalI].equals("Lý do khác") ? EditText.VISIBLE : EditText.GONE
                );
            });
            radioGroup.addView(rb);
        }

        layout.addView(radioGroup);
        layout.addView(editTextOtherReason);

        new AlertDialog.Builder(this)
                .setTitle("Chọn lý do hủy đơn")
                .setView(layout)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    if (selectedReason[0] == -1) {
                        Toast.makeText(this, "Vui lòng chọn lý do", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String reason;
                    if (reasons[selectedReason[0]].equals("Lý do khác")) {
                        reason = editTextOtherReason.getText().toString().trim();
                        if (reason.isEmpty()) {
                            Toast.makeText(this, "Vui lòng nhập lý do cụ thể", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        reason = reasons[selectedReason[0]];
                    }

                    cancelOrder(order, reason);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }


    private void cancelOrder(Order order, String reason) {
        if (order != null) {
            String orderId = order.getId();
            DatabaseReference orderRef = FirebaseDatabase.getInstance()
                    .getReference("orders")
                    .child(orderId);

            orderRef.child("status").setValue("canceled");
            orderRef.child("cancelReason").setValue(reason)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Đơn hàng đã bị hủy", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi hủy đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }



}
