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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUp extends AppCompatActivity {


    private CircleImageView circleImageView;
    private EditText profile_name;
    private Button btn_save;
    private FloatingActionButton next;

    private static final int img_req = 1;
    private Uri imgUrl = null;

    private FirebaseAuth auth;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private String Uid;
    private ProgressBar progressBar;
    ProgressDialog progressDialog;

    private Toolbar setup;
    BottomNavigationView bottom_nav;
    private boolean isPhotoSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        circleImageView = findViewById(R.id.profile_pic);
        profile_name = findViewById(R.id.profile_name);
        btn_save = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        Uid = auth.getCurrentUser().getUid();

        next = findViewById(R.id.next);

        setup = findViewById(R.id.setup_toolbar);
        setSupportActionBar(setup);
        getSupportActionBar().setTitle("Profile Settings");

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Main2.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });


        firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String imageUri = task.getResult().getString("image");
                        profile_name.setText(name);
                        imgUrl = Uri.parse(imageUri);

                        Glide.with(SetUp.this).load(imageUri).into(circleImageView);
                    }
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPD();
                String name = profile_name.getText().toString();
                StorageReference imageRef = storageReference.child("Profile pics").child(Uid + ".jpg");
                if(isPhotoSelected)
                {
                    if (!name.isEmpty() && imgUrl != null) {
                        imageRef.putFile(imgUrl).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            saveToFirestore(task, name, uri);
                                        }
                                    });
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    } else {
                        Toast.makeText(SetUp.this, "Please check the fields required!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    saveToFirestore(null, name, imgUrl);
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

    private void saveToFirestore(Task<UploadTask.TaskSnapshot> task, String name, Uri downloadUri) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("image",downloadUri.toString());

        firestore.collection("Users").document(Uid).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    startActivity(new Intent(SetUp.this, Main2.class));
                    finish();
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
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