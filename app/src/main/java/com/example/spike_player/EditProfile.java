package com.example.spike_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.spike_player.Templates.UserTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView back;
    private Button saveChanges;
    private CircleImageView userprofile;
    private EditText username, userbio, contactDetails, interests;
    private String IMAGE_URL, musername, muserbio, mcontactdetails, minterests;
    private Uri IMAGE_URI;
    private UserTemplate muserTemplate;
    private ProgressBar progressBar;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        back = findViewById(R.id.backicon);
        saveChanges = findViewById(R.id.saveChanges);
        username = findViewById(R.id.edittextViewUsername);
        userbio = findViewById(R.id.edittextViewUserBio);
        contactDetails = findViewById(R.id.edittextViewContactDetails);
        interests = findViewById(R.id.edittextViewInterests);
        userprofile = findViewById(R.id.imageViewUserProfile);
        progressBar=findViewById(R.id.progressBar);

        // refresh the edit profile layout with previously stored data (if any)
        refresh();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        userprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageFromGallery();
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musername = username.getText().toString();
                muserbio = userbio.getText().toString();
                mcontactdetails = contactDetails.getText().toString();
                minterests = interests.getText().toString();

                if (musername.isEmpty() || muserbio.isEmpty() || mcontactdetails.isEmpty() || minterests.isEmpty() || !isImageSelected()) {
                    Toast.makeText(EditProfile.this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(IMAGE_URL==null) {
                    progressBar.setVisibility(View.VISIBLE);
                    uploadImageToFirebaseStorage(IMAGE_URI);
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    uploadChanges();
                }
            }
        });


    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        // Get the Firebase Storage reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Get the user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Define the storage path
        StorageReference userProfileRef = storageRef.child("Users/" + userId + "/Profile/" + "profile_image.jpg");

        // Upload the image
        userProfileRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully
                        Toast.makeText(EditProfile.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                        // Get the download URL of the uploaded image
                        Task<Uri> task1 = taskSnapshot.getStorage().getDownloadUrl();
                        task1.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        IMAGE_URL = uri.toString();
                                        Log.d("PROFILEURL", IMAGE_URL);
                                        uploadChanges();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditProfile.this, "Couldnt obtain profile image url", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Image upload failed
                        Toast.makeText(EditProfile.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        return;

                    }
                });
    }


    private void uploadChanges() {

        if (IMAGE_URL != null) {
            UserTemplate newUserTemplate = new UserTemplate(muserTemplate.getEmail(), muserTemplate.getUserid(), musername
                    , muserbio, mcontactdetails, minterests, IMAGE_URL);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");


            ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(newUserTemplate)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            back.callOnClick();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(EditProfile.this, "Could not update profile", Toast.LENGTH_SHORT).show();
                            Log.e("ERROR", e.toString());
                        }
                    });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Failed to update changes ! ", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isImageSelected() {
        return (IMAGE_URL != null || IMAGE_URI!=null);
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
            username.setText(susername);
        if (suserbio != null)
            userbio.setText(suserbio);
        if (scontactdetails != null)
            contactDetails.setText(scontactdetails);
        if (sinterests != null)
            interests.setText(sinterests);
        if (sprofileimageurl != null) {
            IMAGE_URL=sprofileimageurl;
            Picasso.get()
                    .load(sprofileimageurl)
                    .into(userprofile);
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                // Get the selected image URI
                IMAGE_URI = data.getData();

                // Set the image URI to your ImageView
                userprofile.setImageURI(IMAGE_URI);
            }
        }
    }

}
