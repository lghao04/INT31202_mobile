package com.example.project1.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project1.R;
import com.example.project1.activites.MenuItemDetailActivity;
import com.example.project1.database.MenuItems;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MenuItemsAdapter extends RecyclerView.Adapter<MenuItemsAdapter.ViewHolder> {
    private Context context;
    private List<MenuItems> menuItems;

    public MenuItemsAdapter(Context context, List<MenuItems> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItems item = menuItems.get(position);

        holder.txtDishName.setText(item.name);
        holder.txtDishDescription.setText(item.description);
        holder.txtPrice.setText(String.format("%,.0f đ", item.price));

        Glide.with(context).load(item.imageUrl).into(holder.imgDish);

        // 👉 Click vào item để mở màn chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MenuItemDetailActivity.class);
            intent.putExtra("menuItemId", item.getId()); // đảm bảo bạn đã setId() khi load
            context.startActivity(intent);
        });

        // 👉 Xử lý nút XÓA
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa món?")
                    .setMessage("Bạn có chắc muốn xóa món \"" + item.name + "\" không?")
                    .setPositiveButton("Xóa", (dialog, which) -> deleteMenuItem(item.getId()))
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDish;
        TextView txtDishName, txtDishDescription, txtPrice;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDish = itemView.findViewById(R.id.imgDish);
            txtDishName = itemView.findViewById(R.id.txtDishName);
            txtDishDescription = itemView.findViewById(R.id.txtDishDescription);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            btnDelete = itemView.findViewById(R.id.btnDelete); // ⚠️ Nhớ thêm nút này vào item_menu.xml
        }
    }

    private void deleteMenuItem(String itemId) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("menuItems")
                .child(itemId);

        ref.removeValue()
                .addOnSuccessListener(unused -> Toast.makeText(context, "Đã xóa món!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
