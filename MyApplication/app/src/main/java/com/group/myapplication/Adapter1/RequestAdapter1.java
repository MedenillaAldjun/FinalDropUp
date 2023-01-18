package com.group.myapplication.Adapter1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.group.myapplication.CommentsActivity;
import com.group.myapplication.DASendCashout;
import com.group.myapplication.DASendNotif;
import com.group.myapplication.GlobalTotal;
import com.group.myapplication.Model.Post;
import com.group.myapplication.Model1.Request;
import com.group.myapplication.Model1.Request1;
import com.group.myapplication.R;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter1 extends RecyclerView.Adapter<RequestAdapter1.RequestViewHolder> {

    private List<Request1> mList;
    private Activity context;
    private FirebaseFirestore firestore;
    String documentID;
    AppCompatActivity ac;
    ProgressDialog progressDialog;

    public void setFilteredList(List<Request1> filteredList){
        this.mList =  filteredList;
        notifyDataSetChanged();
    }

    public RequestAdapter1(Activity context,  List<Request1> mList){
        this.mList = mList;
        this.context = context;
    }

    public void setData(List<Request1> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_income, parent, false);
        firestore = FirebaseFirestore.getInstance();
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position1) {
        Request1 req = mList.get(position1);
        holder.setItemsPic(req.getImage1());


        String stringValue = req.getTotal();
        int intValue = Integer.parseInt(stringValue);
        holder.setTotalincome("PHP "+intValue);

        holder.setTotalProfit("PHP " + intValue * .05);

        holder.setDa_address(req.getDa_address());

        long milli1 = req.getTime1().getTime();
        String date = DateFormat.format("MM/dd/yyy", new Date(milli1)).toString();
        holder.setPostDate(date);

        CollectionReference commentsRef = firestore.collection("Items");
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
                alert.setTitle("Delete Item?")
                        .setMessage("Are you sure?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showPD();
                                CollectionReference commentsRef = firestore.collection("Items");
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

        String userId = req.getUser1();
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
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class RequestViewHolder extends RecyclerView.ViewHolder{

        ImageView itemsPic;
        CircleImageView profile_pic;
        ImageButton delete;
        TextView postUsername,postDate, totalincome, totalProfit, da_address;
        View view;


        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            delete = view.findViewById(R.id.admin_deleteincome);

        }

        public void setItemsPic(String picture){
            itemsPic = view.findViewById(R.id.droppingarea_image);
            Glide.with(context).load(picture).into(itemsPic);
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
        public void setTotalincome (String cap){
            totalincome = view.findViewById(R.id.user_income);
            totalincome.setText(cap);
        }
        public void setTotalProfit (String cap){
            totalProfit = view.findViewById(R.id.profit);
            totalProfit.setText(cap);
        }
        public void setDa_address (String cap){
            da_address = view.findViewById(R.id.da_address);
            da_address.setText(cap);
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
