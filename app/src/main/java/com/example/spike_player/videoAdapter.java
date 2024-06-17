package com.example.spike_player;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spike_player.Templates.VideoTemplate;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class videoAdapter extends FirebaseRecyclerAdapter<VideoTemplate,videoAdapter.myViewHolder> {

    public videoAdapter(@NonNull FirebaseRecyclerOptions<VideoTemplate> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull VideoTemplate model) {
            holder.setData(model);
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_view,parent,false));
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        VideoView videoView;
        TextView title,desc;
        ProgressBar progressBar;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView=itemView.findViewById(R.id.videoView);
            title=itemView.findViewById(R.id.textTitle);
            desc=itemView.findViewById(R.id.textDescription);
            progressBar=itemView.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

        }
        void setData(VideoTemplate obj){
           videoView.setVideoPath(obj.getVideoSelectedUrl());
           title.setText(obj.getVideotitle());

           videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
               @Override
               public void onPrepared(MediaPlayer mediaPlayer) {
                   progressBar.setVisibility(View.GONE);
                   mediaPlayer.start();
               }
           });

           videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
               @Override
               public void onCompletion(MediaPlayer mediaPlayer) {
                   mediaPlayer.start();
               }
           });

        }



    }

}
