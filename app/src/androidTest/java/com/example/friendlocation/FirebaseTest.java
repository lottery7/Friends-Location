package com.example.friendlocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;

import com.example.friendlocation.utils.Event;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.Pair;
import com.example.friendlocation.utils.Place;
import com.example.friendlocation.utils.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FirebaseTest {
    int userCount = 4;
    Integer globalUserId;
    Integer globalEventId;
    HashMap<String, User> users = new HashMap<>();
    HashMap<String, Event> events = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        globalUserId = 1;
        globalEventId = 1;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.useEmulator("10.0.2.2", 9000);
        FirebaseUtils.setFirebaseDatabase(database);
    }

    private String addUser(String name, String email, Pair<Double, Double> coord) {
        User user = new User(name, email, (globalUserId++).toString());
        users.put(user.id, user);
        user.coordinates = coord;
        FirebaseUtils.getDatabase().getReference("users").child(user.id).setValue(user);
        return user.id;
    }
    public void addUsers() throws InterruptedException {
        for (Integer i = 1; i <= userCount; i++) {
            addUser(i.toString(), i + "@mail.ru", new Pair<>(i.doubleValue() * 1.1, i.doubleValue() * 11.11));
        }
        FirebaseUtils.getDatabase().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                assertEquals(snapshot.getChildrenCount(), userCount);
                for (DataSnapshot x : snapshot.getChildren()) {
                    User usrFromBD = x.getValue(User.class);
                    User userBased = users.getOrDefault(usrFromBD.id, null);
                    assertEquals(usrFromBD, userBased);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //sleep(5000);
    }

    @Test
    public void addEventTest() throws InterruptedException {
        addUsers();
        for (Integer i = 1; i <= 4; i++){
            Event ev = new Event((globalEventId++).toString(), i.toString(), "description of " + i);
            ev.date = i + "." + i + "." + i;
            ev.uid = i.toString();
            ev.membersUID = new ArrayList<>();
            for (Integer j = 1; j <= i; j++) {
                ev.membersUID.add(j.toString());
            }
            ev.owner = i.toString();
            ev.place = new Place("Place for " + i, new Pair<>(i.doubleValue(), i.doubleValue()));
            FirebaseUtils.addEvent(ev);
            events.put(i.toString(), ev);
        }
        for (int i = 0; i <= userCount; i++) {
            FirebaseUtils.getDatabase().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    assertEquals(snapshot.getChildrenCount(), userCount);
                    for (DataSnapshot x : snapshot.getChildren()) {
                        User usrFromBD = x.getValue(User.class);
                        User userBased = users.getOrDefault(usrFromBD.id, null);
                        assertEquals(usrFromBD, userBased);
                        int cnt = 0;
                        for (DataSnapshot eventIdSnap : x.child("events").getChildren()) {
                            cnt++;
                            String eventId = eventIdSnap.getKey();
                            assertTrue(Integer.parseInt(userBased.id) <= Integer.parseInt(eventId) && Integer.parseInt(eventId) <= userCount);
                        }
                        assertEquals(userCount - Integer.parseInt(userBased.id) + 1, cnt);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        sleep(1000);
    }
}