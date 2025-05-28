package com.example.project1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;
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

    }

    @Override
    public int getItemCount() {
        return promotionItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPromoCode, tvPromoDesc, tvPromoDate,tvPromoRule;
        ImageButton btnEditPromo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPromoCode = itemView.findViewById(R.id.tvPromoCode);
            tvPromoDesc = itemView.findViewById(R.id.tvPromoDesc);
            tvPromoRule = itemView.findViewById(R.id.tvPromoRule);
            tvPromoDate = itemView.findViewById(R.id.tvPromoDate);
            btnEditPromo = itemView.findViewById(R.id.btnEditPromo);
        }
    }
}
