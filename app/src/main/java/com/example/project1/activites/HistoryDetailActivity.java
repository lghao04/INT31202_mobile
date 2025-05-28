package com.example.project1.activites;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;
import com.example.project1.adapter.OrderDetailItemAdapter;
import com.example.project1.database.Order;
import com.example.project1.database.OrderItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        // Ánh xạ View
        TextView tvOrderId = findViewById(R.id.tvOrderId);
        TextView tvOrderTime = findViewById(R.id.tvOrderTime);
        TextView tvNote = findViewById(R.id.tvNote);
        TextView tvSubtotal = findViewById(R.id.tvSubtotal);
        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvCancelReason = findViewById(R.id.tvCancelReason);
        RecyclerView recyclerViewItems = findViewById(R.id.recyclerViewItems);

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

            // Đặt lại tiêu đề tùy theo trạng thái đơn hàng
            String status = order.getStatus();
            if ("completed".equals(status)) {
                tvSubtotal.setText("Tổng: " + order.getSubtotal() + " đ");
                tvStatus.setText("Trạng thái: Đã hoàn thành");
                tvCancelReason.setVisibility(TextView.GONE);
            } else if ("canceled".equals(status)) {
                tvSubtotal.setText("Tổng: " + order.getSubtotal() + " đ");
                tvStatus.setText("Trạng thái: Đã hủy");

                String reason = order.getCancelReason();
                if (reason != null && !reason.isEmpty()) {
                    tvCancelReason.setText("Lý do hủy: " + reason);
                    tvCancelReason.setVisibility(TextView.VISIBLE);
                } else {
                    tvCancelReason.setVisibility(TextView.GONE);
                }
            } else {
                tvSubtotal.setText("Tổng: " + order.getSubtotal() + " đ");
                tvStatus.setText("Trạng thái: Không xác định");
                tvCancelReason.setVisibility(TextView.GONE);
            }

            // Hiển thị danh sách món ăn
            recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
            List<OrderItem> items = order.getItems();
            if (items == null) {
                items = new ArrayList<>();
            }
            OrderDetailItemAdapter adapter = new OrderDetailItemAdapter(items);
            recyclerViewItems.setAdapter(adapter);
        } else {
            // Nếu không có Order trong Intent, thông báo lỗi
            Toast.makeText(this, "Không có dữ liệu đơn hàng!", Toast.LENGTH_SHORT).show();
        }
    }
}

