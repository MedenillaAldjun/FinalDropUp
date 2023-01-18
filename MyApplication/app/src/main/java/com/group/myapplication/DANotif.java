package com.group.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.group.myapplication.Adapter2.BuyerNotificationsAdapter;
import com.group.myapplication.Adapter2.CashoutAdapter;
import com.group.myapplication.Model2.BuyerNotifications;
import com.group.myapplication.Model2.Cashout;

import java.util.ArrayList;
import java.util.List;


public class DANotif extends Fragment {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_d_a_notif, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatActivity ac = (AppCompatActivity) getActivity();
        mainToolbar = view.findViewById(R.id.danotif_toolbar);
        ac.setSupportActionBar(mainToolbar);
        ac.getSupportActionBar().setTitle("DA Notifications");

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentuserID = firebaseAuth.getUid();

        recyclerView = view.findViewById(R.id.danotifs_recview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mList = new ArrayList<>();
        adapter = new CashoutAdapter(getActivity(), mList);
        recyclerView.setAdapter(adapter);

        if(firebaseAuth.getCurrentUser() != null){
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Boolean isBottom = !recyclerView.canScrollVertically(1);
                    if(isBottom)
                        Toast.makeText(getContext(), "You have reached the bottom.", Toast.LENGTH_SHORT).show();
                }
            });
            CollectionReference subCollectionRef = firestore.collection("Users").document(currentuserID).collection("CashoutNotif");
            query = subCollectionRef.orderBy("time1", Query.Direction.DESCENDING);
            listener = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
    }
}