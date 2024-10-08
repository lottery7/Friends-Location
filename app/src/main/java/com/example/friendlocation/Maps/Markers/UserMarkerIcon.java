package com.example.friendlocation.Maps.Markers;

import static com.example.friendlocation.utils.Config.dateFormat;
import static com.example.friendlocation.utils.Config.makerDateFormat;
import static java.lang.Math.min;
import static java.lang.Math.round;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.core.content.ContextCompat;

import com.example.friendlocation.R;
import com.example.friendlocation.utils.AndroidUtils;
import com.example.friendlocation.utils.Event;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.User;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Objects;

public class UserMarkerIcon {
    private final int BACKGROUND_HEIGHT;
    private final int BACKGROUND_WIDTH;
    private final float TOP_SPACE_PERCENT = 0.05F;
    private final float ICON_HEIGHT_PERCENT = 0.5F;
    private final float MIDDLE_HEIGHT_SPACE_PERCENT = 0.02F;
    private final float TEXT_HEIGHT_PERCENT = 0.15F;
    Drawable mark;
    User user;
    Context context;
    Resources resources;
    Drawable icon;

    public UserMarkerIcon(User user, Context context, Resources resources, Drawable icon) {
        this.user = user;
        this.context = context;
        this.resources = resources;
        BACKGROUND_HEIGHT = round(50 * resources.getDisplayMetrics().density);
        BACKGROUND_WIDTH = round(35 * resources.getDisplayMetrics().density);
        this.mark = scaleImage(Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.brief_user_mark)), BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = icon;
    }

    public void findTextSize(String text, Canvas canvas, Paint paint, int paintWidth, int paintHeight, float textHeightShiftPercent) {
        Rect bounds = new Rect();
        int textSize = 50;
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), bounds);
        int textWidth = bounds.width();
        int textHeight = bounds.height();
        while (textWidth > paintWidth * (1 - 2 * TOP_SPACE_PERCENT) || textHeight > TEXT_HEIGHT_PERCENT * paintHeight) {
            paint.setTextSize(--textSize);
            paint.getTextBounds(text, 0, text.length(), bounds);
            textWidth = bounds.width();
            textHeight = bounds.height();
        }

        int textX = (paintWidth - textWidth) / 2;
        int textY = (int) (paintHeight * textHeightShiftPercent + textHeight / 2);

        canvas.drawText(text, textX, textY, paint);
    }

    private Drawable scaleImage(Drawable image, int expectedWidth, int expectedHeight) {
        Objects.requireNonNull(image);
        Bitmap bitmap = Bitmap.createBitmap(expectedWidth, expectedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        image.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        image.draw(canvas);
        return new BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, expectedWidth, expectedHeight, false));
    }


    public BitmapDescriptor getBriefMarkerIcon() throws ParseException {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(mark.getIntrinsicWidth(), mark.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        mark.setBounds(0, 0, mark.getIntrinsicWidth(), mark.getIntrinsicHeight());
        mark.draw(canvas);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        findTextSize(user.name, canvas, paint,
                BACKGROUND_WIDTH, BACKGROUND_HEIGHT,
                TOP_SPACE_PERCENT + ICON_HEIGHT_PERCENT + MIDDLE_HEIGHT_SPACE_PERCENT +TEXT_HEIGHT_PERCENT/2);
        Drawable drawable = icon;
        float scale = min(
                (float) (BACKGROUND_WIDTH * (1 - 2 * TOP_SPACE_PERCENT)) /drawable.getIntrinsicWidth(),
                BACKGROUND_HEIGHT * ICON_HEIGHT_PERCENT /drawable.getIntrinsicHeight()
        );
        drawable = scaleImage(drawable, round(drawable.getIntrinsicWidth()*scale), round(drawable.getIntrinsicHeight()*scale));
        drawable.setBounds((BACKGROUND_WIDTH - drawable.getIntrinsicWidth())/2,
                (int) round(BACKGROUND_HEIGHT * TOP_SPACE_PERCENT),
                (BACKGROUND_WIDTH +drawable.getIntrinsicWidth())/2,
                (int) (drawable.getIntrinsicHeight()+round(BACKGROUND_HEIGHT * TOP_SPACE_PERCENT)));
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
