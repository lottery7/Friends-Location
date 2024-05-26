package com.example.friendlocation.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.R;
import com.example.friendlocation.utils.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventAdapterEvents extends RecyclerView.Adapter<EventAdapterEvents.ViewHolder>{

    private final LayoutInflater inflater;
    private final List<Event> events;

    public EventAdapterEvents(Context context, List<Event> events) {
        this.events = events;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public EventAdapterEvents.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.event_adapter_events, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventAdapterEvents.ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventIconView.setImageResource(R.drawable.event_icon_mark);
        holder.nameView.setText(event.name);
        holder.dateView.setText(event.date);
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i).uid.equals(event.uid)) {
                        events.remove(i);
                        notifyItemRemoved(i);
                        return;
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView eventIconView;
        final TextView nameView, dateView;
        final ImageButton deleteBtn;
        ViewHolder(View view){
            super(view);
            eventIconView = view.findViewById(R.id.eventIcon_imgv);
            nameView = view.findViewById(R.id.eventName_tv);
            dateView = view.findViewById(R.id.eventDate_tv);
            deleteBtn = view.findViewById(R.id.deleteEvent_imgbtn);
        }
    }
}