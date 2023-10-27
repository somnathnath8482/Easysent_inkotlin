package com.easy.kotlintest.socket;

import static android.app.Notification.BADGE_ICON_SMALL;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.easy.kotlintest.R;
import com.easy.kotlintest.activity.LoginActivity;

import java.util.Random;


public class ShowNotification {

    @SuppressLint("UnspecifiedImmutableFlag")
    public void showNotification(Application application, int id, String message, String name, Bitmap bitmap) {

        Intent intent = new Intent();

        intent = new Intent(application.getApplicationContext(), LoginActivity.class);
        String channel_id = "notification_channel";
        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (application, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getActivity
                    (application, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }


        @SuppressLint("WrongConstant") NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(application.getApplicationContext(), channel_id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setBadgeIconType(BADGE_ICON_SMALL) //this is for show badges
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setOnlyAlertOnce(false)
                .setContentIntent(pendingIntent);

        if (bitmap != null) {
            builder.setLargeIcon(bitmap);
        }


        builder = builder.setContentTitle(name)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(message)
                )
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "web_app", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(new Random().nextInt(), builder.build());
    }


}
