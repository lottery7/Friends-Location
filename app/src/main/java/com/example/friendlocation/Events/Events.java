package com.example.friendlocation.Events;

import static com.example.friendlocation.Events.EventsListenerEvents.makeEventsList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
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

import java.util.ArrayList;

public class Events extends BottomBar {

    @Override
    protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_events);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ArrayList<Event> events = new ArrayList<>();
        EventAdapterEvents adapter = new EventAdapterEvents(this, events);
        RecyclerView eventsRv = findViewById(R.id.events_rv);
        eventsRv.setAdapter(adapter);
        eventsRv.setLayoutManager(new LinearLayoutManager(this));
        makeEventsList(adapter, getApplicationContext(), getResources());
    }

    public void createEvent(View v) {
        Intent intent = new Intent(this, CreateEvent.class);
        startActivity(intent);
    }
}