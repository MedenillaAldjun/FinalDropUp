package com.group.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group.myapplication.Adapter1.RequestAdapter;
import com.group.myapplication.Model.Post;
import com.group.myapplication.Model1.Request;
import com.group.myapplication.databinding.ActivitySellerBinding;

import java.util.ArrayList;
import java.util.List;

public class DADashbaord extends AppCompatActivity {

    ActivitySellerBinding binding;

    private FirebaseAuth firebaseAuth;
    private Toolbar mainToolbar;
    private FirebaseFirestore firestore;
    String currentuserID;

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private RequestAdapter adapter;
    private List<Request> mList;
    private Query query;
    private ListenerRegistration listener;

    private SearchView search;


    //fragment initialize
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySellerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mainToolbar = findViewById(R.id.seller_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("DA Dashboard");
        currentuserID = firebaseAuth.getUid();

        recyclerView = findViewById(R.id.seller_recview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(DADashbaord.this));

        mList = new ArrayList<>();
        adapter = new RequestAdapter(DADashbaord.this, mList);
        recyclerView.setAdapter(adapter);

        fab = findViewById(R.id.addpost);

        search = findViewById(R.id.searchview);
        search.clearFocus();

        Animation slide_down;
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.startAnimation(slide_down);
                recyclerView.setVisibility(View.INVISIBLE);
            }

        });


        if(firebaseAuth.getCurrentUser() != null){
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Boolean isBottom = !recyclerView.canScrollVertically(1);
                    if(isBottom)
                        Toast.makeText(DADashbaord.this, "You have reached the bottom.", Toast.LENGTH_SHORT).show();
                }
            });
            CollectionReference subCollectionRef = firestore.collection("Users").document(currentuserID).collection("sellerID");
            query = subCollectionRef.orderBy("time1", Query.Direction.DESCENDING);
            listener = query.addSnapshotListener(DADashbaord.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for(DocumentChange doc : value.getDocumentChanges()){
                        if(doc.getType() ==  DocumentChange.Type.ADDED){
                            Request req = doc.getDocument().toObject(Request.class);
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

//bottom navigation button settings
        binding.navigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){

                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), DADashbaord.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.notif:
                    replaceFragment(new DANotif());
                    break;

                case R.id.placeholder:

                case R.id.account:
                    replaceFragment(new DAProfileFragment());
                    break;

                case R.id.settings:
                    replaceFragment(new SellerLogoutFragment());
                    break;
            }

            return true;
        });

    }

    private void filterList(String text) {
        Animation slide_down;
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        recyclerView.setVisibility(View.VISIBLE);
        List<Request> filteredList = new ArrayList<>();
        for (Request request : mList){
            if(request.getUser1().toLowerCase().contains(text.toLowerCase())){
                recyclerView.setAnimation(slide_down);
                adapter.setFilteredList(filteredList);
                filteredList.add(request);
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(this, "No data found!", Toast.LENGTH_SHORT).show();
        }else{
            recyclerView.setAnimation(slide_down);
            adapter.setFilteredList(filteredList);
        }
    }


//Override methods

    private void replaceFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_layout, fragment);
        ft.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile_menu) {
            startActivity(new Intent(getApplicationContext(), DASetup.class));
        } else if (item.getItemId() == R.id.signout_menu) {
            firebaseAuth.signOut();
            startActivity(new Intent(DADashbaord.this, SendOTP.class));
        }
        return true;
    }

    @Override
    protected void onStart() {
        firestore.collection("Users").document(currentuserID).collection("sellerID")
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