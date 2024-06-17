package com.example.spike_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.spike_player.Templates.VideoTemplate;
import com.example.spike_player.adapter.VideoAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostedVideos extends AppCompatActivity {

    ImageView back;
    RecyclerView recyclerView;
    VideoAdapter adapter;
    List<VideoTemplate> videosPostedList;
    ArrayList<String> keyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posted_videos);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));

        back=findViewById(R.id.backicon);
        recyclerView=findViewById(R.id.recycler);
        videosPostedList=new ArrayList<>();
        keyList=new ArrayList<>();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Setting up the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new VideoAdapter(this,videosPostedList);
        recyclerView.setAdapter(adapter);

        //fetching the required video ids
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("User_Videos");
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        keyList.add(dataSnapshot.getKey());
                    }
                    fetchVideos();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostedVideos.this, "Please check out your network ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchVideos() {

        for (String videoId : keyList) {
            DatabaseReference videoRef = FirebaseDatabase.getInstance().getReference("All_Videos").child(videoId);

            videoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        VideoTemplate videoTemplate = dataSnapshot.getValue(VideoTemplate.class);
                        if (videoTemplate != null) {
                            videosPostedList.add(videoTemplate);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                    Toast.makeText(PostedVideos.this, "Please check out your network ", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}