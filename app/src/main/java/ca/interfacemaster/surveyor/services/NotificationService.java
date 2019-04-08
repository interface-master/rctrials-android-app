package ca.interfacemaster.surveyor.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import ca.interfacemaster.surveyor.R;

public class NotificationService {

    private static Context mContext;
    private static NotificationManager mManager;
    public static String CHANNEL_ID;

    public NotificationService(Context context) {
        // vars
        this.mContext = context;
        CHANNEL_ID = mContext.getText(R.string.notification_channel).toString();
        // init
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // vars
            CharSequence name = "RCTrials Notifications";
            String description = "Notifications delivered by the RCTrials App";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            // channel
            NotificationChannel channel = new NotificationChannel(this.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            // manager
//            this.mManager = mContext.getSystemService(NotificationManager.class);
            this.mManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
            this.mManager.createNotificationChannel(channel);
        }
    }

    public Notification.Builder notify(String title, String body) {
        Log.d("NOTIFY","1");
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("NOTIFY",String.format("2: %s; %s", title, body));
            Notification.Builder nb = new Notification.Builder(this.mContext, this.CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.ic_assignment)
                    .setAutoCancel(true);
            mManager.notify(101, nb.build());
        }
        Log.d("NOTIFY","0");
        return null;
    }
}
