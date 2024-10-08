package com.example.friendlocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.friendlocation.databinding.ActivitySettingBinding;
import com.example.friendlocation.utils.AndroidUtils;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import kotlin.Unit;

public class Setting extends BottomBar {

    private ActivitySettingBinding binding;
    private String TAG = "SettingsTag";
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseCurrentUser;
    User currentUser;
    private BottomNavigationView bottomNavigationView;
    private String userId;
    ActivityResultLauncher<Intent> imgPickLauncher;
    Uri selectedImgUrl;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imgPickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImgUrl = data.getData();
                            AndroidUtils.setProfilePic(this,selectedImgUrl, binding.photoIv);
                            storeProfilePic(this, selectedImgUrl);
                        }
                    }
                });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        firebaseCurrentUser = mAuth.getCurrentUser();
        getUserData();


        //   /--- Pick Photo ---/
        binding.editPhotoIv.setOnClickListener((v) -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(intent -> {
                        imgPickLauncher.launch(intent);
                        return null;
                    });
        });

        //   /--- Log Out ---/
        binding.logOutBtn.setOnClickListener((v) -> {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(Setting.this, SignIn.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            });
        });

        //   /--- Username Changed ---/
        binding.userNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2 && !binding.userNameEt.getText().toString().equals(currentUserName)) {
                    binding.saveNameIv.setVisibility(View.VISIBLE);
                } else {
                    binding.saveNameIv.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.saveNameIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.name = binding.userNameEt.getText().toString();
                Toast.makeText(Setting.this, currentUser.name, Toast.LENGTH_SHORT).show();
                updateUser();
                binding.saveNameIv.setVisibility(View.INVISIBLE);
            }
        });

        //   /--- Bottom Bar ---/
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_bar);
        bottomNavigationView.setSelectedItemId(R.id.settings_case);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.events_case) {
                goToEvents();
                return true;
            } else if (itemId == R.id.chats_case) {
                goToChats();
                return true;
            } else if (itemId == R.id.map_case) {
                goToMap();
                return true;
            } else if (itemId == R.id.friends_case) {
                goToFriends();
                return true;
            } else if (itemId == R.id.settings_case) {
                // Stay in the current Settings activity
                return true;
            } else {
                return false;
            }
        });


    }

    private void getUserData() {
        FirebaseUtils.getProfilePicStorageRef().getDownloadUrl()
                        .addOnCompleteListener(task->{
                            if (task.isSuccessful()){
                                Uri uri = task.getResult();
                                AndroidUtils.setProfilePic(this,uri,binding.photoIv);
                            }
                        });
        FirebaseUtils.getCurrentUserDetails().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                if (currentUser == null) {
                    return;
                }
                binding.userNameEt.setText(currentUser.name);
                binding.saveNameIv.setVisibility(View.INVISIBLE);
                currentUserName=currentUser.name;
                binding.userEmailEt.setText(firebaseCurrentUser.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUser(){
        FirebaseUtils.currentUserDetails().set(currentUser)
                .addOnCompleteListener(task->{
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "User name updated successfully", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error updating user profile.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void storeProfilePic(Context context, Uri imgUri){
        FirebaseUtils.getProfilePicStorageRef().putFile(imgUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error uploading profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
