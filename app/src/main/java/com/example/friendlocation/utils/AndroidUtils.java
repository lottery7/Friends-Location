package com.example.friendlocation.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class AndroidUtils {

    public static void setProfilePic(Context context, Uri imgUri, ImageView imgView){
        Glide.with(context).load(imgUri).apply(RequestOptions.circleCropTransform()).into(imgView);
    }

}
