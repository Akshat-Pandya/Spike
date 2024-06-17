package com.example.spike_player.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spike_player.EditProfile;
import com.example.spike_player.LikedVideos;
import com.example.spike_player.PostedVideos;
import com.example.spike_player.R;
import com.example.spike_player.Templates.UserTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {


    ImageView editProfile;
    Button videosLiked,videosPosted;
    CircleImageView imageViewUserProfile;
    TextView textViewUsername,textViewUserBio,textViewContactDetails,textViewInterests,textViewSubscribers;
    SwipeRefreshLayout swipeRefreshLayout;
    UserTemplate muserTemplate;

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        fetchIds(view);

        videosLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), LikedVideos.class);
                startActivity(intent);
            }
        });

        videosPosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), PostedVideos.class);
                startActivity(intent);
            }
        });

        refresh();
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), EditProfile.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // Trigger the refreshing state
                swipeRefreshLayout.setRefreshing(true);

                // Define a Handler
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                }, 1500);
            }
        });
        return view;
    }


    private void refresh() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        UserTemplate userTemplate = dataSnapshot.getValue(UserTemplate.class);
                        muserTemplate = userTemplate;
                        updateViews();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ERROR", e.toString());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        ref=FirebaseDatabase.getInstance().getReference("Subscriptions");
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    textViewSubscribers.setText(snapshot.getChildrenCount()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateViews() {

        String susername = muserTemplate.getUsername();
        String suserbio = muserTemplate.getUserbio();
        String scontactdetails = muserTemplate.getContactdetail();
        String sinterests = muserTemplate.getInterests();
        String sprofileimageurl = muserTemplate.getUserprofile();
        // Update views

        if (susername != null)
            textViewUsername.setText(susername);
        if (suserbio != null)
            textViewUserBio.setText(suserbio);
        if (scontactdetails != null)
            textViewContactDetails.setText(scontactdetails);
        if (sinterests != null)
            textViewInterests.setText(sinterests);
        if (sprofileimageurl != null) {
            Picasso.get()
                    .load(sprofileimageurl)
                    .into(imageViewUserProfile);
        }
        swipeRefreshLayout.setRefreshing(false);
    }
    private void fetchIds(View view) {
        editProfile=view.findViewById(R.id.buttonEdit);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        textViewUsername=view.findViewById(R.id.textViewUsername);
        textViewUserBio=view.findViewById(R.id.textViewUserBio);
        textViewContactDetails=view.findViewById(R.id.textViewContactDetails);
        textViewInterests=view.findViewById(R.id.textViewInterests);
        imageViewUserProfile=view.findViewById(R.id.imageViewUserProfile);
        videosLiked=view.findViewById(R.id.videosLiked);
        videosPosted=view.findViewById(R.id.videosPosted);
        textViewSubscribers=view.findViewById(R.id.textViewSubscribersCount);
    }

}