package com.example.friendlocation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.friendlocation.databinding.ActivitySignUpBinding;
import com.example.friendlocation.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                            User user = new User(username, email);
                            FirebaseUtils.getCurrentUserDetails().setValue(user);
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
