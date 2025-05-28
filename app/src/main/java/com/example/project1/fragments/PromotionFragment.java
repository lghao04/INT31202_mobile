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

import com.example.project1.R;
import com.example.project1.activites.AddPromotionActivity;
import com.example.project1.adapter.PromotionItemsAdapter;
import com.example.project1.database.promotions;
import com.google.firebase.database.*;
import java.util.ArrayList;



public class PromotionFragment extends Fragment {
    private RecyclerView recyclerView;
    private PromotionItemsAdapter adapter;
    private ArrayList<promotions> promotionItems;
    private ImageButton fab;


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

        loadPromotionItems();

        return view;
    }

    private void loadPromotionItems() {
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
                                item.setId(itemSnap.getKey()); // 👈 Gán ID từ push key
                                promotionItems.add(item);
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