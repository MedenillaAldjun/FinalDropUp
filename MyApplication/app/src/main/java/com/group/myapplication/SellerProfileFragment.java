package com.group.myapplication;

import static android.app.Activity.RESULT_OK;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerProfileFragment extends Fragment {

    private CircleImageView circleImageView;
    private EditText profile_name;
    private Button drop, notif, cashout;

    private static final int img_req = 1;
    private Uri imgUrl = null;
    private Uri downloadUri = null;

    private FirebaseAuth auth;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private String Uid;

    private Toolbar account;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_seller_profile, container, false);
    }


    //dito ipaste
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatActivity ac = (AppCompatActivity)getActivity();

        //basic functions
        circleImageView = view.findViewById(R.id.profile_pic);
        profile_name = view.findViewById(R.id.profile_name);
        drop = view.findViewById(R.id.dropreq2);
        notif= view.findViewById(R.id.notifyBuyers);
        cashout = view.findViewById(R.id.btn_cashout);

        cashout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SellerSendCashout.class);
                startActivity(intent);
                ac.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SellerSendNotif.class);
                startActivity(intent);
                ac.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DropRequest.class);
                startActivity(intent);
                ac.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

//firebase authentications
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        Uid = auth.getCurrentUser().getUid();

//toolbar navigation
        account = view.findViewById(R.id.sellerprofilefragment_toolbar);
        ac.setSupportActionBar(account);
        ac.getSupportActionBar().setTitle("Seller Account Profile");

        firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String imageUri = task.getResult().getString("image");
                        profile_name.setText(name);

                        Glide.with(SellerProfileFragment.this).load(imageUri).into(circleImageView);
                    }
                }
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, img_req);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == img_req && resultCode == RESULT_OK) {
            imgUrl = data.getData();
            circleImageView.setImageURI(imgUrl);

        }
    }


}