package com.group.myapplication;


import android.app.Application;

import java.util.ArrayList;

public class GlobalSpinner extends Application {

    private ArrayList<String> da_list = new ArrayList<String>();

    public ArrayList<String> getDa_list() {
        return da_list;
    }

    public void setDa_list(ArrayList<String> da_list) {
        this.da_list = da_list;
    }

    private ArrayList<String> seller_list = new ArrayList<String>();

    public ArrayList<String> getSeller_list() {
        return seller_list;
    }

    public void setSeller_list(ArrayList<String> seller_list) {
        this.seller_list = seller_list;
    }
}
