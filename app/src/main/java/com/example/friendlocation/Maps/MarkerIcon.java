package com.example.friendlocation.Maps;

import static com.example.friendlocation.utils.Config.dateFormat;
import static com.example.friendlocation.utils.Config.makerDateFormat;

import static java.lang.Math.min;
import static java.lang.Math.round;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.example.friendlocation.R;
import com.example.friendlocation.utils.Event;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.text.ParseException;
import java.util.Objects;

public class MarkerIcon {
    private final int BRIEF_BACKGROUND_HEIGHT;
    private final int BRIEF_BACKGROUND_WIDTH;
    private final float BRIEF_TOP_SPACE_PERCENT = 0.05F;
    private final float BRIEF_ICON_HEIGHT_PERCENT = 0.42F;
    private final float BRIEF_MIDDLE_HEIGHT_SPACE_PERCENT = 0.02F;
    private final float TEXT_HEIGHT_PERCENT = 0.12F;
    Drawable mark;
    Event event;
    Context context;
    Resources resources;

    public MarkerIcon(Event event, Context context, Resources resources) {
        this.event = event;
        this.context = context;
        this.resources = resources;
        BRIEF_BACKGROUND_HEIGHT = round(70 * resources.getDisplayMetrics().density);
        BRIEF_BACKGROUND_WIDTH = round(50 * resources.getDisplayMetrics().density);
        this.mark = scaleImage(Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.meating_mark)), BRIEF_BACKGROUND_WIDTH, BRIEF_BACKGROUND_HEIGHT);
    }

    public void findTextSize(String text, Canvas canvas, Paint paint, int paintWidth, int paintHeight, float textHeightShiftPercent) {
        Rect bounds = new Rect();
        int textSize = 50;
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), bounds);
        int textWidth = bounds.width();
        int textHeight = bounds.height();
        while (textWidth > paintWidth - 10 || textHeight > TEXT_HEIGHT_PERCENT * paintHeight) {
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
        findTextSize(event.name, canvas, paint,
                BRIEF_BACKGROUND_WIDTH, BRIEF_BACKGROUND_HEIGHT,
                BRIEF_TOP_SPACE_PERCENT + BRIEF_ICON_HEIGHT_PERCENT+BRIEF_MIDDLE_HEIGHT_SPACE_PERCENT+TEXT_HEIGHT_PERCENT/2);
        findTextSize(makerDateFormat.format(Objects.requireNonNull(dateFormat.parse(event.date))),
                canvas, paint, BRIEF_BACKGROUND_WIDTH, BRIEF_BACKGROUND_HEIGHT,
                BRIEF_TOP_SPACE_PERCENT + BRIEF_ICON_HEIGHT_PERCENT+2*BRIEF_MIDDLE_HEIGHT_SPACE_PERCENT+TEXT_HEIGHT_PERCENT*3/2);

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.plus_circle);
        float scale = min(
                (float) (BRIEF_BACKGROUND_WIDTH * (1 - BRIEF_TOP_SPACE_PERCENT)) /drawable.getIntrinsicWidth(),
                BRIEF_BACKGROUND_HEIGHT*BRIEF_ICON_HEIGHT_PERCENT/drawable.getIntrinsicHeight()
        );
        drawable = scaleImage(drawable, round(drawable.getIntrinsicWidth()*scale), round(drawable.getIntrinsicHeight()*scale));
        drawable.setBounds((BRIEF_BACKGROUND_WIDTH-drawable.getIntrinsicWidth())/2,
                (int) round(BRIEF_BACKGROUND_HEIGHT*BRIEF_TOP_SPACE_PERCENT),
                (BRIEF_BACKGROUND_WIDTH+drawable.getIntrinsicWidth())/2,
                (int) (drawable.getIntrinsicHeight()+round(BRIEF_BACKGROUND_HEIGHT*BRIEF_TOP_SPACE_PERCENT)));
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
