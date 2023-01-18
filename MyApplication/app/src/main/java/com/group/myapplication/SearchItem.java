package com.group.myapplication;

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

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group.myapplication.Adapter.PostAdapter;
import com.group.myapplication.Model.Post;
import com.group.myapplication.databinding.ActivityMainBinding;
import com.group.myapplication.databinding.ActivitySearchItemBinding;

import java.util.ArrayList;
import java.util.List;

public class SearchItem extends AppCompatActivity {

    ActivitySearchItemBinding binding;

    private FirebaseAuth firebaseAuth;
    private Toolbar mainToolbar;
    private FirebaseFirestore firestore;

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private PostAdapter adapter;
    private List<Post> mList;
    private Query query;
    private ListenerRegistration listener;
    private String currentUserId;

    private SearchView search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mainToolbar = findViewById(R.id.main_toolbar);
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Buyer Dashboard");
        //replaceFragment(new DashboardFragment());

        recyclerView = findViewById(R.id.main_recview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchItem.this));

        mList = new ArrayList<>();
        adapter = new PostAdapter(SearchItem.this, mList);
        recyclerView.setAdapter(adapter);

        fab = findViewById(R.id.addpost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SearchItem.class));
                overridePendingTransition(0,0);
            }
        });

        search = findViewById(R.id.searchview);
        search.clearFocus();

        Animation slide_down;
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        search.startAnimation(slide_down);


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

        if(firebaseAuth.getCurrentUser() != null){
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Boolean isBottom = !recyclerView.canScrollVertically(1);
                    if(isBottom)
                        Toast.makeText(SearchItem.this, "You have reached the bottom.", Toast.LENGTH_SHORT).show();
                }
            });
            query = firestore.collection("Posts").orderBy("time", Query.Direction.DESCENDING);
            listener = query.addSnapshotListener(SearchItem.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for(DocumentChange doc : value.getDocumentChanges()){
                        if(doc.getType() ==  DocumentChange.Type.ADDED){
                            String postId = doc.getDocument().getId();
                            Post post = doc.getDocument().toObject(Post.class).withId(postId);
                            mList.add(post);
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
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.notif:
                    startActivity(new Intent(getApplicationContext(), BuyerNotif.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.placeholder:

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

    private void filterList(String text) {
        List<Post> filteredList = new ArrayList<>();
        for (Post posted : mList){
            if(posted.getProduct().toLowerCase().contains(text.toLowerCase())){
                if(currentUserId.equals(posted.getUser())) {
                    filteredList.add(posted);
                }
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }else{
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
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getApplicationContext(), SignIn.class));
            finish();
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
            startActivity(new Intent(SearchItem.this, SignIn.class));
        }
        return true;
    }

}