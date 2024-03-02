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
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

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
                if (binding.nicknameEt.getText().toString().isEmpty() || binding.mailEt.getText().toString().isEmpty() || binding.passwordEt.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Empty fields are not allowed", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.mailEt.getText().toString(), binding.passwordEt.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        HashMap<String,String> userInfo = new HashMap<>();
                                        userInfo.put("email", binding.mailEt.getText().toString());
                                        userInfo.put("username", binding.nicknameEt.getText().toString());
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(userInfo);
                                        startActivity(new Intent(SignUp.this, MainMap.class));
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "There was an error during registration", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });

        binding.goToSignInImgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, SignIn.class));
            }
        });

    }

}