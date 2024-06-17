
package com.example.spike_player.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spike_player.Templates.UserTemplate;
import  com.example.spike_player.Utility;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spike_player.R;
import com.example.spike_player.VideoPlay;
import com.example.spike_player.Templates.VideoTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.internal.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    List<VideoTemplate> dataList;
    Context context;
    public VideoAdapter(Context context, List<VideoTemplate> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_item_video,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        VideoTemplate item=dataList.get(position);

        String imageUrl =item.getImageSelectedUrl(); // Replace with your image URL
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.thumbnail);
        holder.videotitle.setText(item.getVideotitle());
        FirebaseDatabase.getInstance().getReference("All_Videos").child(item.getId()).child("likes").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                    Integer result=dataSnapshot.getValue(Integer.class);
                    holder.likes.setText(result+"");
            }
        });

        holder.views.setText(" 32");


        //channel specific details
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(dataList.get(position).getUserId()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                UserTemplate template=dataSnapshot.getValue(UserTemplate.class);
                assert template != null;
                Log.d("PROFILE",template.getUserprofile());
                Picasso.get().load(template.getUserprofile()).into(holder.channelLogo);
                holder.channelname.setText(template.getUsername());
            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Utility().setCurrentVideoId(item.getId());
                new Utility().setVideoPostedBy(item.getUserId());
                Intent intent=new Intent(context,VideoPlay.class);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (dataList==null)
        {
            return 0;
        }
        return dataList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnail , channelLogo;
        private TextView videotitle,channelname,likes,dislikes,views;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail=itemView.findViewById(R.id.imageViewThumbnail);
            channelLogo=itemView.findViewById(R.id.imageViewChannelLogo);
            videotitle=itemView.findViewById(R.id.textViewTitle);
            channelname=itemView.findViewById(R.id.textViewchannelname);
            likes=itemView.findViewById(R.id.textViewlikes);
           // dislikes=itemView.findViewById(R.id.textViewdislikes);
            views=itemView.findViewById(R.id.textViewviews);

        }
    }
}
