package com.example.friendlocation;

import static com.example.friendlocation.utils.Config.dateFormat;
import static com.example.friendlocation.utils.FirebaseUtils.getCurrentUserUID;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.TimePickerDialog;
import android.text.format.DateUtils;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.friendlocation.databinding.ActivityCreateEventBinding;
import com.example.friendlocation.utils.Event;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.Pair;
import com.example.friendlocation.utils.Place;
import com.example.friendlocation.utils.User;
import com.example.friendlocation.utils.UsersAdapterEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

public class CreateEvent extends AppCompatActivity {

    private static final int PLACE_REQUEST_CODE = 666;
    private static final int USER_REQUEST_CODE = 999;
    private ActivityCreateEventBinding binding;
    private Place place = new Place("default");
    private String TAG = "CreateEventTag";
    Calendar dateAndTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setInitialDateTime();
        ArrayList<User> users = new ArrayList<>();
        UsersAdapterEvent adapter = new UsersAdapterEvent(this, users);
        binding.usersList.setAdapter(adapter);

        binding.usersList.setLayoutManager(new LinearLayoutManager(this));

        binding.backToEventsImgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToEvents();
            }
        });

    }

    public void addEvent(View v) {
        Event ev = new Event();
        ev.name = binding.titleEt.getText().toString();
        ev.date = dateFormat.format(dateAndTime.getTime());
        ev.description = binding.descriptionEt.getText().toString();
        if (ev.name.isEmpty() || ev.date.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Empty fields are not allowed", Toast.LENGTH_SHORT).show();
            return;
        }
        UsersAdapterEvent adapter = (UsersAdapterEvent) binding.usersList.getAdapter();
        ev.membersUID = adapter.getUsersUID();
        ev.membersUID.add(getCurrentUserUID());
        ev.place = place;
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

    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    public void addUser(View v) {
        Intent intent = new Intent(this, SearchUser.class);
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
            user.uid = data.getStringExtra("uid");
            UsersAdapterEvent adapter = (UsersAdapterEvent) binding.usersList.getAdapter();
            adapter.addUser(user);
        }
        else if (requestCode == PLACE_REQUEST_CODE && resultCode == Activity.RESULT_OK) { // Проверка результата для MainMap
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