package com.example.friendlocation;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.friendlocation.Events.Events;
import com.example.friendlocation.Maps.MainMap;

public class BottomBar extends AppCompatActivity {
    public void goToEvents(View v) {
        if(!(this instanceof Events)){
            Intent intent = new Intent(this, Events.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    }

    public void goToChats(View v) {
        if(!(this instanceof AllChats)){
            Intent intent = new Intent(this, AllChats.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    }

    public void goToMap(View v) {
        if(!(this instanceof MainMap)){
            Intent intent = new Intent(this, MainMap.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    }

    public void goToFriends(View v) {
        if(!(this instanceof Friends)){
            Intent intent = new Intent(this, Friends.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    }

    public void goToSetting(View v) {
        if(!(this instanceof Setting)){
            Intent intent = new Intent(this, Setting.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    }
}
