package com.group.myapplication;

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
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group.myapplication.Adapter2.BuyerNotificationsAdapter;
import com.group.myapplication.Adapter2.NotificationsAdapter;
import com.group.myapplication.Model2.BuyerNotifications;
import com.group.myapplication.Model2.Notifications;
import com.group.myapplication.databinding.ActivityBuyerNotifBinding;
import com.group.myapplication.databinding.ActivitySellerNotifBinding;

import java.util.ArrayList;
import java.util.List;

public class BuyerNotif extends AppCompatActivity {

   ActivityBuyerNotifBinding binding;

    private FirebaseAuth firebaseAuth;
    private Toolbar mainToolbar;
    private FirebaseFirestore firestore;

    private RecyclerView recyclerView;
    private BuyerNotificationsAdapter adapter;
    private List<BuyerNotifications> mList;
    private Query query;
    private ListenerRegistration listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuyerNotifBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mainToolbar = findViewById(R.id.buyernotif_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Buyer Notications");

        recyclerView = findViewById(R.id.buyernotif_recview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(BuyerNotif.this));

        mList = new ArrayList<>();
        adapter = new BuyerNotificationsAdapter(BuyerNotif.this, mList);
        recyclerView.setAdapter(adapter);


        if(firebaseAuth.getCurrentUser() != null){
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Boolean isBottom = !recyclerView.canScrollVertically(1);
                    if(isBottom)
                        Toast.makeText(BuyerNotif.this, "You have reached the bottom.", Toast.LENGTH_SHORT).show();
                }
            });
            query = firestore.collection("BuyerNotifications").orderBy("time2", Query.Direction.DESCENDING);
            listener = query.addSnapshotListener(BuyerNotif.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for(DocumentChange doc : value.getDocumentChanges()){
                        if(doc.getType() ==  DocumentChange.Type.ADDED){
                            BuyerNotifications notif = doc.getDocument().toObject(BuyerNotifications.class);
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
                    startActivity(new Intent(getApplicationContext(), Main2.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.notif:
                    startActivity(new Intent(getApplicationContext(), BuyerNotif.class));
                    overridePendingTransition(0,0);
                    break;

                case R.id.placeholder:
                    break;

                case R.id.account:
                    replaceFragment(new ProfileFragment());
                    break;

                case R.id.settings:
                    replaceFragment(new BuyerLogoutFragment());
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
            startActivity(new Intent(BuyerNotif.this, SignIn.class));
        }
        return true;
    }

}