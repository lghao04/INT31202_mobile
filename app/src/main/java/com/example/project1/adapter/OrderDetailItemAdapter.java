package com.example.project1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project1.R;
import com.example.project1.database.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailItemAdapter extends RecyclerView.Adapter<OrderDetailItemAdapter.ViewHolder> {

    private List<OrderItem> items;

    public OrderDetailItemAdapter(List<OrderItem> items) {
        this.items = (items != null) ? items : new ArrayList<>();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail_item, parent, false); // dùng đúng tên file bạn có
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = items.get(position);

        holder.tvItemName.setText(item.getItemName());
        holder.tvItemQuantity.setText("Số lượng: " + item.getQuantity());
        holder.tvItemTotal.setText("Thành tiền: " + item.getTotalPrice() + "đ");

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background) // ảnh placeholder nếu thiếu
                .into(holder.ivItemImage);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemQuantity, tvItemTotal;
        ImageView ivItemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
        }
    }
}
