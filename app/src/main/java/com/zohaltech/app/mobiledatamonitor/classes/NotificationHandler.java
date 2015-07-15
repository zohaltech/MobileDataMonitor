package com.zohaltech.app.mobiledatamonitor.classes;

/**
 * Created by Ali on 7/15/2015.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.zohaltech.app.mobiledatamonitor.R;
import com.zohaltech.app.mobiledatamonitor.activities.MainActivity;

public class NotificationHandler {

    private static final int NOTIFICATION_ID = 1;

    private static NotificationManager notificationManager;

    static {
        notificationManager = (android.app.NotificationManager) App.context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void displayNotification(Context context, String title, String text, String subText) {
        Notification notification;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB)

        {
            PendingIntent resultPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            notification = new Notification(R.drawable.ic_notification, title, System.currentTimeMillis());
            notification.setLatestEventInfo(context, title, text, resultPendingIntent);
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notificationManager.notify(NOTIFICATION_ID, notification);
        } else

        {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification))
                            .setContentTitle(title)
                            .setContentText(text)
                            .setSubText(subText)
                                    //.setVisibility(NotificationCompat.VISIBILITY_SECRET) //invisible in lock screen
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) //visible in lock screen
                            .setOngoing(true)
                            .setShowWhen(false)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setAutoCancel(false);
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(context, MainActivity.class);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    public static void cancelNotification(Context context) {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}