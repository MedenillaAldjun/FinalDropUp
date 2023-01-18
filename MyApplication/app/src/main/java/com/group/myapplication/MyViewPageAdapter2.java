package com.group.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.group.myapplication.buyernotifs.Dropped;
import com.group.myapplication.buyernotifs.Feedbacks;

public class MyViewPageAdapter2 extends FragmentStateAdapter {
    public MyViewPageAdapter2(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new Dropped();
            case 1:
                return new Feedbacks();
            default:
                return new Dropped();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
