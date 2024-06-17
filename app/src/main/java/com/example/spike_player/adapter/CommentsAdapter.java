package com.example.spike_player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spike_player.R;
import com.example.spike_player.Templates.CommentTemplate;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    List<CommentTemplate> commentList;
    Context context;

    public CommentsAdapter(List<CommentTemplate> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_comments, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CommentTemplate commentTemplate = commentList.get(position);
        holder.commentText.setText(commentTemplate.getComment());
        holder.timestamp.setText(commentTemplate.getTimestamp());

        if (commentTemplate.getPostedByName() != null) {
            holder.username.setText(commentTemplate.getPostedByName());
        }

        if (commentTemplate.getPostedByProfileImage() != null) {
            Picasso.get().load(commentTemplate.getPostedByProfileImage()).into(holder.profileImage);
        }

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView commentText, username, timestamp;
        CircleImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.textViewCommentText);
            username = itemView.findViewById(R.id.textViewUserName);
            timestamp = itemView.findViewById(R.id.textViewTimestamp);
            profileImage = itemView.findViewById(R.id.imageViewUserProfile);
        }
    }
}
