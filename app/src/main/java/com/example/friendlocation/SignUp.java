package com.example.friendlocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.friendlocation.databinding.ActivitySignInBinding;
import com.example.friendlocation.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private DatabaseReference database;
    private final String userKey = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance().getReference();

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        binding.goToSignInImgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignIn();
            }
        });
    }

    private void signUpUser() {
        String email = binding.emailEt.getText().toString();
        String username = binding.usernameEt.getText().toString();
        String password = binding.passwordEt.getText().toString();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Empty fields are not allowed", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                            User user = new User(id, username, email, password);
                            database.child(userKey).child(id).setValue(user);
                            goToMainMap();
                        } else {
                            Toast.makeText(getApplicationContext(), "There was an error during registration", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void goToSignIn() {
        startActivity(new Intent(SignUp.this, SignIn.class));
    }

    private void goToMainMap() {
        startActivity(new Intent(SignUp.this, MainMap.class));
    }
}
