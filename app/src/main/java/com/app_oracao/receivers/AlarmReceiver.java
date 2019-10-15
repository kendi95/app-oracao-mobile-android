package com.app_oracao.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.app_oracao.App;
import com.app_oracao.R;

import java.text.SimpleDateFormat;

public class AlarmReceiver extends BroadcastReceiver {

    private SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat f2 = new SimpleDateFormat("HH:mm");

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(App.ALARM_BROADCAST_RECEIVER)){

            final String CHANNEL_ID = "oração_channel_id";
            final String CHANNEL_NAME = "Notificação do AppOração";

            PendingIntent pendingIntent = PendingIntent.getService(context, 100, intent, 0);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(intent.getStringExtra("titulo"))
                    .setContentText(f2.format(intent.getLongExtra("horario_inicio", 0))+" - "+
                            f2.format(intent.getLongExtra("horario_fim", 0)))
                    .setSound(sound)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setContentIntent(pendingIntent)
                    .setUsesChronometer(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.ic_check_circle_green_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_check_circle_green_24dp));
            manager.notify(0, builder.build());

        }
    }
}
