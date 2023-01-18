package com.group.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.group.myapplication.Adapter1.RequestAdapter;
import com.group.myapplication.Adapter1.RequestAdapter1;
import com.group.myapplication.Model1.Request;
import com.group.myapplication.Model1.Request1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AdminPanel extends AppCompatActivity {

    private Toolbar mainToolbar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    String currentuserID;

    SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private RequestAdapter1 adapter,adapter1;
    private TextView totalprofit;
    private List<Request1> mList;
    private Query query;
    private ListenerRegistration listener;

    private CollectionReference collectionReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        collectionReference = FirebaseFirestore.getInstance().collection("Items");

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentuserID = firebaseAuth.getUid();
        swipeRefreshLayout = findViewById(R.id.admin_swipelayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onStart();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView = findViewById(R.id.admin_recview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminPanel.this));

        mList = new ArrayList<>();
        adapter = new RequestAdapter1(AdminPanel.this, mList);
        recyclerView.setAdapter(adapter);

        if(firebaseAuth.getCurrentUser() != null){
            onStart();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Boolean isBottom = !recyclerView.canScrollVertically(1);
                    if(isBottom)
                        Toast.makeText(AdminPanel.this, "You have reached the bottom.", Toast.LENGTH_SHORT).show();
                }
            });
            query = firestore.collection("Items").orderBy("time1", Query.Direction.DESCENDING);
            listener = query.addSnapshotListener(AdminPanel.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for(DocumentChange doc : value.getDocumentChanges()){
                        if(doc.getType() ==  DocumentChange.Type.ADDED){
                            Request1 req = doc.getDocument().toObject(Request1.class);
                            mList.add(req);
                            adapter.notifyDataSetChanged();
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                    }
                    listener.remove();
                }
            });
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile_menu) {
            startActivity(new Intent(getApplicationContext(), SetUp.class));
        } else if (item.getItemId() == R.id.signout_menu) {
            firebaseAuth.signOut();
            startActivity(new Intent(AdminPanel.this, SendOTP.class));
        }
        return true;
    }

    @Override
    protected void onStart() {
        firestore.collection("Items")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.d(TAG, "onEvent: ", error);
                            return;
                        } if(value != null){
                            List<DocumentChange> documentChangeList = value.getDocumentChanges();
                            for (DocumentChange documentChange : documentChangeList){
                                Log.d(TAG, "onEvent: " + documentChange.getDocument().getData());
                            }

                        }else{
                            Log.d(TAG, "onEvent: query snapshot was null");
                        }
                    }
                });
        super.onStart();
    }


}