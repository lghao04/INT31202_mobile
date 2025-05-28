package com.example.project1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project1.R;
import com.example.project1.adapter.OrderPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrdersFragment extends Fragment {

    private SearchView searchView;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private OrderPagerAdapter pagerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_orders, container, false);

        searchView = root.findViewById(R.id.searchViewOrder);
        tabLayout = root.findViewById(R.id.tabLayoutOrders);
        viewPager = root.findViewById(R.id.viewPagerOrders);

        pagerAdapter = new OrderPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("Mới");
                    else if (position == 1) tab.setText("Đã xác thực");
                    else tab.setText("Lịch sử");
                }).attach();

        // Gợi ý: để xử lý tìm kiếm trong fragment con, nên dùng ViewModel hoặc FragmentResult
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Gợi ý xử lý sau này
                // Sử dụng FragmentResult hoặc ViewModel để gửi chuỗi newText tới các fragment con
                return true;
            }
        });

        return root;
    }
}
