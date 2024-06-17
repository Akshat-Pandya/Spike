package com.example.spike_player.fragments;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.example.spike_player.R;
import com.example.spike_player.Templates.SpikeTemplate;
import com.example.spike_player.Templates.VideoTemplate;
import com.example.spike_player.adapter.TestAdapter;
import com.example.spike_player.adapter.VideoAdapter;
import com.example.spike_player.adapter.spikeAdapter;
import com.example.spike_player.sampleSpike;
import com.example.spike_player.videoAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TrendingFragment extends Fragment {


    LottieAnimationView lottie;
    public TrendingFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        lottie=view.findViewById(R.id.takemeimage);

        lottie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),sampleSpike.class);
                startActivity(intent);
            }
        });

        return view;

    }
}