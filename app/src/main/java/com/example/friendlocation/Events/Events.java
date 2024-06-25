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
import com.example.friendlocation.databinding.ActivitySettingBinding;
import com.example.friendlocation.utils.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.LinkedList;

public class Events extends BottomBar {

    private ActivityEventsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.events, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinkedList<Event> events = new LinkedList<>();
        EventAdapterEvents adapter = new EventAdapterEvents(this, events);
//        calendarView.setHeaderTextColor(ContextCompat.getColor(this, R.color.black));
//        binding.calendarView.setDateTextAppearance(ContextCompat.getColor(this, R.color.bright_pink));

        binding.eventsRv.setAdapter(adapter);
        binding.eventsRv.setLayoutManager(new LinearLayoutManager(this));
        makeEventsList(adapter, binding.calendarView, getApplicationContext(), getResources());

        //   /--- Bottom Bar ---/
        binding.bottomBar.setSelectedItemId(R.id.events_case);
        binding.bottomBar.setOnItemSelectedListener(item -> {
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