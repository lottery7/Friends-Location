package com.example.friendlocation.Maps;

import static com.example.friendlocation.utils.Config.dateFormat;
import static com.example.friendlocation.utils.Config.makerDateFormat;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.System.lineSeparator;

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

public class FullEventMarkerIcon {
    private final int BACKGROUND_HEIGHT;
    private final int BACKGROUND_WIDTH;
    private final float TOP_SPACE_PERCENT = 0.05F;
    private final float ICON_HEIGHT_PERCENT;
    private final float MIDDLE_HEIGHT_SPACE_PERCENT = 0.02F;
    private final float TEXT_HEIGHT_PERCENT = 0.05F;
    Drawable mark;
    Event event;
    Context context;
    Resources resources;

    public FullEventMarkerIcon(Event event, Context context, Resources resources) {
        this.event = event;
        this.context = context;
        this.resources = resources;
        BACKGROUND_HEIGHT = round(225 * resources.getDisplayMetrics().density);
        BACKGROUND_WIDTH = round(200 * resources.getDisplayMetrics().density);
        int spaceCount = 4 + event.countLinesInDescription();
        ICON_HEIGHT_PERCENT = (1F - 3 * TOP_SPACE_PERCENT - spaceCount * MIDDLE_HEIGHT_SPACE_PERCENT - spaceCount * TEXT_HEIGHT_PERCENT);
        this.mark = scaleImage(Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.full_meating_mark)), BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }

    public void findTextSize(String text, Canvas canvas, Paint paint, int paintWidth, int paintHeight, float textHeightShiftPercent) {
        Rect bounds = new Rect();
        int textSize = 100;
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), bounds);
        int textWidth = bounds.width();
        int textHeight = bounds.height();
        while (textWidth > paintWidth * (1F - 2 * TOP_SPACE_PERCENT) || textHeight > TEXT_HEIGHT_PERCENT * paintHeight) {
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


    public BitmapDescriptor getFullMarkerIcon() throws ParseException {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(mark.getIntrinsicWidth(), mark.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        mark.setBounds(0, 0, mark.getIntrinsicWidth(), mark.getIntrinsicHeight());
        mark.draw(canvas);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        findTextSize(event.name, canvas, paint,
                BACKGROUND_WIDTH, BACKGROUND_HEIGHT,
                TOP_SPACE_PERCENT + ICON_HEIGHT_PERCENT + MIDDLE_HEIGHT_SPACE_PERCENT + TEXT_HEIGHT_PERCENT / 2);

        findTextSize(makerDateFormat.format(Objects.requireNonNull(dateFormat.parse(event.date))),
                canvas, paint, BACKGROUND_WIDTH, BACKGROUND_HEIGHT,
                TOP_SPACE_PERCENT + ICON_HEIGHT_PERCENT + 2 * MIDDLE_HEIGHT_SPACE_PERCENT + TEXT_HEIGHT_PERCENT * 3 / 2);

        findTextSize("Members: " + event.membersUID.size(),
                canvas, paint, BACKGROUND_WIDTH, BACKGROUND_HEIGHT,
                TOP_SPACE_PERCENT + ICON_HEIGHT_PERCENT + 3 * MIDDLE_HEIGHT_SPACE_PERCENT + TEXT_HEIGHT_PERCENT * 5 / 2);
        String[] description = event.description.split(lineSeparator());
        int index = 0;
        for (; index < event.countLinesInDescription(); index++) {
            findTextSize(description[index], canvas, paint,
                    BACKGROUND_WIDTH, BACKGROUND_HEIGHT,
                    TOP_SPACE_PERCENT + ICON_HEIGHT_PERCENT + (4 + index) * MIDDLE_HEIGHT_SPACE_PERCENT + TEXT_HEIGHT_PERCENT * (7 + 2 * index) / 2);
        }

        findTextSize("Click to go to chat", canvas, paint,
                BACKGROUND_WIDTH, BACKGROUND_HEIGHT,
                TOP_SPACE_PERCENT + ICON_HEIGHT_PERCENT + (4 + index) * MIDDLE_HEIGHT_SPACE_PERCENT + TEXT_HEIGHT_PERCENT * (7 + 2 * index) / 2);


        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.event_icon_mark);
        float scale = min(
                (float) (BACKGROUND_WIDTH * (1 - TOP_SPACE_PERCENT)) / drawable.getIntrinsicWidth(),
                BACKGROUND_HEIGHT * ICON_HEIGHT_PERCENT / drawable.getIntrinsicHeight()
        );
        drawable = scaleImage(drawable, round(drawable.getIntrinsicWidth() * scale), round(drawable.getIntrinsicHeight() * scale));
        drawable.setBounds((BACKGROUND_WIDTH - drawable.getIntrinsicWidth()) / 2,
                (int) round(BACKGROUND_HEIGHT * TOP_SPACE_PERCENT),
                (BACKGROUND_WIDTH + drawable.getIntrinsicWidth()) / 2,
                (int) (drawable.getIntrinsicHeight() + round(BACKGROUND_HEIGHT * TOP_SPACE_PERCENT)));
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
