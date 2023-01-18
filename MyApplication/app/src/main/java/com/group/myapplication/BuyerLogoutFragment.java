package com.group.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyerLogoutFragment extends Fragment {

    private CircleImageView circleImageView;
    private EditText profile_name;
    private Button btn_save, btn_switch, btn_switch1;

    private static final int img_req = 1;
    private Uri imgUrl = null;
    private Uri downloadUri = null;

    private FirebaseAuth auth;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private String Uid;

    private Toolbar signout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_buyer_logout, container, false);
    }

    //dito ipaste
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity ac = (AppCompatActivity) getActivity();

        //basic functions
        circleImageView = view.findViewById(R.id.profile_pic);
        profile_name = view.findViewById(R.id.profile_name);
        btn_save = view.findViewById(R.id.btn_save);
        btn_switch = view.findViewById(R.id.switchtoseller);
        btn_switch1 = view.findViewById(R.id.switchtoseller2);

//firebase authentications
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        Uid = auth.getCurrentUser().getUid();

//toolbar navigation
        signout = view.findViewById(R.id.signout_seller_toolbar);
        ac.setSupportActionBar(signout);
        ac.getSupportActionBar().setTitle("Buyer Account Settings");

        firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String imageUri = task.getResult().getString("image");
                        profile_name.setText(name);

                        Glide.with(BuyerLogoutFragment.this).load(imageUri).into(circleImageView);
                    }
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(getContext(), SendOTP.class));
                ac.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //Buyer to SellerView
        btn_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SellerSetUp.class));
                ac.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        btn_switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), DASetup.class));
                ac.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

    }
}
