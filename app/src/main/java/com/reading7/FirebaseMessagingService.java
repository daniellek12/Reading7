package com.reading7;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;


import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String messageTitle = remoteMessage.getNotification().getTitle();
        String messageBody = remoteMessage.getNotification().getBody();

        String type = remoteMessage.getData().get("type");
        String book_title = remoteMessage.getData().get("book_title");
        String from_email = remoteMessage.getData().get("from_email");
        String from_user_email = remoteMessage.getData().get("user_email");
        String get_action = remoteMessage.getNotification().getClickAction();

        NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(this,"defaultId").setContentTitle(messageTitle)
                 .setSmallIcon(R.drawable.logo)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentText(messageBody);
        Intent res = new Intent(get_action);
        res.putExtra("type",type);
        res.putExtra("book_title",book_title);
        res.putExtra("from_email",from_email);



        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,(int)System.currentTimeMillis(),res,PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(resultPendingIntent);

        int mNotification_id = (int)System.currentTimeMillis();
        NotificationManager mNotifyMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("defaultId", "NOTIFICATION_CHANNEL_NAME", importance);
            assert mNotifyMgr != null;
            mBuilder.setChannelId("defaultId");
            mNotifyMgr.createNotificationChannel(notificationChannel);
        }
        mNotifyMgr.notify(mNotification_id, mBuilder.build());


        /*int mNotification_id = (int)System.currentTimeMillis();

        NotificationMessagingFirebase notificationHelper = new NotificationMessagingFirebase(this,messageTitle,messageBody,type,request_id,get_action);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(mNotification_id, nb.build());

*/


        /**Creates an explicit intent for an Activity in your app**/





    }
}
