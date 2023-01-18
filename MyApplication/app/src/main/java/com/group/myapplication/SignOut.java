package com.group.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import com.group.myapplication.databinding.ActivityAccountProfileBinding;
import com.group.myapplication.databinding.ActivitySignOutBinding;

import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignOut extends AppCompatActivity {

    ActivitySignOutBinding binding;

    private CircleImageView circleImageView;
    private EditText profile_name;
    private Button btn_save;

    private static final int img_req = 1;
    private Uri imgUrl = null;
    private Uri downloadUri = null;

    private FirebaseAuth auth;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private String Uid;

    private Toolbar signout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//basic functions
        circleImageView = findViewById(R.id.profile_pic);
        profile_name = findViewById(R.id.profile_name);
        btn_save = findViewById(R.id.btn_save);


//firebase authentications
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        Uid = auth.getCurrentUser().getUid();

//toolbar navigation
        signout = findViewById(R.id.signout_toolbar);
        setSupportActionBar(signout);
        getSupportActionBar().setTitle("Account Settings");


//bottom navigation button settings
        binding.navigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){

                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.notif:

                case R.id.placeholder:

                case R.id.account:
                    startActivity(new Intent(getApplicationContext(), AccountProfile.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.settings:
                    startActivity(new Intent(getApplicationContext(), SignOut.class));
                    overridePendingTransition(0,0);
                    return true;

            }

            return true;
        });

        firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String imageUri = task.getResult().getString("image");
                        profile_name.setText(name);

                        Glide.with(SignOut.this).load(imageUri).into(circleImageView);
                    }
                }
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(getApplicationContext(), SignIn.class));
                finish();
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

    private void saveToFirestore(Task<UploadTask.TaskSnapshot> task, String name, StorageReference imageRef) {
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                downloadUri = uri;
                HashMap<String, Object> map = new HashMap<>();
                map.put("name",name);
                map.put("image",downloadUri.toString());

                firestore.collection("Users").document(Uid).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(SignOut.this, "Profile Settings Saved!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignOut.this, MainActivity.class));
                            finish();
                        }else{

                            Toast.makeText(SignOut.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == img_req && resultCode == RESULT_OK) {
            imgUrl = data.getData();
            circleImageView.setImageURI(imgUrl);

        }
    }
}