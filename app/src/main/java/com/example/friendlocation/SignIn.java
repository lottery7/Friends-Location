package com.example.friendlocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.friendlocation.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    private ActivitySignInBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setContentView(R.layout.activity_main_map);

        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.userEt.getText().toString().isEmpty() || binding.passwordEt.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Empty fields are not allowed", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.userEt.getText().toString(), binding.passwordEt.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(SignIn.this, MainMap.class));
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        binding.goToSingUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });


    }

}