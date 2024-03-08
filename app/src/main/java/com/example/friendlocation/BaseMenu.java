package com.example.friendlocation;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class BaseMenu extends AppCompatActivity {
    public void goToCalendar(View v) {
        Intent intent = new Intent(this, Calendar.class);
        startActivity(intent);
    }

    public void goToChats(View v) {
        Intent intent = new Intent(this, AllChats.class);
        startActivity(intent);
    }

    public void goToMap(View v) {
        Intent intent = new Intent(this, MainMap.class);
        startActivity(intent);
    }

    public void goToFriends(View v) {
        Intent intent = new Intent(this, Friends.class);
        startActivity(intent);
    }

    public void goToSetting(View v) {
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
    }
}
