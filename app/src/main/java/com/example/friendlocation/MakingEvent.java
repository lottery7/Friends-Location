package com.example.friendlocation;

import static com.example.friendlocation.utils.Config.dateFormat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.friendlocation.databinding.ActivityMakingEventBinding;
import com.example.friendlocation.utils.Event;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.User;
import com.example.friendlocation.utils.UsersAdapter;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MakingEvent extends AppCompatActivity {

    private ActivityMakingEventBinding binding;
    Calendar dateAndTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMakingEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setInitialDateTime();

        ArrayList<User> users = new ArrayList<>();
        users.add(new User ("Егор", "yes@mail.ru", "uid1"));
        users.add(new User ("Егор2", "yes@mail.ru", "uid2"));
        users.add(new User ("Егор3", "yes@mail.ru", "uid3"));
        users.add(new User ("Егор4", "yes@mail.ru", "uid4"));
        UsersAdapter adapter = new UsersAdapter(this, users);
        binding.usersList.setAdapter(adapter);

        binding.usersList.setLayoutManager(new LinearLayoutManager(this));

    }

    public void addEvent(View v) {
        Event ev = new Event();
        ev.name = binding.eventNameEd.getText().toString();
        ev.date = dateFormat.format(dateAndTime.getTime());
        ev.description = binding.eventDescriptionEd.getText().toString();
        if (ev.name.isEmpty() || ev.date.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Empty fields are not allowed", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUtils.addEvent(ev);
    }

    public void setDate(View v) {
        new DatePickerDialog(MakingEvent.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public void setTime(View v) {
        new TimePickerDialog(MakingEvent.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    private void setInitialDateTime() {
        binding.eventDateEd.setText(DateUtils.formatDateTime(this,
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
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        User user = new User();
        user.name = data.getStringExtra("name");
        user.email = data.getStringExtra("mail");
        user.uid = data.getStringExtra("uid");
        UsersAdapter adapter = (UsersAdapter) binding.usersList.getAdapter();
        adapter.addUser(user);
    }
}