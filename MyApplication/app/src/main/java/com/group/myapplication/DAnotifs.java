package com.group.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group.myapplication.Adapter2.BuyerNotificationsAdapter;
import com.group.myapplication.Adapter2.CashoutAdapter;
import com.group.myapplication.Model2.BuyerNotifications;
import com.group.myapplication.Model2.Cashout;
import com.group.myapplication.databinding.ActivityBuyerNotifBinding;
import com.group.myapplication.databinding.ActivityDanotifsBinding;

import java.util.ArrayList;
import java.util.List;

public class DAnotifs extends AppCompatActivity {

    ActivityDanotifsBinding binding;

    private FirebaseAuth firebaseAuth;
    private Toolbar mainToolbar;
    private FirebaseFirestore firestore;

    private RecyclerView recyclerView;
    private CashoutAdapter adapter;
    private List<Cashout> mList;
    private Query query;
    private ListenerRegistration listener;
    String currentuserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDanotifsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mainToolbar = findViewById(R.id.danotif_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("DA Notifications");
        currentuserID = firebaseAuth.getUid();

        recyclerView = findViewById(R.id.danotif_recview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(DAnotifs.this));

        mList = new ArrayList<>();
        adapter = new CashoutAdapter(DAnotifs.this, mList);
        recyclerView.setAdapter(adapter);

        if(firebaseAuth.getCurrentUser() != null){
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Boolean isBottom = !recyclerView.canScrollVertically(1);
                    if(isBottom)
                        Toast.makeText(DAnotifs.this, "You have reached the bottom.", Toast.LENGTH_SHORT).show();
                }
            });
            CollectionReference subCollectionRef = firestore.collection("Users").document(currentuserID).collection("CashoutNotif");
            query = subCollectionRef.orderBy("time1", Query.Direction.DESCENDING);
            listener = query.addSnapshotListener(DAnotifs.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for(DocumentChange doc : value.getDocumentChanges()){
                        if(doc.getType() ==  DocumentChange.Type.ADDED){
                            Cashout notif = doc.getDocument().toObject(Cashout.class);
                            mList.add(notif);
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
                    startActivity(new Intent(getApplicationContext(), DAnotifs.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.placeholder:
                    break;

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
            startActivity(new Intent(DAnotifs.this, SignIn.class));
        }
        return true;
    }
    @Override
    public void onStart() {
        firestore.collection("Users").document(currentuserID).collection("CashoutNotif")
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
