package com.example.friendlocation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.friendlocation.databinding.ActivityRestorePasswordBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class RestorePassword extends AppCompatActivity {
    private ActivityRestorePasswordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestorePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        mAuth = FirebaseAuth.getInstance();

        binding.restorePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailEt.getText().toString();
                if (email.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Email field shouldn't be empty ", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    restorePassword(email);
                }
            }
        });

        binding.goToSignInImgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignIn();
            }
        });

    }

    private void restorePassword(String email) {

        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "A recovery email has been sent to you", Toast.LENGTH_SHORT).show();
                goToSignIn();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "The email is incorrect", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void goToSignIn() {
        startActivity(new Intent(RestorePassword.this, SignIn.class));
    }
}
