package com.group.myapplication;

import androidx.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Ratings1 extends AppCompatActivity {

    private Toolbar toolbar;
    private RatingBar punctuality;
    private Button submit;

    private EditText caption;
    private ImageButton items_preview;
    private ProgressBar da_pb;

    private StorageReference storage;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String currentUserId;
    private Uri itemsImageUri;
    private final static int req = 1;

    ProgressDialog progressDialog;

    SearchableSpinner spinner1;
    private DatabaseReference spinnerRef1;
    public ArrayList<String> lists1;
    ArrayAdapter<String> adapter1;
    String selectedUserUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings1);

        toolbar = findViewById(R.id.buyerRating_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Rate Buyer");

        punctuality = findViewById(R.id.punctuality);
        submit = findViewById(R.id.submit);

        lists1 = ((GlobalSpinner) this.getApplication()).getSeller_list();
        spinner1 = findViewById(R.id.buyerSpinner);
        spinnerRef1 = FirebaseDatabase.getInstance().getReference("SellerID");
        spinner1.setTitle("Select Seller:");
        adapter1 = new ArrayAdapter<String>(Ratings1.this, android.R.layout.simple_spinner_dropdown_item,lists1);

        submit = findViewById(R.id.submit);

        caption = findViewById(R.id.buyercap);

        storage = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        da_pb = findViewById(R.id.buyerpbar);
        da_pb.setVisibility(View.INVISIBLE);

        CollectionReference usersRef = firestore.collection("Users");

        List<String> usersNameList = new ArrayList<>();
        List<String> usersUidList = new ArrayList<>();

        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getString("name");
                        String uid = document.getId();
                        usersNameList.add(name);
                        usersUidList.add(uid);


                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(Ratings1
                                .this, android.R.layout.simple_spinner_item, usersNameList);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner1.setAdapter(spinnerAdapter);

                        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedUserUid = usersUidList.get(i);


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    }
                }
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPD();
                String c = String.valueOf(punctuality.getRating());

                String captions = caption.getText().toString();

                if (!captions.isEmpty()) {
                    HashMap<String, Object> postMap1 = new HashMap<>();
                    postMap1.put("user2", currentUserId);
                    postMap1.put("caption2", captions);
                    postMap1.put("rating", c);
                    postMap1.put("time2", FieldValue.serverTimestamp());

                    firestore.collection("Users").document(selectedUserUid).collection("BuyerFeedback1").add(postMap1);
                    progressDialog.dismiss();
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.sent_toast, (ViewGroup)findViewById(R.id.sent_toast));
                    final Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);

                    toast.show();
                    startActivity(new Intent(Ratings1.this, Ratings1.class));
                    finish();
                } else {
                    da_pb.setVisibility(View.INVISIBLE);
                    Toast.makeText(Ratings1.this, "Please check the fields required!", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }
    private void showPD(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Submitting");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMax(100);
        progressDialog.show();
    }
}