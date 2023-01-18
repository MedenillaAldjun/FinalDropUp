package com.group.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.group.myapplication.categoryfragments.Clothes;
import com.group.myapplication.categoryfragments.Fashion;
import com.group.myapplication.categoryfragments.Shoes;
import com.group.myapplication.categoryfragments.gadgets;
import com.group.myapplication.notiFragments.Approval;
import com.group.myapplication.notiFragments.Cashout;
import com.group.myapplication.notiFragments.Feedback;

public class MyViewPageAdapter1 extends FragmentStateAdapter {
    public MyViewPageAdapter1(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new Approval();
            case 1:
                return new Cashout();
            case 2:
                return new Feedback();
            default:
                return new Approval();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
