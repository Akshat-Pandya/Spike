package com.example.spike_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spike_player.Templates.UserTemplate;
import com.example.spike_player.Templates.VideoTemplate;
import com.example.spike_player.adapter.VideoAdapter;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.internal.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChannelPlaylist extends AppCompatActivity {

    private ImageButton videos, spikes;
    private TextView textViewchannelName, textViewsubscribers;
    private CircleImageView imageChannelLogo;
    private String channelName, channelLogo, subscribers;
    private ImageButton editCarausel;
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private List<VideoTemplate> dataList;
    private ArrayList<String> keyList;
    private String uploaderId = null;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_channel_playlist);
        findViews();

        uploaderId = Utility.getVideoPostedBy();

        fetchChannelDetails();

        fetchSubscribers();

        dataList = new ArrayList<>();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        spikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChannelPlaylist.this, "To be implemented - ", Toast.LENGTH_SHORT).show();

            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new VideoAdapter(this, dataList);
        recyclerView.setAdapter(adapter);

        fetchVideoKeys();

    }

    private void fetchSubscribers() {
        FirebaseDatabase.getInstance().getReference("Subscriptions").child(uploaderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    textViewsubscribers.setText("Subscribers : " + snapshot.getChildrenCount() + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchChannelDetails() {

        FirebaseDatabase.getInstance().getReference("Users").child(uploaderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserTemplate userTemplate = snapshot.getValue(UserTemplate.class);
                    channelName = userTemplate.getUsername();
                    channelLogo = userTemplate.getUserprofile();
                    if (channelName != null) {
                        textViewchannelName.setText(channelName);
                    }
                    if (channelLogo != null) {
                        Picasso.get().load(channelLogo).into(imageChannelLogo);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void findViews() {
        videos = findViewById(R.id.channelVideos);
        spikes = findViewById(R.id.channelSpikes);
        recyclerView = findViewById(R.id.recyclerVideos);
        backButton = findViewById(R.id.backButton);
        imageChannelLogo = findViewById(R.id.channelLogo);
        textViewchannelName = findViewById(R.id.channelName);
        textViewsubscribers = findViewById(R.id.subscribers);
    }

    private void fetchVideoKeys() {
        //uploaderId=Utility.getVideoPostedBy();

        keyList = new ArrayList<>();

        // Fetching the video id of all the videos uploaded by the channel
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User_Videos");
        ref.child(uploaderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        keyList.add(child.getKey());
                    }
                    fetchVideoTemplates();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChannelPlaylist.this, "Network error", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void fetchVideoTemplates() {
        // Fetching the video templates corresponding to the keyList keys

        DatabaseReference allVideosRef = FirebaseDatabase.getInstance().getReference("All_Videos");

        for (String videoId : keyList) {
            DatabaseReference videoRef = allVideosRef.child(videoId);

            videoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Retrieve the video template data
                        VideoTemplate videoTemplate = snapshot.getValue(VideoTemplate.class);

                        if (videoTemplate != null) {
                            dataList.add(videoTemplate);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle any errors that occur during the data retrieval process
                    Toast.makeText(ChannelPlaylist.this, "Network error", Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseError", "Error fetching video template: " + error.getMessage());
                }
            });
        }
    }
}