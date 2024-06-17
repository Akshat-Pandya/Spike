package com.example.spike_player.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spike_player.R;
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

public class LibraryFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private VideoAdapter adapter;
    private List<VideoTemplate> libraryVideos;
    public LibraryFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_library, container, false);
        recyclerView=view.findViewById(R.id.recycler);
        libraryVideos=new ArrayList<>();

        layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new VideoAdapter(getContext(),libraryVideos);
        recyclerView.setAdapter(adapter);

        // fetch the library elements
        fetchVideos();

        // Inflate the layout for this fragment
        return view;
    }

    private void fetchVideos() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("UserLibrary");
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot temp : snapshot.getChildren()) {
                        if (temp.exists()) {
                            String key = temp.getKey();
                            boolean result = temp.getValue(Boolean.class);
                            if (result)
                                queryVideoDetails(key);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void queryVideoDetails(String key){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("All_Videos");
        ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    VideoTemplate video=snapshot.getValue(VideoTemplate.class);
                    libraryVideos.add(video);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR",error.toString());
            }
        });
    }
}