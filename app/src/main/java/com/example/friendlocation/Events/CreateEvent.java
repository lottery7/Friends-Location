package com.example.friendlocation.Events;

import static com.example.friendlocation.utils.Config.dateFormat;
import static com.example.friendlocation.utils.FirebaseUtils.getCurrentUserID;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

import com.example.friendlocation.Maps.MainMap;
import com.example.friendlocation.R;
import com.example.friendlocation.databinding.ActivityCreateEventBinding;
import com.example.friendlocation.utils.Event;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.Pair;
import com.example.friendlocation.utils.Place;
import com.example.friendlocation.utils.User;
import com.example.friendlocation.adapters.UsersAdapterCreateEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class CreateEvent extends AppCompatActivity {

    private static final int PLACE_REQUEST_CODE = 666;
    private static final int USER_REQUEST_CODE = 999;
    private ActivityCreateEventBinding binding;
    private Place place = new Place();
    private String TAG = "CreateEventTag";
    private String createMod = "CreateEvent";
    Calendar dateAndTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
            if (extras == null) {
                createMod = null;
            } else {
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

            FirebaseUtils.getDatabase().getReference("events").child(createMod).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Event ev = snapshot.getValue(Event.class);
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
                        FirebaseUtils.getDatabase().getReference("users").child(ev.membersUID.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                assert user != null;
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
    }

    public void constructEvent(Event ev) {
        ev.name = binding.titleEt.getText().toString();
        ev.date = dateFormat.format(dateAndTime.getTime());
        ev.description = binding.descriptionEt.getText().toString();
        if (ev.name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Empty name are not allowed", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ev.date.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Empty date are not allowed", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Objects.equals(place.description, "default")) {
            Toast.makeText(getApplicationContext(), "Empty place are not allowed", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ev.countLinesInDescription() > Event.MAX_DESCRIPTION_LINES) {
            Toast.makeText(getApplicationContext(), "The description has too much lines (" +
                    ev.countLinesInDescription() + "/" + Event.MAX_DESCRIPTION_LINES + ")", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ev.maxLineSizeInDescription() > Event.MAX_DESCRIPTION_LINE_SIZE) {
            Toast.makeText(getApplicationContext(), "The description has too big line (" +
                    ev.maxLineSizeInDescription() + "/" + Event.MAX_DESCRIPTION_LINE_SIZE + ")", Toast.LENGTH_SHORT).show();
            return;
        }
        UsersAdapterCreateEvent adapter = (UsersAdapterCreateEvent) binding.usersList.getAdapter();
        ev.membersUID = adapter.getUsersUID();
        ev.membersUID.add(getCurrentUserID());
        ev.place = place;
        ev.owner = getCurrentUserID();
    }

    public void saveEvent(View v) {
        Event ev = new Event();
        ev.uid = createMod;
        constructEvent(ev);
        FirebaseUtils.saveEvent(ev);
        finish();
    }

    public void addEvent(View v) {
        Event ev = new Event();
        constructEvent(ev);
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

}