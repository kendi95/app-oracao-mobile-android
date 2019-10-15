package com.app_oracao.servicies;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.app_oracao.App;
import com.app_oracao.database.Database;
import com.app_oracao.database.model.EventoDB;
import com.app_oracao.database.service.EventoService;
import com.app_oracao.receivers.EventoReceiver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventoDBService extends Service {

    private EventoService service;
    private SQLiteDatabase db;
    private Database database;
    private List<EventoDB> eventoDBS;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database = new Database(this);
        db = database.getReadableDatabase();
        eventoDBS = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(intent.getAction().equals(App.ACTION_SEND_EVENTOS_TO_EVENTODBSERVICE)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getEventos();
                }
            }).start();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void getEventos(){
        service = new EventoService(this);
        eventoDBS = service.findAll();
        if(!eventoDBS.isEmpty()){
            Intent eventoReceiver = new Intent(this, EventoReceiver.class);
            eventoReceiver.putExtra("eventoDBS", (Serializable) eventoDBS);
            eventoReceiver.setAction(App.EVENTO_BROADCAST_RECEIVER);
            sendBroadcast(eventoReceiver);
            stopSelf();
        } else {
            stopSelf();
        }
    }
}
