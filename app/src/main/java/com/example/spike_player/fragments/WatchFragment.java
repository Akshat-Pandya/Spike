package com.example.spike_player.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.spike_player.MainActivity;
import com.example.spike_player.R;
import com.example.spike_player.Templates.VideoTemplate;
import com.example.spike_player.UploadVideo;
import com.example.spike_player.adapter.VideoAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class WatchFragment extends Fragment {

    private RecyclerView recycler;
    private VideoAdapter adapter;
    private ImageView upload;
    private ProgressBar progressBar;
    private int currentPage = 1;
    private int batchSize = 4;
    private boolean isLoading = false;
    private boolean hasMoreVideos = true;
    public WatchFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_watch, container, false);
        recycler=view.findViewById(R.id.recycler);
        upload=view.findViewById(R.id.upload);
        progressBar=view.findViewById(R.id.progressBar);

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Hide progress bar after 2000ms (2 seconds)
       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               progressBar.setVisibility(View.GONE);
           }
       },5000);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), UploadVideo.class);
                startActivity(intent);
            }
        });

        //  List to store the retrieved VideoTemplate objects
        List<VideoTemplate> dataList = new ArrayList<>();
        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        recycler.setLayoutManager(manager);
        adapter=new VideoAdapter(getContext(),dataList);
        recycler.setAdapter(adapter);


        DatabaseReference allVideosRef = FirebaseDatabase.getInstance().getReference().child("All_Videos");
        allVideosRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // This method is triggered when a new child is added
                // Retrieve data for each video
                VideoTemplate videoTemplate = dataSnapshot.getValue(VideoTemplate.class);
                // Add the retrieved VideoTemplate to the list
                if (videoTemplate != null) {
                    dataList.add(videoTemplate);
                    adapter.notifyDataSetChanged();
                }
                // Now, dataList contains the latest VideoTemplate objects from All_Videos node
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // This method is triggered when the data of a child is updated
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // This method is triggered when a child is removed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // This method is triggered when a child changes its position in the list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // This method will be triggered in case of an error
                Log.e("DATABASE ERROR",databaseError.toString());
            }
        });



        return view;
    }
}