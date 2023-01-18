package com.group.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group.myapplication.Adapter.PostAdapter;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DropRequest extends AppCompatActivity {

    private Toolbar droprequest;
    private Button  btn_confirm;
    private EditText caption,totalincome;
    private ImageButton items_preview;
    private ProgressBar da_pb;

    private StorageReference storage;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String currentUserId;
    private Uri itemsImageUri;
    private final static int req = 1;
    private String pos;

   SearchableSpinner spinner;
   private DatabaseReference spinnerRef;
   public ArrayList<String> lists;
   ArrayAdapter<String> adapter;
    String selectedUserUid, name;
    ProgressDialog progressDialog;

   
   String list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_request);

        droprequest = findViewById(R.id.dropreq_toolbar);
        setSupportActionBar(droprequest);
        getSupportActionBar().setTitle("Add Dropping Request");
        totalincome = findViewById(R.id.total);

        lists = ((GlobalSpinner) this.getApplication()).getDa_list();
        spinner = findViewById(R.id.spinner);
        spinnerRef = FirebaseDatabase.getInstance().getReference("AddressDA");
        spinner.setTitle("Select Dropping Address:");
        adapter = new ArrayAdapter<String>(DropRequest.this, android.R.layout.simple_spinner_dropdown_item,lists);

        caption = findViewById(R.id.caption);

        items_preview = findViewById(R.id.items_preview);

        storage = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        da_pb = findViewById(R.id.req_progressBar);
        da_pb.setVisibility(View.INVISIBLE);

        btn_confirm = findViewById(R.id.btn_confirmreq);

        items_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, req);
            }
        });

        CollectionReference usersRef = firestore.collection("Users");

        List<String> usersNameList = new ArrayList<>();
        List<String> usersUidList = new ArrayList<>();

        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        name = document.getString("da_address");
                        String uid = document.getId();
                        usersNameList.add(name);
                        usersUidList.add(uid);


                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(DropRequest.this, android.R.layout.simple_spinner_item, usersNameList);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerAdapter);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(DropRequest.this);
                alert.setTitle("Dropping: TERMS & CONDITIONS")
                        .setMessage("NOTE: Upon DA's approval, your items are only allowed 3-5 days accommodation within the chosen Dropping Area. After which, would result in PENALTY.\n" +
                                "\nThis is is to ensure, and prevent items from being overdue and would cause piling up. \n" +
                                "\nAs for Cash Out transactions, we would be deducting 5% from your total income, as our Commission Fee.")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("I accept, proceed.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showPD();
                                String captions = caption.getText().toString();
                                String income = totalincome.getText().toString();

                                if (!captions.isEmpty() && itemsImageUri != null) {
                                    StorageReference postRef = storage.child("Item Images").child(FieldValue.serverTimestamp().toString() + ".jpg");
                                    postRef.putFile(itemsImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                postRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        HashMap<String, Object> postMap1 = new HashMap<>();
                                                        postMap1.put("image1", uri.toString());
                                                        postMap1.put("user1", currentUserId);
                                                        postMap1.put("caption1", captions);
                                                        postMap1.put("total", income);
                                                        postMap1.put("da_address", name);
                                                        postMap1.put("time1", FieldValue.serverTimestamp());

                                                        firestore.collection("Users").document(selectedUserUid).collection("sellerID").add(postMap1);

                                                        firestore.collection("Items").add(postMap1).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                if (task.isSuccessful()) {
                                                                    progressDialog.dismiss();
                                                                    LayoutInflater inflater = getLayoutInflater();
                                                                    View layout = inflater.inflate(R.layout.sent_toast, (ViewGroup)findViewById(R.id.sent_toast));
                                                                    final Toast toast = new Toast(getApplicationContext());
                                                                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                                                    toast.setDuration(Toast.LENGTH_SHORT);
                                                                    toast.setView(layout);

                                                                    toast.show();
                                                                    startActivity(new Intent(DropRequest.this, SellerDashboard.class));
                                                                    finish();
                                                                } else {
                                                                    da_pb.setVisibility(View.INVISIBLE);
                                                                    Toast.makeText(DropRequest.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                                    }
                                                });

                                            } else {
                                                da_pb.setVisibility(View.INVISIBLE);
                                                Toast.makeText(DropRequest.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                } else {
                                    da_pb.setVisibility(View.INVISIBLE);
                                    Toast.makeText(DropRequest.this, "Please check the fields required!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                alert.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == req && resultCode == RESULT_OK) {
            itemsImageUri = data.getData();
            items_preview.setImageURI(itemsImageUri);
        }
    }
    private void showPD(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Sending Request");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMax(100);
        progressDialog.show();
    }

}