package com.example.spike_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.spike_player.Templates.VideoTemplate;
import com.example.spike_player.adapter.VideoAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LikedVideos extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView backbutton;
    private  VideoAdapter adapter;
    private List<VideoTemplate> likedVideoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_videos);
        recyclerView=findViewById(R.id.recycler);
        backbutton=findViewById(R.id.backicon);
        changeStatusBarColor();

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        likedVideoList=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new VideoAdapter(this,likedVideoList);
        recyclerView.setAdapter(adapter);

        // Fetch liked video IDs for the current user
        fetchLikedVideos();
    }

    private void fetchLikedVideos() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the UserLikes node for the current user
        DatabaseReference userLikesRef = FirebaseDatabase.getInstance().getReference("UserLikes").child(userId);

        // Retrieve liked video IDs we could have even used the get () method but still ... This single event listener reads data only once then is detached
        userLikesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot videoSnapshot : dataSnapshot.getChildren()) {
                        String videoId = videoSnapshot.getKey();
                        Boolean result=videoSnapshot.getValue(Boolean.class);

                        if(Boolean.TRUE.equals(result)) {
                            // Query video details from the All_Videos node
                            queryVideoDetails(videoId);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ERROR",databaseError.toString());
            }
        });
    }

    private void queryVideoDetails(String videoId) {

        // Reference to the All_Videos node for the specific video
        DatabaseReference videoRef = FirebaseDatabase.getInstance().getReference("All_Videos").child(videoId);

        // Retrieve video details we could have also used get method but still ... single value event listener listens only once and then is detached
        videoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    VideoTemplate video = dataSnapshot.getValue(VideoTemplate.class);

                    if (video != null) {
                        // Add the liked video to the list
                        likedVideoList.add(video);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ERROR",databaseError.toString());
            }
        });


    }
    void changeStatusBarColor(){
        // In your Activity's onCreate method
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }


    }
}