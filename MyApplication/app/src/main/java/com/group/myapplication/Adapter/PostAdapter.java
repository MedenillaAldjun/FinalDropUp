package com.group.myapplication.Adapter;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.accessibility.AccessibilityManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.group.myapplication.CommentsActivity;
import com.group.myapplication.DASendNotif;
import com.group.myapplication.Model.Post;
import com.group.myapplication.PostClick;
import com.group.myapplication.R;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> List;
    private Context context;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    AppCompatActivity ac;
    ProgressDialog progressDialog;

    public void setFilteredList(List<Post> filteredList){
        this.List =  filteredList;
        notifyDataSetChanged();
    }

    public PostAdapter (Context context, List<Post> List){
        this.List = List;
        this.context = context;

    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_post, parent, false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = List.get(position);
        holder.setPost_id(post.getImage());

        holder.setPostFbname(post.getFbname());
        holder.setPostProducts("Product: " +post.getProduct());
        holder.setPostPrice("Price: " + post.getPrice());
        holder.setPostAddress("Dropping Address: " + post.getAddress());
        holder.setPostGcashname("FB Name: " + post.getGcashname());
        holder.setPostGcashnum("GCash: " + post.getGcashnum());



        long milli = post.getTime().getTime();
        String date = DateFormat.format("MM/dd/yyy", new Date(milli)).toString();
        holder.setPostDate(date);


        String userId = post.getUser();
        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String username = task.getResult().getString("name");
                    String image = task.getResult().getString("image");

                    holder.setProfile_pic(image);
                    holder.setPostUsernames(username);
                }else{
                    Toast.makeText(context, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        //likebtn
        String postId = post.PostId;
        String currentUserId = auth.getCurrentUser().getUid();
        holder.likepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).set(likesMap);
                        }else {
                            firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).delete();
                        }
                    }
                });
            }
        });

        //like color change
        firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null){
                    if(value.exists()){
                        holder.likepic.setImageDrawable(context.getDrawable(R.drawable.pressed));
                    }else{
                        holder.likepic.setImageDrawable(context.getDrawable(R.drawable.before));
                    }
                }
            }
        });

        //likescount
        firestore.collection("Posts/" + postId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null){
                    if(!value.isEmpty()){
                        int count = value.size();
                        holder.setPostLikecounts(count);
                    }
                }else{
                        holder.setPostLikecounts(0);
                }
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent comment = new Intent(context, CommentsActivity.class);
                comment.putExtra("postid", postId);
                context.startActivity(comment);
            }
        });
        
        if(currentUserId.equals(post.getUser())){
            holder.sold.setVisibility(View.VISIBLE);
            holder.sold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Mark as Sold?")
                            .setMessage("NOTE: Upon marking your post sold, your post will be deleted including the all likes and comments. \n" +


                                    "\nWe recommend you to take a screenshot on the comment section first, before proceeding.")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("I understand, proceed.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    showPD();
                                   firestore.collection("Posts/" + postId + "/Comments").get()
                                           .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                               @Override
                                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                for (QueryDocumentSnapshot snapshot: task.getResult()){
                                                    firestore.collection("Posts/" + postId + "/Comments").document(snapshot.getId()).delete();
                                                }
                                               }
                                           });
                                    firestore.collection("Posts/" + postId + "/Likes").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    for (QueryDocumentSnapshot snapshot: task.getResult()){
                                                        firestore.collection("Posts/" + postId + "/Likes").document(snapshot.getId()).delete();
                                                    }
                                                }
                                            });
                                    firestore.collection("Posts").document(postId).delete();
                                    progressDialog.dismiss();
                                    LayoutInflater inflater = LayoutInflater.from(context);
                                    View layout = inflater.inflate(R.layout.deleted_toast, (ViewGroup)view.findViewById(R.id.deleted_toast));
                                    final Toast toast = new Toast(context);
                                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.setView(layout);

                                    toast.show();
                                    List.remove(position);
                                    notifyDataSetChanged();
                                }
                            });
                    alert.show();
                }
            });
        }

        holder.post_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostClick.class);

                intent.putExtra("post", post.getImage());
                intent.putExtra("prod_id", post.getProduct());
                intent.putExtra("price", post.getPrice());
                intent.putExtra("da", post.getAddress());
                intent.putExtra("fb_name", post.getFbname());
                intent.putExtra("gname", post.getGcashname());
                intent.putExtra("gnum", post.getGcashnum());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView post_id, likepic, comments;
        CircleImageView profile_pic;
        TextView postUsernames, postDate, postProducts, postAddress, postPrice, postLikecounts, postFbname, postGcashname, postGcashnum,
        dd_product, dd_price, dd_da, dd_gname, dd_gnum;
        ImageButton sold;
        View mView;
        LinearLayout layout;

        CardView post_click;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            likepic = mView.findViewById(R.id.like_id);
            comments = mView.findViewById(R.id.comment);
            sold = mView.findViewById(R.id.soldbtn);

            dd_product = mView.findViewById(R.id.dd_product);
            dd_price = mView.findViewById(R.id.dd_price);
            dd_da = mView.findViewById(R.id.dd_da);
            dd_gname = mView.findViewById(R.id.dd_gname);
            dd_gnum = mView.findViewById(R.id.dd_gnum);

            layout = mView.findViewById(R.id.layout);
            post_click = mView.findViewById(R.id.postdetails);

            post_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dd_product.setText(postProducts.getText());
                    int a = (dd_product.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;

                    dd_price.setText(postPrice.getText());
                    int b = (dd_price.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;

                    dd_da.setText(postAddress.getText());
                    int c = (dd_da.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;

                    dd_gname.setText(postGcashname.getText());
                    int d = (dd_gname.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;

                    dd_gnum.setText(postGcashnum.getText());
                    int e = (dd_gnum.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;


                    TransitionManager.beginDelayedTransition(layout, new AutoTransition());
                    dd_product.setVisibility(a);
                    dd_price.setVisibility(b);
                    dd_da.setVisibility(c);
                    dd_gname.setVisibility(d);
                    dd_gnum.setVisibility(e);
                }
            });

        }

        public void setPostLikecounts(int count){
            postLikecounts =  mView.findViewById(R.id.likecount_id);
            postLikecounts.setText(count + " Likes");
        }
        public void setPost_id(String urlPost){
            post_id = mView.findViewById(R.id.post_id);
            Glide.with(context).load(urlPost).into(post_id);
        }
        public void setProfile_pic(String urlProfile){
            profile_pic = mView.findViewById(R.id.profile_pic);
            Glide.with(context).load(urlProfile).into(profile_pic);
        }
       public void setPostUsernames(String usernames){
            postUsernames = mView.findViewById(R.id.username);
            postUsernames.setText(usernames);
       }
        public void setPostDate(String date){
            postDate = mView.findViewById(R.id.date);
            postDate.setText(date);
        }

        public void setPostFbname(String fb){
            postFbname = mView.findViewById(R.id.fbname_id);
            postFbname.setText(fb);
        }

        public void setPostProducts(String products){
            postProducts = mView.findViewById(R.id.prod_id);
            postProducts.setText(products);
        }

        public void setPostPrice(String price){
            postPrice = mView.findViewById(R.id.pr_id);
            postPrice.setText(price);
        }

        public void setPostAddress(String address){
            postAddress= mView.findViewById(R.id.address_id);
            postAddress.setText(address);
        }

        public void setPostGcashname(String address){
            postGcashname = mView.findViewById(R.id.gname);
            postGcashname.setText(address);
        }

        public void setPostGcashnum(String address){
            postGcashnum = mView.findViewById(R.id.gnum);
            postGcashnum.setText(address);
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
