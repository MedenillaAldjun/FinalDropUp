package com.group.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DASetup extends AppCompatActivity {
    private CircleImageView circleImageView;
    private EditText profile_name,address_da;
    private Button btn_save;
    private FloatingActionButton next;

    private static final int img_req = 1;
    private Uri imgUrl = null;

    public ArrayList<String> lists;
    private DatabaseReference spinnerRef;
    ArrayAdapter<String> adapter;

    private FirebaseAuth auth;
    private StorageReference storageReference;

    private FirebaseFirestore firestore;
    private String Uid;
    private ProgressBar progressBar;

    private Toolbar setup;
    BottomNavigationView bottom_nav;
    private boolean isPhotoSelected = false;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dasetup);

        circleImageView = findViewById(R.id.profile_pic);
        profile_name = findViewById(R.id.profile_name);
        address_da = findViewById(R.id.address_da);
        btn_save = findViewById(R.id.btn_save);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        auth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        Uid = auth.getCurrentUser().getUid();

        spinnerRef = FirebaseDatabase.getInstance().getReference("AddressDA");
        lists = ((GlobalSpinner) this.getApplication()).getDa_list();
        adapter = new ArrayAdapter<String>(DASetup.this, android.R.layout.simple_spinner_dropdown_item, lists);


        next = findViewById(R.id.next);
        setup = findViewById(R.id.dasetup_toolbar);
        setSupportActionBar(setup);
        getSupportActionBar().setTitle("DA Profile SetUp");

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DADashbaord.class));
                finish();
            }
        });


        firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String imageUri = task.getResult().getString("image");
                        String name = task.getResult().getString("name");
                        String addressda = task.getResult().getString("da_address");
                        address_da.setText(addressda);
                        profile_name.setText(name);

                        Glide.with(DASetup.this).load(imageUri).into(circleImageView);
                    }
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPD();
                String sellerID = address_da.getText().toString();

                if(sellerID != null){
                    HashMap<String, Object> postMap = new HashMap<>();
                    postMap.put("da_address", sellerID);

                    firestore.collection("Users").document(Uid).set(postMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.profilesaved_toast, (ViewGroup)findViewById(R.id.profilesaved_toast));
                                final Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);

                                toast.show();
                                startActivity(new Intent(DASetup.this, DADashbaord.class));
                                finish();
                            }
                            else{
                                Toast.makeText(DASetup.this, "Profile Settings Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }else{
                    Toast.makeText(DASetup.this, "Please enter your Seller ID!", Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == img_req && resultCode == RESULT_OK) {
            imgUrl = data.getData();
            circleImageView.setImageURI(imgUrl);

            isPhotoSelected = true;
        }
    }
    private void showPD(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Setting Profile");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMax(100);
        progressDialog.show();
    }

}

