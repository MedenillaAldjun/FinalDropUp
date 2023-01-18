package com.group.myapplication.Adapter2;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.group.myapplication.DASendNotif;
import com.group.myapplication.Model2.Notifications;
import com.group.myapplication.R;
import com.group.myapplication.Ratings;
import com.group.myapplication.Ratings1;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.RequestViewHolder> {

    private List<Notifications> mList;
    private Activity context;
    String documentID;
    ProgressDialog progressDialog;

    private FirebaseFirestore firestore;

    public NotificationsAdapter(Activity context,  List<Notifications> mList){
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationsAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_notif, parent, false);
        firestore = FirebaseFirestore.getInstance();
        return new NotificationsAdapter.RequestViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.RequestViewHolder holder, int position1) {
        Notifications notif = mList.get(position1);
        holder.setItemsPic(notif.getImage2());
        holder.setPostCaption(notif.getCaption2());

        long milli1 = notif.getTime2().getTime();
        String date = DateFormat.format("MM/dd/yyy", new Date(milli1)).toString();
        holder.setPostDate(date);

        String userId = notif.getUser2();
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

        DocumentReference postRef = firestore.collection("Users").document(userId);

// Get the "comments" subcollection of the post
        CollectionReference commentsRef = postRef.collection("Approval");

        commentsRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //document is a DocumentSnapshot
                                documentID = document.getId();
                                // do something with the commentId
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Delete Notification?")
                        .setMessage("Are you sure?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showPD();
                                CollectionReference commentsRef = firestore.collection("Users").document(userId).collection("Approval");
                                commentsRef.document(documentID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                            mList.remove(position1);
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

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder{
        ImageView DAPic;
        CircleImageView profile_pic;
        TextView postUsername,postDate,postCaption;
        View view;

        ImageButton delete;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            delete = view.findViewById(R.id.approval_cashout_del);
        }

        public void setItemsPic(String picture){
            DAPic = view.findViewById(R.id.droppingarea_image);
            Glide.with(context).load(picture).into(DAPic);
        }
        public void setProfile_pic(String profile){
            profile_pic = view.findViewById(R.id.profile_pic2);
            Glide.with(context).load(profile).into(profile_pic);
        }
        public void setPostUsername (String user){
            postUsername = view.findViewById(R.id.username2);
            postUsername.setText(user);
        }
        public void setPostDate (String time){
            postDate = view.findViewById(R.id.date2);
            postDate.setText(time);
        }
        public void setPostCaption (String cap){
            postCaption = view.findViewById(R.id.da_requestCaption);
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
