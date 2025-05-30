package com.example.project1.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;
import com.example.project1.activites.AddPromotionActivity;
import com.example.project1.database.promotions;

import java.util.List;

public class PromotionItemsAdapter extends RecyclerView.Adapter<PromotionItemsAdapter.ViewHolder> {

    private Context context;
    private List<promotions> promotionItems;

    public PromotionItemsAdapter(Context context, List<promotions> promotionItems) {
        this.context = context;
        this.promotionItems = promotionItems;
    }

    @NonNull
    @Override
    public PromotionItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.promotion_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        promotions item = promotionItems.get(position);

        // Set mã giảm giá
        holder.tvPromoCode.setText("Mã: " + item.getPromoCode());

        String desc;
        if ("percentage".equals(item.getDiscountType())) {
            desc = "Giảm " + item.getDiscountAmount() + "% "
                    + "Giảm tối đa " + item.getMaxDiscountAmount() +"đ";
        } else if ("fixed_amount".equals(item.getDiscountType())) {
            desc = "Giảm " + item.getDiscountAmount() + "đ";
        } else {
            desc = "Không xác định";
        }
        holder.tvPromoDesc.setText(desc);

        String rule = item.getMinimumOrder();
        holder.tvPromoRule.setText("Đơn hàng tối thiểu :" + rule +"đ");

        holder.tvPromoDate.setText(item.getStartDate() + " - " + item.getEndDate());

        if (item.isExpired()) {
            holder.itemView.setBackgroundColor(Color.LTGRAY); // Nền xám
            holder.btnEditPromo.setVisibility(View.GONE); // Ẩn nút chỉnh sửa
        }
        else {

            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.btnEditPromo.setVisibility(View.VISIBLE); // Ẩn nút chỉnh sửa
        }
        holder.btnEditPromo.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddPromotionActivity.class);
            intent.putExtra("promotionId", item.getId());
            intent.putExtra("promotionObject", item); // đảm bảo có toMap() trả về HashMap<String, Object>
            context.startActivity(intent);


        });



    }

    @Override
    public int getItemCount() {
        return promotionItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPromoCode, tvPromoDesc, tvPromoDate,tvPromoRule;
        ImageButton btnEditPromo;
        ImageButton filter;
        TextView filterStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPromoCode = itemView.findViewById(R.id.tvPromoCode);
            tvPromoDesc = itemView.findViewById(R.id.tvPromoDesc);
            tvPromoRule = itemView.findViewById(R.id.tvPromoRule);
            tvPromoDate = itemView.findViewById(R.id.tvPromoDate);
            btnEditPromo = itemView.findViewById(R.id.btnEditPromo);
            filter = itemView.findViewById(R.id.btnFilter);
            filterStatus = itemView.findViewById(R.id.tvFilterStatus);
        }
    }
}
