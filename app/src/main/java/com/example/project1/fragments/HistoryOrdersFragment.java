package com.example.project1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;
import com.example.project1.activites.HistoryDetailActivity;
import com.example.project1.adapter.OrderAdapter;
import com.example.project1.database.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class HistoryOrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private ArrayList<Order> orderList;
    private DatabaseReference ordersRef;
    private String currentRestaurantId;
    private TextView tvFilterStatus; // TextView để hiển thị trạng thái lọc

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewHistoryOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ImageButton btnFilter = view.findViewById(R.id.btnFilter);
        tvFilterStatus = view.findViewById(R.id.tvFilterStatus); // Ánh xạ TextView trạng thái lọc

        orderList = new ArrayList<>();
        currentRestaurantId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adapter = new OrderAdapter(getContext(), orderList, currentRestaurantId, order -> {
            Intent intent = new Intent(getContext(), HistoryDetailActivity.class);
            intent.putExtra("order", order);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Lắng nghe sự kiện lọc
        btnFilter.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), btnFilter);
            popupMenu.getMenu().add("Tất cả");
            popupMenu.getMenu().add("Đơn đã hủy");
            popupMenu.getMenu().add("Đơn đã hoàn tất");

            popupMenu.setOnMenuItemClickListener(item -> {
                String selected = item.getTitle().toString();
                if (selected.equals("Tất cả")) {
                    loadOrdersByStatus(null);
                    tvFilterStatus.setText("Tất cả"); // Cập nhật trạng thái lọc
                } else if (selected.equals("Đơn đã hủy")) {
                    loadOrdersByStatus("canceled");
                    tvFilterStatus.setText("Đơn đã hủy"); // Cập nhật trạng thái lọc
                } else if (selected.equals("Đơn đã hoàn tất")) {
                    loadOrdersByStatus("completed");
                    tvFilterStatus.setText("Đơn đã hoàn tất"); // Cập nhật trạng thái lọc
                }
                return true;
            });

            popupMenu.show();
        });

        loadOrdersByStatus(null); // Load tất cả khi bắt đầu
        return view;
    }

    private void loadOrdersByStatus(@Nullable String status) {
        ordersRef.orderByChild("restaurantId").equalTo(currentRestaurantId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Order> filteredOrders = new ArrayList<>();
                        for (DataSnapshot orderSnap : snapshot.getChildren()) {
                            Order order = orderSnap.getValue(Order.class);
                            if (order != null) {
                                if ("pending".equals(order.getStatus()) || "verified".equals(order.getStatus())) continue;

                                if (status == null || status.equals(order.getStatus())) {
                                    filteredOrders.add(order);
                                }
                            }
                        }
                        adapter.setOrders(filteredOrders);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi khi lấy dữ liệu
                        Toast.makeText(getContext(), "Không thể tải dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
