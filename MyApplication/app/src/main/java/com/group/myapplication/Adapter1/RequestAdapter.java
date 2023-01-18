package com.group.myapplication.Adapter1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.group.myapplication.CommentsActivity;
import com.group.myapplication.DASendCashout;
import com.group.myapplication.DASendNotif;
import com.group.myapplication.Model.Post;
import com.group.myapplication.Model1.Request;
import com.group.myapplication.R;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private List<Request> mList;
    private Activity context;
    private FirebaseFirestore firestore;
    String documentID;
    AppCompatActivity ac;
    ProgressDialog progressDialog;

    public void setFilteredList(List<Request> filteredList){
        this.mList =  filteredList;
        notifyDataSetChanged();
    }

    public RequestAdapter(Activity context,  List<Request> mList){
        this.mList = mList;
        this.context = context;
    }

    public String getDocumentId(int position) {
        return mList.get(position).getDocumentID();
    }


    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_request, parent, false);
        firestore = FirebaseFirestore.getInstance();
        return new RequestViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position1) {



        Request req = mList.get(position1);

        holder.setItemsPic(req.getImage1());
        holder.setPostCaption(req.getCaption1());

        String stringValue = req.getTotal();
        int intValue = Integer.parseInt(stringValue);

        holder.setTotalincome("Total Income: " + intValue);


        
        long milli1 = req.getTime1().getTime();
        String date = DateFormat.format("MM/dd/yyy", new Date(milli1)).toString();
        holder.setPostDate(date);

        String userId = req.getUser1();
        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String username1 = task.getResult().getString("sellerid");
                    String image1 = task.getResult().getString("image");

                    holder.setProfile_pic(image1);
                    holder.setPostUsername(username1);
                }else{
                    Toast.makeText(context, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
// Assume you have a reference to the parent document in the "posts" collection
        DocumentReference postRef = firestore.collection("Users").document(userId);

// Get the "comments" subcollection of the post
        CollectionReference commentsRef = postRef.collection("sellerID");

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

        String postId = req.PostId;
        if(userId.equals(req.getUser1())){
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Delete Request?")
                            .setMessage("Are you sure?")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    showPD();
                                    CollectionReference commentsRef = firestore.collection("Users").document(userId).collection("sellerID");
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




    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder{

        ImageView itemsPic;
        CircleImageView profile_pic;
        TextView postUsername,postDate,postCaption, totalincome;
        View view;
        Button notifApprove, notifDecline, notifDetails, notifPenalty;
        ImageButton delete;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            notifApprove = view.findViewById(R.id.approve);
            notifDecline = view.findViewById(R.id.decline);
            notifDetails = view.findViewById(R.id.send_details);
            notifPenalty = view.findViewById(R.id.penalize);
            delete = view.findViewById(R.id.delete_req);

            notifApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent send = new Intent(context, DASendNotif.class);
                    context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    context.startActivity(send);

                }
            });

            notifDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent send = new Intent(context, DASendNotif.class);
                    context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    context.startActivity(send);
                }
            });

            notifPenalty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent send = new Intent(context, DASendNotif.class);
                    context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    context.startActivity(send);

                }
            });

            notifDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent send = new Intent(context, DASendCashout.class);
                    context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    context.startActivity(send);

                }
            });
        }

        public void setItemsPic(String picture){
            itemsPic = view.findViewById(R.id.items_id);
            Glide.with(context).load(picture).into(itemsPic);
        }
        public void setProfile_pic(String profile){
            profile_pic = view.findViewById(R.id.profile_pic1);
            Glide.with(context).load(profile).into(profile_pic);
        }
        public void setPostUsername (String user){
            postUsername = view.findViewById(R.id.username1);
            postUsername.setText(user);
        }
        public void setPostDate (String time){
            postDate = view.findViewById(R.id.date1);
            postDate.setText(time);
        }
        public void setPostCaption (String cap){
            postCaption = view.findViewById(R.id.caption_id);
            postCaption.setText(cap);
        }
        public void setTotalincome (String cap){
            totalincome = view.findViewById(R.id.income);
            totalincome.setText(cap);
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
