package com.devraj.blogapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    List<BlogPostModel> blog_list;
    Context context;

    FirebaseFirestore firebaseFirestore;


    public BlogRecyclerAdapter(List<BlogPostModel> blog_list) {
        this.blog_list = blog_list;
    }

    @NonNull
    @Override
    public BlogRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context = parent.getContext();

        firebaseFirestore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BlogRecyclerAdapter.ViewHolder holder, int position) {

        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String image_url = blog_list.get(position).getImage_url();
        String image_thumb = blog_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url, image_thumb);



        //retrieve userdata
        String user_id = blog_list.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    String username = task.getResult().getString("name");
                    String image = task.getResult().getString("image");

                    holder.setUserdata(username, image);


                } else {
                    String error = task.getException().getLocalizedMessage();
                    Toast.makeText(context, "Error:" + error, Toast.LENGTH_SHORT).show();
                }

            }
        });

        //to retrievethe timestamp
        long milliseconds = blog_list.get(position).getTimestamp().getTime();
        String dateString = android.text.format.DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();
        holder.setTime(dateString);

        holder.blogcommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentintent = new Intent(context, CommentActivity.class);
                context.startActivity(commentintent);
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return blog_list.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        View myview;
        TextView descview;
        ImageView blogImageview;

        TextView userdata;
        CircleImageView imagedata;

        TextView blogdate;

        ImageView blogcommentBtn;


        public ViewHolder(View itemView) {
            super(itemView);
            myview = itemView;

            blogcommentBtn = myview.findViewById(R.id.blog_comment_icon);

        }

        public void setDescText(String descText) {
            descview = myview.findViewById(R.id.blog_desc);
            descview.setText(descText);
        }

        public void setBlogImage(String downloadUri, String imagethumburi) {
            blogImageview = myview.findViewById(R.id.blog_image);

            RequestOptions placeholderoptions = new RequestOptions();
            placeholderoptions.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderoptions)
                    .load(downloadUri).thumbnail(Glide.with(context).load(imagethumburi))
                    .into(blogImageview);
        }

        public void setUserdata(String name, String image) {
            userdata = myview.findViewById(R.id.blog_user_name);
            imagedata = myview.findViewById(R.id.blog_user_image);

            userdata.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(imagedata);
        }

        //for getting date
        public void setTime(String date) {
            blogdate = myview.findViewById(R.id.blog_date);
            blogdate.setText(date);
        }

    }
}
