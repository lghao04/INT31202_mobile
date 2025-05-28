package com.example.project1.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.project1.R;
import com.example.project1.activites.AddMenuItemActivity;
import com.example.project1.adapter.MenuItemsAdapter;
import com.example.project1.database.MenuItems;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class MenuFragment extends Fragment {

    private RecyclerView recyclerView;
    private MenuItemsAdapter adapter;
    private ArrayList<MenuItems> menuItems;
    private ImageButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMenu);
        fab = view.findViewById(R.id.fabAddDish);

        menuItems = new ArrayList<>();
        adapter = new MenuItemsAdapter(getContext(), menuItems);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddMenuItemActivity.class));
        });

        loadMenuItems();

        return view;
    }

    private void loadMenuItems() {
        String restaurantId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("menuItems");

        ref.orderByChild("restaurantId").equalTo(restaurantId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        menuItems.clear();
                        for (DataSnapshot itemSnap : snapshot.getChildren()) {
                            MenuItems item = itemSnap.getValue(MenuItems.class);
                            if (item != null) {
                                item.setId(itemSnap.getKey()); // 👈 Gán ID từ push key
                                menuItems.add(item);
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
