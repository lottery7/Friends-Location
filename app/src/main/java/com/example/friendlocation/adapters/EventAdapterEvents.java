package com.example.friendlocation.adapters;
import static androidx.core.app.ActivityCompat.startActivityForResult;
import static com.example.friendlocation.utils.Config.dateFormat;
import static com.example.friendlocation.utils.FirebaseUtils.getCurrentUserID;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlocation.Events.CreateEvent;
import com.example.friendlocation.Events.FirebaseEventPart;
import com.example.friendlocation.Maps.MainMap;
import com.example.friendlocation.R;
import com.example.friendlocation.utils.Event;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.User;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EventAdapterEvents extends RecyclerView.Adapter<EventAdapterEvents.ViewHolder>{

    private final LayoutInflater inflater;
    public final int PLACE_REQUEST_CODE = 0;
    private final List<Event> events;
    private final Context context;

    public EventAdapterEvents(Context context, List<Event> events) {
        this.events = events;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
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
        holder.editBtn.setOnClickListener(v -> {
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).uid.equals(event.uid)) {
                    if (!event.owner.equals(getCurrentUserID())) {
                        Toast.makeText(context, "You should be an owner to edit event", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(context, CreateEvent.class);
                    intent.putExtra("EDIT_MODE", event.uid);
                    context.startActivity(intent);
                    return;
                }
            }
        });
        if (!getCurrentUserID().equals(event.owner)) {
            holder.editBtn.setVisibility(View.INVISIBLE);
        }
        holder.deleteBtn.setOnClickListener(v -> {
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).uid.equals(event.uid)) {
                    FirebaseEventPart.exitFromEvent(event);
                    events.remove(i);
                    notifyItemRemoved(i);
                    return;
                }
            }
        });
    }

    public void addEvent(Event event) throws ParseException {
        if (events.stream().anyMatch((v)->v.uid.equals(event.uid))) {
            return;
        }
        for (int i = 0 ; i < events.size(); i++) {
            if (Objects.requireNonNull(dateFormat.parse(event.date)).before(dateFormat.parse(events.get(i).date))) {
                events.add(i, event);
                notifyItemInserted(i);
                return;
            }
        }
        events.add(event);
        notifyItemInserted(events.size()-1);
    }
    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView eventIconView;
        final TextView nameView, dateView;
        final ImageButton deleteBtn;
        final ImageButton editBtn;
        final ImageButton chatBtn;
        ViewHolder(View view){
            super(view);
            eventIconView = view.findViewById(R.id.eventIcon_imgv);
            nameView = view.findViewById(R.id.eventName_tv);
            dateView = view.findViewById(R.id.eventDate_tv);
            editBtn = view.findViewById(R.id.editEvent_imgbtn);
            chatBtn = view.findViewById(R.id.chatEvent_imgbtn);
            deleteBtn = view.findViewById(R.id.deleteEvent_imgbtn);
        }
    }
}