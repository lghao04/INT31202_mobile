package com.example.project1.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;
import com.example.project1.activites.OrderDetailActivity;
import com.example.project1.database.Order;
import com.example.project1.database.OrderItem;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private ArrayList<Order> orders;
    private String restaurantId;
    private Context context;
    private OnOrderClickListener listener;


    public OrderAdapter(Context context, ArrayList<Order> orders, String restaurantId) {
        this.context = context;
        this.orders = orders;
        this.restaurantId = restaurantId;
    }
    public OrderAdapter(Context context, ArrayList<Order> orders, String restaurantId, OnOrderClickListener listener) {
        this.context = context;
        this.orders = orders;
        this.restaurantId = restaurantId;
        this.listener = listener;
    }


    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText("Mã đơn: " + order.getId());

        Date date = new Date(order.getOrderTime().getTime());
        holder.tvOrderTime.setText("Thời gian đặt: " + DateFormat.getDateTimeInstance().format(date));

        // Lấy tên món ăn từ restaurantId của cửa hàng
        List<String> itemNames = new ArrayList<>();
        String thumbnailUrl = null;

        for (OrderItem item : order.getItems()) {
            itemNames.add(item.getItemName());
            if (thumbnailUrl == null && item.getImageUrl() != null) {
                thumbnailUrl = item.getImageUrl();
            }
        }

// Sắp xếp tên món theo thứ tự chữ cái tăng dần
        itemNames.sort(String::compareToIgnoreCase);

// Gán chuỗi tên món
        holder.tvOrderItems.setText("Món: " + TextUtils.join(", ", itemNames));
        holder.tvOrderSubtotal.setText("Tạm tính: " + order.getSubtotal() + "đ");

        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(thumbnailUrl).into(holder.ivOrderThumbnail);
        }

        // Xử lý chuyển hướng khi click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderTime, tvOrderItems, tvOrderSubtotal;
        ImageView ivOrderThumbnail;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvOrderSubtotal = itemView.findViewById(R.id.tvOrderSubtotal);
            ivOrderThumbnail = itemView.findViewById(R.id.ivOrderThumbnail);
        }
    }

    public void setOrders(List<Order> newOrders) {
        this.orders.clear();
        this.orders.addAll(newOrders);
        notifyDataSetChanged();
    }



}
