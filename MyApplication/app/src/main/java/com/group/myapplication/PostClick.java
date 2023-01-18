package com.group.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PostClick extends AppCompatActivity {

    ImageView img;
    TextView product,price,da,fb,gname,gnum;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_click);

        toolbar = findViewById(R.id.postdetails_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post Details");

        product = findViewById(R.id.click_prod);
        price = findViewById(R.id.click_price);
        da = findViewById(R.id.click_da);
        fb = findViewById(R.id.click_fb);
        gname = findViewById(R.id.click_gname);
        gnum = findViewById(R.id.click_gnum);

        fb.setText(getIntent().getStringExtra("fb_name"));

        product.setText(getIntent().getStringExtra("prod_id"));
        price.setText(getIntent().getStringExtra("price"));
        da.setText(getIntent().getStringExtra("da"));
        gname.setText(getIntent().getStringExtra("gname"));
        gnum.setText(getIntent().getStringExtra("gnum"));


    }
}