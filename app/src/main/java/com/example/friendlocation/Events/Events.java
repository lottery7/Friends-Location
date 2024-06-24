package com.example.friendlocation.Events;

import static com.example.friendlocation.Events.EventsListenerEvents.makeEventsList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;

import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.BottomBar;
import com.example.friendlocation.R;
import com.example.friendlocation.adapters.EventAdapterEvents;
import com.example.friendlocation.databinding.ActivityCreateEventBinding;
import com.example.friendlocation.databinding.ActivityEventsBinding;
import com.example.friendlocation.utils.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.LinkedList;

public class Events extends BottomBar {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_events);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.muted_cyan));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinkedList<Event> events = new LinkedList<>();
        EventAdapterEvents adapter = new EventAdapterEvents(this, events);
        RecyclerView eventsRv = findViewById(R.id.events_rv);
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setHeaderTextColor(ContextCompat.getColor(this, R.color.black));

        eventsRv.setAdapter(adapter);
        eventsRv.setLayoutManager(new LinearLayoutManager(this));
        makeEventsList(adapter, calendarView, getApplicationContext(), getResources());

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

    public void createEvent(View v) {
        Intent intent = new Intent(this, CreateEvent.class);
        startActivity(intent);
    }
}