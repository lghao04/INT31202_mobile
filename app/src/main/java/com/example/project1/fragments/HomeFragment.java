package com.example.project1.fragments;



import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project1.R;
import com.example.project1.adapter.HomePagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {


    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private HomePagerAdapter pagerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        tabLayout = root.findViewById(R.id.tabLayoutHome);
        viewPager = root.findViewById(R.id.viewPagerHome);

        pagerAdapter = new HomePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("Thực đơn");
                    else tab.setText("Mã giảm giá");
                }).attach();



        return root;
    }
}
