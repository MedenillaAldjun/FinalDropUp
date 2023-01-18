package com.group.myapplication.Adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.group.myapplication.CommentsActivity;
import com.group.myapplication.DADashbaord;
import com.group.myapplication.DASendNotif;
import com.group.myapplication.Model.Comments;
import com.group.myapplication.Model.PostId;
import com.group.myapplication.R;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private List<Comments> mList;
    private Activity context;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String currentuserID, documentID, postId, documentID1;
    AppCompatActivity ac;
    ProgressDialog progressDialog;

    public CommentsAdapter(Activity context,  List<Comments> mList){
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_comment, parent, false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentuserID = auth.getUid();
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        Comments comment = mList.get(position);
        holder.setPostCaption(comment.getComment());

        String userId = comment.getUser();
        String currentUserId = auth.getCurrentUser().getUid();
        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String username1 = task.getResult().getString("name");
                    String image1 = task.getResult().getString("image");

                    holder.setProfile_pic(image1);
                    holder.setPostUsername(username1);
                }else{
                    Toast.makeText(context, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });


// Get the "comments" subcollection of the post
        CollectionReference commentsRef = firestore.collection("Posts");
        commentsRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //document is a DocumentSnapshot
                                documentID = document.getId();
                                // do something with the commentId
                                CollectionReference commentsRef1 = firestore.collection("Posts").document(documentID).collection("Comments");
                                commentsRef1.get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        //document is a DocumentSnapshot
                                                        documentID1 = document.getId();
                                                        // do something with the commentId
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



         postId = comment.PostId;
        if(currentUserId.equals(comment.getUser())){
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Delete Comment?")
                            .setMessage("Are you sure?")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    showPD();
                                    CollectionReference commentsRef = firestore.collection("Posts").document(documentID).collection("Comments");
                                    commentsRef.document(documentID1).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                LayoutInflater inflater = LayoutInflater.from(context);
                                                View layout = inflater.inflate(R.layout.deleted_toast, (ViewGroup)view.findViewById(R.id.deleted_toast));
                                                final Toast toast = new Toast(context);
                                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                                toast.setDuration(Toast.LENGTH_SHORT);
                                                toast.setView(layout);

                                                toast.show();
                                                mList.remove(position);
                                                notifyDataSetChanged();
                                            } else {
                                                System.out.println("Error deleting document: " + task.getException());
                                            }
                                        }
                                    });
                                }
                            });
                    alert.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profile_pic, profile_picCom;
        TextView postUsername,
                 postDate, postCaption;
        ImageButton delete;
        View view;
        CardView card;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            card = view.findViewById(R.id.view);
            postCaption = view.findViewById(R.id.user_comments);

            delete = view.findViewById(R.id.delete_comment);
        }
        public void setProfile_pic(String profile){
            profile_pic = view.findViewById(R.id.profile_pic2);
            Glide.with(context).load(profile).into(profile_pic);
        }

        public void setPostUsername (String user){
            postUsername = view.findViewById(R.id.username2);
            postUsername.setText(user);
        }

        public void setPostCaption (String cap){
            postCaption = view.findViewById(R.id.user_comments);
            postCaption.setText(cap);
        }


    }
    private void showPD(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Deleting Item");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMax(100);
        progressDialog.show();
    }

}
