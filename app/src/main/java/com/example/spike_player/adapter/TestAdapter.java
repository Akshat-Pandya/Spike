package com.example.spike_player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spike_player.R;
import com.example.spike_player.Templates.VideoTemplate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

    private ArrayList<VideoTemplate> dataList;
    private Context context;

    public TestAdapter(Context context) {
        this.context = context;
        this.dataList=new ArrayList<>();
    }

    public void addAll(ArrayList<VideoTemplate> newVideos)
    {
        int initSize= dataList.size();
        dataList.addAll(newVideos);
        notifyItemRangeInserted(initSize, newVideos.size());
    }
    public String getLastItemId(){
        return dataList.get(dataList.size()-1).getId();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_item_video,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl=dataList.get(position).getImageSelectedUrl();
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.thumbnail);
        holder.videotitle.setText(dataList.get(position).getVideotitle());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView videotitle;
        ImageView thumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videotitle=itemView.findViewById(R.id.textViewTitle);
            thumbnail=itemView.findViewById(R.id.imageViewThumbnail);
        }
    }
}
