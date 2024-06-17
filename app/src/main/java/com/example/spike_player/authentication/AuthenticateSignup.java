package com.example.spike_player.authentication;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spike_player.R;
import com.example.spike_player.Templates.UserTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthenticateSignup extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextRePassword;
    private Button btnSignUp;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate_signup);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRePassword = findViewById(R.id.editTextRePassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        getWindow().setStatusBarColor(Color.BLACK);

        firebaseAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    private void signUp() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String rePassword = editTextRePassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(rePassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user with email and password
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(AuthenticateSignup.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                            createNewNode(user);
                            onBackPressed();
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(AuthenticateSignup.this, "Sign up failed. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createNewNode(FirebaseUser user) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        UserTemplate newuser=new UserTemplate(user.getEmail(),user.getUid());
        ref.child(user.getUid()).setValue(newuser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(AuthenticateSignup.this, "Added to database", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(AuthenticateSignup.this, "Could not add to database", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
