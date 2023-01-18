package com.group.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group.myapplication.Adapter.CommentsAdapter;
import com.group.myapplication.Adapter2.NotificationsAdapter;
import com.group.myapplication.Model.Comments;
import com.group.myapplication.Model.Users;
import com.group.myapplication.Model2.Notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    private EditText comment;
    private ImageButton btncomment;
    private RecyclerView com_recview;
    private FirebaseFirestore firestore;
    private String post_id;
    private String currentUserId;
    private FirebaseAuth auth;
    private CircleImageView comprof;

    private CommentsAdapter adapter;
    private List<Comments> mList;
    private Toolbar comment_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        comment = findViewById(R.id.commented);
        btncomment = findViewById(R.id.btn_comment);
        com_recview = findViewById(R.id.comments_recview);
        comprof = findViewById(R.id.profile_pic3);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        comment_toolbar = findViewById(R.id.comments_toolbar);
        setSupportActionBar(comment_toolbar);
        getSupportActionBar().setTitle("Post's Comments");

        mList = new ArrayList<>();
        adapter = new CommentsAdapter(CommentsActivity.this, mList);

        post_id = getIntent().getStringExtra("postid");
        com_recview.setHasFixedSize(true);
        com_recview.setLayoutManager(new LinearLayoutManager(this));
        com_recview.setAdapter(adapter);


        firestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String imageUri = task.getResult().getString("image");

                        Glide.with(CommentsActivity.this).load(imageUri).into(comprof);
                    }
                }
            }
        });

        firestore.collection("Posts/" + post_id + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Comments comment = documentChange.getDocument().toObject(Comments.class);
                        mList.add(comment);
                        adapter.notifyDataSetChanged();
                    } else   {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        btncomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String com = comment.getText().toString();
                if (!com.isEmpty()) {
                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("comment", com);
                    commentsMap.put("time", FieldValue.serverTimestamp());
                    commentsMap.put("user", currentUserId);

                    firestore.collection("Posts/" + post_id + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CommentsActivity.this, "Comment Added Successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CommentsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(CommentsActivity.this, "Please write a comment!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}