package com.example.project1.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.project1.fragments.HistoryOrdersFragment;
import com.example.project1.fragments.NewOrdersFragment;
import com.example.project1.fragments.VerifiedOrdersFragment;

public class OrderPagerAdapter extends FragmentStateAdapter {

    public OrderPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NewOrdersFragment();
            case 1:
                return new VerifiedOrdersFragment();
            case 2:
                return new HistoryOrdersFragment();
            default:
                return new NewOrdersFragment(); // fallback
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
