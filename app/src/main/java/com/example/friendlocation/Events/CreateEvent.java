package com.example.friendlocation.Events;

import static com.example.friendlocation.Setting.storeProfilePic;
import static com.example.friendlocation.utils.Config.dateFormat;
import static com.example.friendlocation.utils.FirebaseUtils.getCurrentUserID;
import static com.example.friendlocation.utils.FirebaseUtils.getDatabase;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.TimePickerDialog;
import android.text.format.DateUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.friendlocation.BottomBar;
import com.example.friendlocation.ChatMessage;
import com.example.friendlocation.ChatroomModel;
import com.example.friendlocation.Maps.MainMap;
import com.example.friendlocation.R;
import com.example.friendlocation.databinding.ActivityCreateEventBinding;
import com.example.friendlocation.utils.AndroidUtils;
import com.example.friendlocation.utils.ChatroomUtils;
import com.example.friendlocation.utils.Event;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.Pair;
import com.example.friendlocation.utils.Place;
import com.example.friendlocation.utils.User;
import com.example.friendlocation.adapters.UsersAdapterCreateEvent;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class CreateEvent extends BottomBar {

    private static final int PLACE_REQUEST_CODE = 666;
    private static final int USER_REQUEST_CODE = 999;
    private ActivityCreateEventBinding binding;
    ActivityResultLauncher<Intent> imgPickLauncher;
    Uri selectedImgUrl;
    private Place place = new Place();
    private String TAG = "CreateEventTag";
    private String createMod = "CreateEvent";
    private String eventChatUID;
    Calendar dateAndTime = Calendar.getInstance();
    Context context;

    String eventUID = getDatabase().getReference("events").push().getKey();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();


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

        setInitialDateTime();
        ArrayList<User> users = new ArrayList<>();
        UsersAdapterCreateEvent adapter = new UsersAdapterCreateEvent(this, users);
        binding.usersList.setAdapter(adapter);

        binding.usersList.setLayoutManager(new LinearLayoutManager(this));

        binding.backToEventsImgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToEvents();
            }
        });

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                createMod = extras.getString("EDIT_MODE");
            }
        } else {
            createMod = (String) savedInstanceState.getSerializable("EDIT_MODE");
        }
        Log.w(TAG, "Mode: " + createMod);

        Button safeBtn = findViewById(R.id.create_event);
        if (Objects.equals(createMod, "CreateEvent")){
            safeBtn.setText("Create");
            safeBtn.setOnClickListener(this::addEvent);
        } else {
            safeBtn.setText("Save");
            safeBtn.setOnClickListener(this::saveEvent);

            getDatabase().getReference("events").child(createMod).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Event ev = snapshot.getValue(Event.class);
                    try {
                        eventUID = ev.uid;
                    } catch (Exception e) {
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    }
                    FirebaseUtils.getEventProfilePicStorageRef(eventUID).getDownloadUrl()
                            .addOnCompleteListener(task->{
                                if (task.isSuccessful()){
                                    Uri uri = task.getResult();
                                    AndroidUtils.setProfilePic(context,uri,binding.photoIv);
                                }
                            });
                    try {
                        eventChatUID = ev.chatUID;
                    } catch (Exception e) {
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    }
                    try {
                        ((TextView) findViewById(R.id.title_et)).setText(ev.name);
                    } catch (Exception e) {
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    }
                    try {
                        ((TextView) findViewById(R.id.description_et)).setText(ev.description);
                    } catch (Exception e) {
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    }
                    try {
                        ((TextView) findViewById(R.id.date_et)).setText(ev.date);
                    } catch (Exception e) {
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    }
                    try {
                        ((TextView) findViewById(R.id.place_et)).setText(ev.place.description);
                        place = ev.place;
                    } catch (Exception e) {
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    }
                    for (int i = 0; i < ev.membersUID.size(); i++ ){
                        getDatabase().getReference("users").child(ev.membersUID.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                if (user == null) {
                                    return;
                                }
                                if (!user.id.equals(getCurrentUserID())) {
                                    adapter.addUser(user);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        //   /--- Pick Photo ---/
        binding.editPhotoIv.setOnClickListener((v) -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(intent -> {
                        imgPickLauncher.launch(intent);
                        return null;
                    });
        });

        //   /--- Bottom Bar ---/
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_bar);
        bottomNavigationView.setSelectedItemId(R.id.events_case);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.events_case) {
                // Stay in the current Event activity
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
                goToSetting();
                return true;
            } else {
                return false;
            }
        });
        //   /--- Bottom Bar ---/


    }

    public boolean constructEvent(Event ev) {
        if (ev.uid == null) {
            ev.uid = eventUID;
        }
        ev.chatUID = eventChatUID;
        ev.name = binding.titleEt.getText().toString();
        ev.date = dateFormat.format(dateAndTime.getTime());
        ev.description = binding.descriptionEt.getText().toString();
        if (ev.name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Empty name are not allowed", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ev.date.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Empty date are not allowed", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Objects.equals(place.description, "default")) {
            Toast.makeText(getApplicationContext(), "Empty place are not allowed", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ev.countLinesInDescription() > Event.MAX_DESCRIPTION_LINES) {
            Toast.makeText(getApplicationContext(), "The description has too much lines (" +
                    ev.countLinesInDescription() + "/" + Event.MAX_DESCRIPTION_LINES + ")", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ev.maxLineSizeInDescription() > Event.MAX_DESCRIPTION_LINE_SIZE) {
            Toast.makeText(getApplicationContext(), "The description has too big line (" +
                    ev.maxLineSizeInDescription() + "/" + Event.MAX_DESCRIPTION_LINE_SIZE + ")", Toast.LENGTH_SHORT).show();
            return false;
        }
        UsersAdapterCreateEvent adapter = (UsersAdapterCreateEvent) binding.usersList.getAdapter();
        ev.membersUID = adapter.getUsersUID();
        ev.membersUID.add(getCurrentUserID());
        ev.place = place;
        ev.owner = getCurrentUserID();
        return true;
    }

    public void saveEvent(View v) {
        Event ev = new Event();
        ev.uid = createMod;
        if (!constructEvent(ev)) {
            return;
        }
        FirebaseUtils.saveEvent(ev);
        FirebaseUtils.getChatroomReference(ev.chatUID).update("title", ev.name, "userIds", ev.membersUID);
        finish();
    }

    public void addEvent(View v) {
        Event ev = new Event();
        if (!constructEvent(ev)) {
            return;
        }
        ChatroomModel chatroomModel = ChatroomUtils.createGroupModel(ev.membersUID);
        chatroomModel.title = ev.name;
        ev.chatUID = chatroomModel.id;
        chatroomModel.lastMessageDate = Timestamp.now();
        chatroomModel.lastMessageSenderId = FirebaseUtils.getCurrentUserID();
        chatroomModel.lastMessageText = "Create the group";
        FirebaseUtils.getChatroomReference(ev.chatUID).set(chatroomModel);
        ChatMessage chatMessage = new ChatMessage(
                chatroomModel.lastMessageText,
                FirebaseUtils.getCurrentUserID(),
                chatroomModel.lastMessageDate
        );

        FirebaseUtils.getChatroomMessagesReference(chatroomModel.id).add(chatMessage);
        FirebaseUtils.addEvent(ev);
        finish();
    }

    public void setDate(View v) {
        new DatePickerDialog(CreateEvent.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public void setTime(View v) {
        new TimePickerDialog(CreateEvent.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    private void setInitialDateTime() {
        binding.dateEt.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));
    }

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    public void addUser(View v) {
        Intent intent = new Intent(this, SearchUserEvent.class);
        startActivityForResult(intent, USER_REQUEST_CODE);
    }


    public void selectPlace(View v) {
        Intent intent = new Intent(this, MainMap.class);
        intent.putExtra("MAP_MODE", "select_place");
        startActivityForResult(intent, PLACE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == USER_REQUEST_CODE && resultCode == Activity.RESULT_OK) { // Проверка результата для SearchUser
            if (data == null) {
                return;
            }
            User user = new User();
            user.name = data.getStringExtra("name");
            user.email = data.getStringExtra("mail");
            user.id = data.getStringExtra("uid");
            UsersAdapterCreateEvent adapter = (UsersAdapterCreateEvent) binding.usersList.getAdapter();
            adapter.addUser(user);
        } else if (requestCode == PLACE_REQUEST_CODE && resultCode == Activity.RESULT_OK) { // Проверка результата для MainMap
            if (data != null) {
                place.description = data.getStringExtra("PLACE_DESCRIPTION");
                String lt = data.getStringExtra("PLACE_LATITUDE");
                String lg = data.getStringExtra("PLACE_LONGITUDE");
                place.coordinates = new Pair<>(Double.valueOf(Objects.requireNonNull(lt)),
                        Double.valueOf(Objects.requireNonNull(lg)));
                binding.placeEt.setText(place.description);
                Log.w(TAG, "Place: " + place);
            }
        }
    }

    private void backToEvents() {
        finish();
    }

    public void storeProfilePic(Context context, Uri imgUri){
        FirebaseUtils.getEventProfilePicStorageRef(eventUID).putFile(imgUri)
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