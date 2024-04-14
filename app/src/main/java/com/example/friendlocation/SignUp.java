package com.example.friendlocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.friendlocation.databinding.ActivitySignUpBinding;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.User;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private GoogleSignInClient googleSignInClient;
    private String TAG = "SignUpTag";
    private FirebaseAuth mAuth;
    private int RESULT_CODE_SINGIN = 666;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        binding.googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(singInIntent,RESULT_CODE_SINGIN);
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
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthWeakPasswordException) {
                                Toast.makeText(getApplicationContext(), "Password is too weak, use at least 8 symbols", Toast.LENGTH_SHORT).show();
                            } else if (exception instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "Email is already in use by another account", Toast.LENGTH_SHORT).show();
                            } else {
                                Exception e = task.getException();
                                Log.w(TAG, "signUpResult:failed " + e.toString());
                                Toast.makeText(getApplicationContext(), "An error occurred during registration", Toast.LENGTH_SHORT).show();
                            }
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
//            Toast.makeText(SignIn.this,"Signed In successfully",Toast.LENGTH_LONG).show();
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
//                  I check it dose not save user data one more time)
                    saveGoogleUserInDatabase(firebaseUser);
                    goToMainMap();
                }
                else {
                    Log.w(TAG, "Google auntification failed");
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



    private void goToSignIn() {
        startActivity(new Intent(SignUp.this, SignIn.class));
    }

    private void goToMainMap() {
        startActivity(new Intent(SignUp.this, MainMap.class));
    }
}
