package com.example.friendlocation.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;

public class Config {
    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static SimpleDateFormat makerDateFormat = new SimpleDateFormat("dd-MM-yyyy");

}
