package com.group.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class AddPostCategory extends AppCompatActivity {
    Toolbar maintoolbar;
    ImageButton fashion, gadgets, clothes, shoes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_category);

        maintoolbar = findViewById(R.id.category_toolbar);
        setSupportActionBar(maintoolbar);
        getSupportActionBar().setTitle("Add Post");

        fashion = findViewById(R.id.fashion);
        gadgets = findViewById(R.id.gadgets);
        clothes = findViewById(R.id.clothes);
        shoes = findViewById(R.id.shoes);

        clothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddPostActivity.class));
            }
        });

        fashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddPostFashion.class));
            }
        });

        gadgets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddPostGadget.class));
            }
        });

        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddPostShoes.class));
            }
        });



    }
}