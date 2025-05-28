package com.example.project1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;
import com.example.project1.activites.OrderDetailActivity;
import com.example.project1.adapter.OrderAdapter;
import com.example.project1.database.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class NewOrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private ArrayList<Order> orderList;
    private DatabaseReference ordersRef;
    private String currentRestaurantId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_orders, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewNewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        currentRestaurantId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Truyền context vào adapter
        adapter = new OrderAdapter(getContext(), orderList, currentRestaurantId, order -> {

            Intent intent = new Intent(getContext(), OrderDetailActivity.class);
            intent.putExtra("order", order); // Order phải implements Serializable hoặc Parcelable
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        loadPendingOrders();

        return view;
    }

    private void loadPendingOrders() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    Order order = orderSnap.getValue(Order.class);
                    if (order != null &&
                            "pending".equals(order.getStatus()) &&
                            currentRestaurantId.equals(order.getRestaurantId())) {
                        orderList.add(order);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Không thể tải đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
