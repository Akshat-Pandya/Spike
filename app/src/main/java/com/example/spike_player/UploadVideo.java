package com.example.spike_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.spike_player.Templates.VideoTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadVideo extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    private EditText editTextVideoTitle,editTextVideoDuration;
    private RadioGroup radioGroup;
    private Button btnSelectVideo,btnSelectImage,btnUpload;
    private ProgressBar progressBar;
    private Uri videoSelectedUrl,imageSelectedUrl;
    private String videoUploadedUrl,imageUploadedUrl;
    private ImageView back;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);
        changeStatusBarColor();
        // Initialize components using the IDs from VideoUploadIds class
        editTextVideoTitle = findViewById(R.id.editTextVideoTitle);
        editTextVideoDuration = findViewById(R.id.editTextVideoDuration);
        radioGroup = findViewById(R.id.radioGroupVideoType);
        btnSelectVideo = findViewById(R.id.btnSelectVideo);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUpload = findViewById(R.id.btnUpload);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        back=findViewById(R.id.backicon);
        myToolbar=findViewById(R.id.mytoolbar);

        setSupportActionBar(myToolbar);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        btnSelectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectVideo();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                // Disable the button
                btnUpload.setEnabled(false);
                upload();
            }
        });
        
    }

    private void changeStatusBarColor() {
        // Use the following code to change the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }
    }

    private void selectVideo() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_VIDEO_REQUEST);
    }

    private void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    private void upload() {
        String videocategory=null;
        String videotitle=editTextVideoTitle.getText().toString();
        String videoduration=editTextVideoDuration.getText().toString();
        RadioButton btncategory=findViewById(radioGroup.getCheckedRadioButtonId());
        if(btncategory!=null){
        videocategory=btncategory.getText().toString();
        }





        if(videotitle!=null && videoSelectedUrl!=null && imageSelectedUrl!=null && videocategory!=null)
        {
            Log.d("TAG",videoSelectedUrl.toString());
            Log.d("TAG",imageSelectedUrl.toString());
            progressBar.setVisibility(View.VISIBLE);
            uploadToStorage();
        }
        else
        {
            Toast.makeText(this, "Please fill all details ", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            btnUpload.setEnabled(true);
            return;
        }

    }

    private void uploadToStorage() {
        if (videoSelectedUrl != null && imageSelectedUrl != null) {
            // Get a reference to the Firebase Storage location
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            // Create references for video and thumbnail files
            StorageReference videoRef = storageRef.child("Media/" + FirebaseAuth.getInstance().getUid() + "/Videos/" + System.currentTimeMillis() + ".mp4");
            StorageReference thumbnailRef = storageRef.child("Media/" + FirebaseAuth.getInstance().getUid() + "/Thumbnails/" + System.currentTimeMillis() + ".jpg");


            // Upload video - > Upload Image
            UploadTask videoUploadTask = videoRef.putFile(videoSelectedUrl);
            videoUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> task1=taskSnapshot.getStorage().getDownloadUrl();
                    task1.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            videoUploadedUrl = uri.toString();
                            Log.d("VIDEOURL",videoUploadedUrl);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadVideo.this, "Couldnt obtain video url", Toast.LENGTH_SHORT).show();
                        }
                    });

                    UploadTask thumbnailUploadTask = thumbnailRef.putFile(imageSelectedUrl);
                    thumbnailUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> task2=taskSnapshot.getStorage().getDownloadUrl();
                            task2.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUploadedUrl = uri.toString();
                                    Log.d("IMAGEURL",imageUploadedUrl);
                                    updateDatabase();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadVideo.this, "Couldnt obtain image url", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });


                }
            });
        }
        else
        {
            // Handle case where either video or thumbnail URI is null
            Toast.makeText(this, "Please select both video and thumbnail.", Toast.LENGTH_SHORT).show();
            btnUpload.setEnabled(true);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void updateDatabase() {
        String videotitle=editTextVideoTitle.getText().toString();
        String videoduration=editTextVideoDuration.getText().toString();
        RadioButton btncategory=findViewById(radioGroup.getCheckedRadioButtonId());
        String videocategory=btncategory.getText().toString();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("User_Videos").child(FirebaseAuth.getInstance().getUid());
        // Create a new child node with a unique key using push()
        DatabaseReference newVideoRef = ref.push();
        // Get the generated key as a string
        String Id = newVideoRef.getKey();
        String timestamp = String.valueOf(System.currentTimeMillis());
        VideoTemplate newVideo=new VideoTemplate(videotitle,videocategory,videoduration,videoUploadedUrl,imageUploadedUrl,Id, FirebaseAuth.getInstance().getCurrentUser().getUid(),timestamp);
        updateUserVideoNode(newVideo,Id);
    }

    private void updateUserVideoNode(VideoTemplate newVideo,String Id) {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("User_Videos").child(FirebaseAuth.getInstance().getUid());
        ref.child(Id).setValue(newVideo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    updateAllVideosNode(newVideo,Id);
                }
                else{
                    Toast.makeText(UploadVideo.this, "Failed To Update Database", Toast.LENGTH_SHORT).show();
                    btnUpload.setEnabled(true);
                }
            }
        });


    }
    private void updateAllVideosNode(VideoTemplate newVideo, String currentTimeString) {

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("All_Videos");
        ref.child(currentTimeString).setValue(newVideo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(UploadVideo.this, "Updated Database", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    onBackPressed();

                }
                else{
                    Toast.makeText(UploadVideo.this, "Failed To Update Database", Toast.LENGTH_SHORT).show();
                    btnUpload.setEnabled(true);
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                // Get the selected image URI
                imageSelectedUrl = data.getData();

                // Now you can use the selectedImageUri to obtain the image's URL or perform other actions
                btnSelectImage.setBackgroundColor(getResources().getColor(R.color.green));
                btnSelectImage.setText("SELECTED");
            }
            else if(requestCode==PICK_VIDEO_REQUEST)
            {
                // Get the selected video URI
                videoSelectedUrl = data.getData();

                // Now you can use the selectedVideoUri to obtain the video's URL or perform other actions

                btnSelectVideo.setBackgroundColor(getResources().getColor(R.color.green));
                btnSelectVideo.setText("SELECTED");
            }
        }
        else{
            Toast.makeText(this, "Item not selected", Toast.LENGTH_SHORT).show();
        }
    }
}