package com.group.myapplication.categoryfragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group.myapplication.Adapter.FashionAdapter;
import com.group.myapplication.Adapter.PostAdapter;
import com.group.myapplication.Model.Post;
import com.group.myapplication.R;

import java.util.ArrayList;
import java.util.List;


public class Fashion extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private Query query;
    private ListenerRegistration listener;
    private List<Post> mList;
    private FashionAdapter adapter;
    private RecyclerView recyclerView;

    SwipeRefreshLayout swipeRefreshLayout;

    private SearchView search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fashion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        swipeRefreshLayout = view.findViewById(R.id.fashion_swipelayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieve();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        search = view.findViewById(R.id.searchview);
        search.clearFocus();

        Animation slide_down;
        slide_down = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);

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

        recyclerView = view.findViewById(R.id.fashion_recview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mList = new ArrayList<>();
        adapter = new FashionAdapter(getContext(), mList);
        recyclerView.setAdapter(adapter);


        if(firebaseAuth.getCurrentUser() != null){
            retrieve();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Boolean isBottom = !recyclerView.canScrollVertically(1);
                    if(isBottom)
                        Toast.makeText(getContext(), "You have reached the bottom.", Toast.LENGTH_SHORT).show();
                }
            });
            query = firestore.collection("Fashion").orderBy("time", Query.Direction.DESCENDING);
            listener = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    }
    private void filterList(String text) {
        Animation slide_down;
        slide_down = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
        recyclerView.setVisibility(View.VISIBLE);
        List<Post> filteredList = new ArrayList<>();
        for (Post posted : mList) {
            if (posted.getProduct().toLowerCase().contains(text.toLowerCase())) {
                recyclerView.setAnimation(slide_down);
                adapter.setFilteredList(filteredList);
                filteredList.add(posted);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No data found!", Toast.LENGTH_SHORT).show();
        } else {
            recyclerView.setAnimation(slide_down);
            adapter.setFilteredList(filteredList);
        }
    }

    @Override
    public void onStart() {
        firestore.collection("Fashion")
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

    private void retrieve(){
        firestore.collection("Fashion")
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
    }

}