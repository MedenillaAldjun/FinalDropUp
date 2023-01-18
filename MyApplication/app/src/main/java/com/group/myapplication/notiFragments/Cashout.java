package com.group.myapplication.notiFragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.group.myapplication.Adapter2.NotificationsAdapter;
import com.group.myapplication.Model2.Notifications;
import com.group.myapplication.R;
import com.group.myapplication.SellerNotif;

import java.util.ArrayList;
import java.util.List;

public class Cashout extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private List<Notifications> mList;
    String currentuserID;
    SwipeRefreshLayout swipeRefreshLayout;


    private Query query,query1,query2;
    private ListenerRegistration listener,listener1,listener2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cashout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        swipeRefreshLayout = view.findViewById(R.id.cashout_swipelayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieve();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView = view.findViewById(R.id.cashout_recview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        currentuserID = firebaseAuth.getUid();

        mList = new ArrayList<>();
        adapter = new NotificationsAdapter(getActivity(), mList);
        recyclerView.setAdapter(adapter);

        if (firebaseAuth.getCurrentUser() != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Boolean isBottom = !recyclerView.canScrollVertically(1);
                    if (isBottom)
                        Toast.makeText(getContext(), "You have reached the bottom.", Toast.LENGTH_SHORT).show();
                }
            });
            CollectionReference subCollectionRef = firestore.collection("Users").document(currentuserID).collection("SellerCashoutNotif");
            query1 = subCollectionRef.orderBy("time2", Query.Direction.DESCENDING);
            listener1 = query1.addSnapshotListener( new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for(DocumentChange doc : value.getDocumentChanges()){
                        if(doc.getType() ==  DocumentChange.Type.ADDED){
                            Notifications notif = doc.getDocument().toObject(Notifications.class);
                            mList.add(notif);
                            adapter.notifyDataSetChanged();
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                    }
                    listener1.remove();
                }
            });


        }
    }

    public void retrieve() {
        firestore.collection("Users").document(currentuserID).collection("SellerCashoutNotif")
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