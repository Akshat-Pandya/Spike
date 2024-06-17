package com.example.spike_player;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spike_player.Templates.CommentTemplate;
import com.example.spike_player.Templates.UserTemplate;
import com.example.spike_player.Templates.VideoTemplate;
import com.example.spike_player.adapter.CommentsAdapter;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoPlay extends AppCompatActivity {

    SimpleExoPlayer player;
    int COMMENT_COUNT = 0;
    PlayerView playerView;
    String VIDEO_ID, userProfileImage, channelName, channelLogoUrl;
    TextView textViewComments, videotitle, textviewLikes, textviewDislikes, textViewChannelName, textViewSubscribe;
    CircleImageView commentPosterLogo, channelLogo;
    ImageButton postComment;
    ImageView back, like, dislike, addToLibrary;
    EditText editTextComment;
    RecyclerView recyclerComments;
    VideoTemplate template;
    UserTemplate channelDetails, currentUserDetails;
    Boolean isLiked, currentState;
    LinearLayoutManager linearLayoutManager;
    LinearLayout videoposter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        getWindow().setStatusBarColor(Color.BLACK);


        fetchIds();

        fetchChannelDetails();

        fetchCurrentUserDetails();

        getCommentCount();


        videoposter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VideoPlay.this, ChannelPlaylist.class);
                startActivity(intent);
            }
        });


        VIDEO_ID = new Utility().getCurrentVideoId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("All_Videos");
        ref.child(VIDEO_ID).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                template = dataSnapshot.getValue(VideoTemplate.class);
                playVideo(template);
            }

            void onFailure(DatabaseError error) {
                Toast.makeText(VideoPlay.this, "Failed to fetch video template", Toast.LENGTH_SHORT).show();
            }
        });


        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextComment.getText().toString().isEmpty()) {
                    postComment(editTextComment.getText().toString());
                }
            }
        });

        textViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpComments();
            }
        });

        textViewSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSubscriptionState();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerComments.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                commentPosterLogo.setVisibility(View.VISIBLE);
                editTextComment.setVisibility(View.VISIBLE);
                postComment.setVisibility(View.VISIBLE);
            }
        });

        addToLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLibraryPreferences();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserLibrary").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                ref.child(new Utility().getCurrentVideoId()).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                     @Override
                                                                                                     public void onSuccess(Void unused) {
                                                                                                         Toast.makeText(VideoPlay.this, "Video added to library . ", Toast.LENGTH_SHORT).show();
                                                                                                     }
                                                                                                 }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VideoPlay.this, "Could not add video to the library . ", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        // Setting up the recycler view for displaying the comments made on current video
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerComments.setLayoutManager(linearLayoutManager);
        List<CommentTemplate> commentList = new ArrayList<>();
        CommentsAdapter adapter = new CommentsAdapter(commentList, getApplicationContext());
        recyclerComments.setAdapter(adapter);

        ref = FirebaseDatabase.getInstance().getReference("Comments").child(new Utility().getCurrentVideoId());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CommentTemplate comment = snapshot.getValue(CommentTemplate.class);
                commentList.add(comment);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Setting up likes / dislikes listener
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("UserLikes").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //Update initial like dislike count
        updateLikeCount(new Utility().getCurrentVideoId(), 0);
        updateDislikeCount(new Utility().getCurrentVideoId(), 0);
        // Check like/dislike status
        likesRef.child(new Utility().getCurrentVideoId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Video ID found in user's likes/dislikes
                    isLiked = snapshot.getValue(Boolean.class);
                    if (isLiked == null) {
                        like.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray))); // Change to your blue color resource
                        dislike.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray))); // Change to your blue color resource
                    } else if (isLiked)
                        like.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue))); // Change to your blue color resource

                    else
                        dislike.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red))); // Change to your blue color resource

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("ERROR", "cant check status !");
            }
        });
        // Set up click listeners for like / dislike button
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLikeButton(FirebaseAuth.getInstance().getCurrentUser().getUid(), new Utility().getCurrentVideoId());
            }
        });
        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleDislikeButton(FirebaseAuth.getInstance().getCurrentUser().getUid(), new Utility().getCurrentVideoId());
            }
        });
    }


    private void fetchSubscriptionState() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Subscriptions").child(channelDetails.getUserid());
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentState = dataSnapshot.getValue(Boolean.class);
                }

                if (Boolean.TRUE.equals(currentState)) {
                    textViewSubscribe.setText("Subscribed");
                    textViewSubscribe.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.gray));
                }

            }
        });
    }

    private void toggleSubscriptionState() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Subscriptions").child(channelDetails.getUserid());
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                currentState = dataSnapshot.getValue(Boolean.class);
                if (currentState != null) {
                    ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            textViewSubscribe.setText("Subscribe");
                            textViewSubscribe.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                        }
                    });
                } else {
                    ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            textViewSubscribe.setText("Subscribed");
                            textViewSubscribe.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.gray));
                        }
                    });
                }
            }
        });


    }

    private void fetchChannelDetails() {

        String videoPostedBy = new Utility().getVideoPostedBy();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(videoPostedBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    channelDetails = snapshot.getValue(UserTemplate.class);
                    channelLogoUrl = channelDetails.getUserprofile();
                    channelName = channelDetails.getUsername();

                    if (channelLogoUrl != null)
                        Picasso.get().load(channelLogoUrl).into(channelLogo);

                    if (channelName != null) {
                        textViewChannelName.setText(channelName);
                    }
                }
                fetchSubscriptionState();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchCurrentUserDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    currentUserDetails = dataSnapshot.getValue(UserTemplate.class);
                    if (currentUserDetails != null) {
                        Picasso.get().load(currentUserDetails.getUserprofile()).into(commentPosterLogo);
                    }
                }
            }
        });
    }

    private void toggleLibraryPreferences() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserLibrary").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.child(new Utility().getCurrentVideoId()).setValue(true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void unused) {
                                              Toast.makeText(VideoPlay.this, "Video added to library . ", Toast.LENGTH_SHORT).show();
                                          }
                                      }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VideoPlay.this, "Could not add video to the library . ", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    void settoggleLikeButton() {
        // fetch the initial value of isLiked
        like.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue))); // Change to your blue color resource
        like.setEnabled(false);
        dislike.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray))); // Change to your blue color resource
        dislike.setEnabled(true);
        Toast.makeText(this, "Updated initial preferences ", Toast.LENGTH_SHORT).show();

    }

    void settoggleDisLikeButton() {
        // fetch the initial value of isDisliked
        dislike.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red))); // Change to your blue color resource
        dislike.setEnabled(false);
        like.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray))); // Change to your blue color resource
        like.setEnabled(true);
        Toast.makeText(this, "Updated initial preferences ", Toast.LENGTH_SHORT).show();

    }

    void updateLikeCount(String videoId, int incrementValue) {

        DatabaseReference videoRef = FirebaseDatabase.getInstance().getReference("All_Videos").child(videoId);

        videoRef.child("likes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the current likes value
                Integer currentLikes = task.getResult().getValue(Integer.class);

                // If currentLikes is null, initialize it to 0
                if (currentLikes == null) {
                    currentLikes = 0;
                }

                // Add incrementValue to the current likes
                int updatedLikes = currentLikes + incrementValue;

                // Update the likes count in the database
                videoRef.child("likes").setValue(updatedLikes);
                textviewLikes.setText(updatedLikes + "");
            } else {
                Log.e("ERROR", task.getException().toString());
            }
        });
    }

    void updateDislikeCount(String videoId, int incrementValue) {

        DatabaseReference videoRef = FirebaseDatabase.getInstance().getReference("All_Videos").child(videoId);

        videoRef.child("dislikes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the current dislikes value
                Integer currentDislikes = task.getResult().getValue(Integer.class);

                // If currentDislikes is null, initialize it to 0
                if (currentDislikes == null) {
                    currentDislikes = 0;
                }

                // Add incrementValue to the current dislikes
                int updatedDislikes = currentDislikes + incrementValue;

                // Update the dislikes count in the database
                videoRef.child("dislikes").setValue(updatedDislikes);
                textviewDislikes.setText(updatedDislikes + "");
            } else {
                // Handle the error
                Exception exception = task.getException();
                Log.e("ERROR", exception.toString());
            }
        });
    }


    void toggleLikeButton(String userId, String videoId) {

    /*Handle 3 cases : Case 1 - Video is never liked or disliked means that isLiked = null -> increment like count by 1 change like button to blue and make it disabled
                       Case 2 - Video is already liked means that button is already blue or like button is disabled but still like button is tapped -> decrement like count by 1 change like button to gray and make it enabled
                       Case 3 - Video is disliked means that disliked button is red or dislike button is disabled  but still the like button is tapped -> increment like count by 1 and decrement dislike count by 1    change like button to blue dislike button to gray and make like button disabled
    */
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserLiked").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        // Case 1
        if (isLiked == null) {
            isLiked = true;
            updateLikeCount(videoId, 1);
            like.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue))); // Change to your blue color resource
            //  like.setEnabled(false);
        }
        //Case 2
        else if (isLiked) {
            isLiked = null;
            updateLikeCount(videoId, -1);
            like.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray))); // Change to your blue color resource
            // like.setEnabled(true);
        }
        //Case 3
        else if (!isLiked) {
            isLiked = true;
            updateLikeCount(videoId, 1);
            updateDislikeCount(videoId, -1);
            like.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue))); // Change to your blue color resource
            dislike.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray))); // Change to your blue color resource
            // like.setEnabled(false);
        }
        updateUserPreference(userId, videoId, isLiked);
    }

    void toggleDislikeButton(String userId, String videoId) {
           /*Handle 3 cases : Case 1 - Video is never liked or disliked means that isLiked = null -> increment dislike count by 1 change dislike button to red and make it disabled
                       Case 2 - Video is already disliked means that button is already red or dislike button is disabled but still it is tapped -> decrement dislike count by 1 change dislike button to gray and make it enabled
                       Case 3 - Video is liked means that like button is blue or like button is disabled  but still the dislike button is tapped -> increment dislike count by 1 and decrement like count by 1    change dislike button to red like button to gray and make dislike button disabled
           */
        //Case 1
        if (isLiked == null) {
            isLiked = false;
            updateDislikeCount(videoId, 1);
            dislike.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red))); // Change to your blue color resource
        }
        //Case 2
        else if (!isLiked) {
            isLiked = null;
            updateDislikeCount(videoId, -1);
            dislike.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray))); // Change to your blue color resource
        }
        //Case 3
        else if (isLiked) {
            isLiked = false;
            updateLikeCount(videoId, -1);
            updateDislikeCount(videoId, 1);
            dislike.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red))); // Change to your blue color resource
            like.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray))); // Change to your blue color resource
            // dislike.setEnabled(false);
        }
        updateUserPreference(userId, videoId, isLiked);

    }

    void updateUserPreference(String userId, String videoId, Boolean value) {
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("UserLikes").child(userId);

        likesRef.child(videoId).setValue(value);
    }


    private void playVideo(VideoTemplate template) {

        videotitle.setText(template.getVideotitle());
        String VIDEO_URL = template.getVideoSelectedUrl();
        // Initialize ExoPlayer - make an object of class SiimpleExoPlayer.Builder inside constructor pass current context and select .build()
        player = new SimpleExoPlayer.Builder(this).build();

        // Initialize PlayerView
        playerView = findViewById(R.id.playerView);

        // Set the player with the PlayerView
        playerView.setPlayer(player);

        // Prepare the MediaSource : Determine the type of media file that needs to be played
        MediaSource mediaSource = buildMediaSource(Uri.parse(VIDEO_URL));

        // set up the media source inside the player view
        player.setMediaSource(mediaSource);

        // Prepare the player to play when ready
        player.prepare();
    }

    private MediaSource buildMediaSource(Uri uri) {

        // Produces DataSource instances through which media data is loaded
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, getResources().getString(R.string.app_name));

        // This is the MediaSource representing the media to be played
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));

    }

    private void getCommentCount() {

        // Fetching the current user's or the current comment poster's logo
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.child("userprofile").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                userProfileImage = dataSnapshot.getValue(String.class);
            }
        });
        // loading the image into the users profile image in the comment section
        if (userProfileImage != null)
            Picasso.get().load(userProfileImage).into(commentPosterLogo);

        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comments").child(new Utility().getCurrentVideoId());
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    COMMENT_COUNT = (int) dataSnapshot.getChildrenCount();
                    textViewComments.setText(COMMENT_COUNT + " Comments ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textViewComments.setText(COMMENT_COUNT + " Comments ");
            }
        });

    }

    private void setUpComments() {
        commentPosterLogo.setVisibility(View.GONE);
        editTextComment.setVisibility(View.GONE);
        postComment.setVisibility(View.GONE);

        recyclerComments.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);


    }

    private void postComment(String comment) {

        editTextComment.getText().clear();
        String videoId = new Utility().getCurrentVideoId();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments").child(videoId);
        String commentId = ref.push().getKey();

        String timestampFormat = "yyyyMMdd_HHmmss";
        String timestamp = new SimpleDateFormat(timestampFormat).format(new Date());

        CommentTemplate commentTemplate = new CommentTemplate(commentId, comment, timestamp);
        if (currentUserDetails.getUsername() != null)
            commentTemplate.setPostedByName(currentUserDetails.getUsername());
        if (currentUserDetails.getUserprofile() != null)
            commentTemplate.setPostedByProfileImage(currentUserDetails.getUserprofile());

        ref.child(commentId).setValue(commentTemplate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(VideoPlay.this, "Comment Posted", Toast.LENGTH_SHORT).show();
                getCommentCount();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Handle the back button click
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchIds() {
        textViewComments = findViewById(R.id.textViewComments);
        commentPosterLogo = findViewById(R.id.circularImageViewComments);
        channelLogo = findViewById(R.id.circularImageViewChannelIcon);
        postComment = findViewById(R.id.postComment);
        editTextComment = findViewById(R.id.editTextAddComment);
        textviewLikes = findViewById(R.id.textviewLikes);
        textviewDislikes = findViewById(R.id.textviewDislikes);
        textViewSubscribe = findViewById(R.id.subscribeTextView);
        back = findViewById(R.id.back);
        like = findViewById(R.id.like);
        dislike = findViewById(R.id.dislike);
        addToLibrary = findViewById(R.id.addToLibrary);
        recyclerComments = findViewById(R.id.recyclerComments);
        videotitle = findViewById(R.id.videotitle);
        textViewChannelName = findViewById(R.id.textViewchannelname);
        videoposter = findViewById(R.id.videoposter);
    }
}

