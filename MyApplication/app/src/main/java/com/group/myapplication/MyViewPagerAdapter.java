package com.group.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.group.myapplication.Model.Post;
import com.group.myapplication.categoryfragments.Clothes;
import com.group.myapplication.categoryfragments.Fashion;
import com.group.myapplication.categoryfragments.Shoes;
import com.group.myapplication.categoryfragments.gadgets;

import java.util.ArrayList;
import java.util.List;

public class MyViewPagerAdapter extends FragmentStateAdapter {


    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new Clothes();
            case 1:
                return new gadgets();
            case 2:
                return new Fashion();
            case 3:
                return new Shoes();
            default:
                return new Clothes();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }



}
