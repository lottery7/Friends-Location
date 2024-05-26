package com.example.friendlocation.Events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.friendlocation.BottomBar;
import com.example.friendlocation.adapters.EventAdapterEvents;
import com.example.friendlocation.databinding.ActivityCreateEventBinding;
import com.example.friendlocation.databinding.ActivityEventsBinding;
import com.example.friendlocation.utils.Event;

import java.util.ArrayList;

public class Events extends BottomBar {
    private ActivityEventsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ArrayList<Event> events = new ArrayList<>();
        events.add(new Event("uid", "name", "date", "descript"));
        EventAdapterEvents adapter = new EventAdapterEvents(this, events);
        binding.eventsRv.setAdapter(adapter);
        binding.eventsRv.setLayoutManager(new LinearLayoutManager(this));
    }

    public void createEvent(View v) {
        Intent intent = new Intent(this, CreateEvent.class);
        startActivity(intent);
    }
}