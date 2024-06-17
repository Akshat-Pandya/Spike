package com.example.spike_player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.example.spike_player.Templates.VideoTemplate;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class sampleSpike extends AppCompatActivity {

    ViewPager2 viewPager2;
    videoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_spike);
        getWindow().setStatusBarColor(Color.BLACK);

        viewPager2=findViewById(R.id.viewPager);


        // Assuming "All_Videos" is the node containing your videos in the database
        DatabaseReference videosRef = FirebaseDatabase.getInstance().getReference("All_Videos");

// Create a query to fetch data in reverse order
        Query reverseQuery = videosRef.orderByChild("timestamp").limitToLast(10);
// Now you can use this query to fetch data using FirebaseRecyclerOptions


        Query query = FirebaseDatabase.getInstance().getReference("All_Videos").orderByKey();
        FirebaseRecyclerOptions<VideoTemplate> options =
                new FirebaseRecyclerOptions.Builder<VideoTemplate>()
                        .setQuery(reverseQuery, VideoTemplate.class)
                        .build();

        adapter=new videoAdapter(options);
        viewPager2.setAdapter(adapter);


    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}