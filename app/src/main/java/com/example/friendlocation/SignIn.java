package com.example.friendlocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.friendlocation.utils.FirebaseUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.example.friendlocation.databinding.ActivitySignInBinding;


public class SignIn extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private GoogleSignInClient googleSignInClient;
    private String TAG = "SignInTag";
    private FirebaseAuth mAuth;
    private int RESULT_CODE_SINGIN = 666;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToMainMap();
        }

        GoogleSignInOptions gso = new
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);

        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });

        binding.googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(singInIntent,RESULT_CODE_SINGIN);
            }
        });

        binding.goToSingUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUp();
            }
        });

        binding.forgotPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRestorePassword();
            }
        });

    }

    private void signInUser() {
        String email = binding.userEt.getText().toString();
        String password = binding.passwordEt.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Empty fields are not allowed", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            goToMainMap();
                        } else {
                            Exception e = task.getException();
                            Log.w(TAG, "signInResult:failed " + e.toString());
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // ------------------Google----------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CODE_SINGIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount user = task.getResult(ApiException.class);
            FirebaseGoogleAuth(user);
        } catch (ApiException e) {
            e.printStackTrace();
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount user) {
        AuthCredential credential = GoogleAuthProvider.getCredential(user.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.w(TAG, "Google auntification succeeded");
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    saveGoogleUserInDatabase(firebaseUser);
                    goToMainMap();
                }
                else {
                    Exception e = task.getException();
                    Log.w(TAG, "signInResult:failed " + e.toString());
                }
            }
        });
    }

    private void saveGoogleUserInDatabase(FirebaseUser firebaseUser) {
        String username = firebaseUser.getDisplayName();
        String email = firebaseUser.getEmail();
        User user = new User(username, email);
        FirebaseUtils.getCurrentUserDetails().setValue(user);
        Log.w(TAG, "User was added to database");
    }
    // ------------------Google----------------------

    private void goToSignUp() {
        startActivity(new Intent(SignIn.this, SignUp.class));
    }

    private void goToMainMap() {
        startActivity(new Intent(SignIn.this, MainMap.class));
    }

    private void goToRestorePassword() {
        startActivity(new Intent(this, RestorePassword.class));
    }
}