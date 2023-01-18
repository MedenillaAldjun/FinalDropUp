package com.group.myapplication.buyernotifs;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.group.myapplication.Adapter2.BuyerNotificationsAdapter;
import com.group.myapplication.Adapter2.OrderAdapter1;
import com.group.myapplication.Model2.BuyerNotifications;
import com.group.myapplication.Model2.Order1;
import com.group.myapplication.R;

import java.util.ArrayList;
import java.util.List;


public class Feedbacks extends Fragment {

    private FirebaseAuth firebaseAuth;
    private Toolbar mainToolbar;
    private FirebaseFirestore firestore;
    String currentuserID;

    private RecyclerView recyclerView;
    private OrderAdapter1 adapter;
    private List<Order1> mList;
    private Query query;
    private ListenerRegistration listener;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedbacks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.feedbacks_recview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        currentuserID = firebaseAuth.getUid();

        mList = new ArrayList<>();
        adapter = new OrderAdapter1(getActivity(), mList);
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
            CollectionReference subCollectionRef = firestore.collection("Users").document(currentuserID).collection("BuyerFeedback1");
            query = subCollectionRef.orderBy("time2", Query.Direction.DESCENDING);
            listener = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for(DocumentChange doc : value.getDocumentChanges()){
                        if(doc.getType() ==  DocumentChange.Type.ADDED){
                            Order1 notif = doc.getDocument().toObject(Order1.class);
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

    @Override
    public void onStart() {
        firestore.collection("Users").document(currentuserID).collection("BuyerFeedback1")
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

    public void retrieve() {
        firestore.collection("Users").document(currentuserID).collection("BuyerFeedback1")
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