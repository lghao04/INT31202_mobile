package com.example.project1.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.project1.fragments.MenuFragment;
import com.example.project1.fragments.PromotionFragment;

public class HomePagerAdapter extends FragmentStateAdapter {

    public HomePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MenuFragment();
            case 1:
                return new PromotionFragment();
            default:
                return new MenuFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
