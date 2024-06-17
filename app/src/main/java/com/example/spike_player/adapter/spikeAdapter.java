package com.example.spike_player.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.spike_player.R;
import com.example.spike_player.Templates.VideoTemplate;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class spikeAdapter extends FirebaseRecyclerAdapter<VideoTemplate, spikeAdapter.VideoViewHolder> {

    public spikeAdapter(@NonNull FirebaseRecyclerOptions<VideoTemplate> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull VideoViewHolder holder, int position, @NonNull VideoTemplate model) {
        Picasso.get().load(model.getImageSelectedUrl()).into(holder.thumbnailImageView);
        holder.titleTextView.setText(model.getVideotitle());
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spike_layout, parent, false);
        return new VideoViewHolder(view);
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        TextView titleTextView;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.imageViewThumbnail);
            titleTextView = itemView.findViewById(R.id.videotitle);
        }
    }
}

