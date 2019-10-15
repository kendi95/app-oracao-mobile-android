package com.app_oracao.servicies;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.app_oracao.App;
import com.app_oracao.database.Database;
import com.app_oracao.receivers.CreateDBReceiver;

public class CreateDBService extends Service {

    private SQLiteDatabase db;
    private Database database;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database = new Database(this);
        db = database.getWritableDatabase();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(intent.getAction().equals(App.ACTION_SEND_CREATE_DB_TO_CREATEDBSERVICE)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    createOrOpenDB();
                }
            }).start();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createOrOpenDB(){
        Intent eventoDBService = new Intent(this, CreateDBReceiver.class);
        eventoDBService.setAction(App.CREATE_DB_BROADCAST_RECEIVER);

        if(db.isOpen()){
            eventoDBService.putExtra("isOpen", db.isOpen());
            sendBroadcast(eventoDBService);

        }
    }
}
