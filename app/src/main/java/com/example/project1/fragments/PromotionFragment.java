package com.example.project1.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.project1.R;
import com.example.project1.activites.AddPromotionActivity;
import com.example.project1.adapter.PromotionItemsAdapter;
import com.example.project1.database.promotions;
import com.google.firebase.database.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class PromotionFragment extends Fragment {
    private RecyclerView recyclerView;
    private PromotionItemsAdapter adapter;
    private ArrayList<promotions> promotionItems;
    private ImageButton fab , filter;
    private TextView filterStatus;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promotion, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPromotions);
        fab = view.findViewById(R.id.fabAddPromotion);

        promotionItems = new ArrayList<>();
        adapter = new PromotionItemsAdapter(getContext(), promotionItems);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddPromotionActivity.class));

        });
        filter = view.findViewById(R.id.btnFilter);
        filterStatus = view.findViewById(R.id.tvFilterStatus);

        filter.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), filter);
            popupMenu.getMenu().add("Khả dụng");
            popupMenu.getMenu().add("Hết hạn");

            popupMenu.setOnMenuItemClickListener(item -> {
                String selected = item.getTitle().toString();
                if (selected.equals("Khả dụng")) {
                    loadPromotionItems("Khả dụng");
                    filterStatus.setText("Khả dụng"); // Cập nhật trạng thái lọc
                } else if (selected.equals("Hết hạn")) {
                    loadPromotionItems("Hết hạn");// Cập nhật trạng thái lọc
                    filterStatus.setText("Hết hạn");
                }
                return true;
            });

            popupMenu.show();
        });


        loadPromotionItems("Khả dụng");

        return view;
    }

    private void loadPromotionItems(@NonNull String status) {
        String restaurantId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("promotions");

        ref.orderByChild("restaurantId").equalTo(restaurantId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        promotionItems.clear();
                        for (DataSnapshot itemSnap : snapshot.getChildren()) {
                            promotions item = itemSnap.getValue(promotions.class);
                            if (item != null) {
                                item.setId(itemSnap.getKey());// 👈 Gán ID từ push key

                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                    Date endDate = sdf.parse(item.getEndDate());
                                    Date currentDate = new Date();
                                    boolean isExpired = endDate != null && currentDate.after(endDate);
                                    item.setExpired(isExpired);

                                    // ⚠️ Chỉ thêm vào danh sách nếu đúng trạng thái cần lọc
                                    if ((status.equals("Khả dụng") && !isExpired)
                                            || (status.equals("Hết hạn") && isExpired)) {
                                        promotionItems.add(item);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors
                    }
                });


    }
}