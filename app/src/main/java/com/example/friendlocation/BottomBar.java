package com.example.friendlocation;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.friendlocation.Events.Events;
import com.example.friendlocation.Maps.MainMap;

public class BottomBar extends AppCompatActivity {
    public void goToEvents() {
        startActivity(new Intent(this, Events.class));
        overridePendingTransition(0, 0);
    }

    public void goToChats() {
        startActivity(new Intent(this, AllChats.class));
        overridePendingTransition(0, 0);
    }

    public void goToMap() {
        startActivity(new Intent(this, MainMap.class));
        overridePendingTransition(0, 0);
    }

    public void goToFriends() {
        startActivity(new Intent(this,Friends.class));
        overridePendingTransition(0, 0);
    }

    public void goToSetting() {
        startActivity(new Intent(this, Setting.class));
        overridePendingTransition(0, 0);
    }
}
