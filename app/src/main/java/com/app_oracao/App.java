package com.app_oracao;

import android.app.Application;
import android.content.Intent;

import com.app_oracao.servicies.CreateDBService;

public class App extends Application {

    public static final String ACTION_SEND_CREATE_DB_TO_CREATEDBSERVICE = "com.app_oracao.ACTION_SEND_CREATE_DB_TO_CREATEDBSERVICE";
    public static final String ACTION_SEND_EVENTOS_TO_EVENTODBSERVICE = "com.app_oracao.ACTION_SEND_EVENTOS_TO_EVENTODBSERVICE";
    public static final String ACTION_SEND_EVENTOS_TO_ALARMSERVICE = "com.app_oracao.ACTION_SEND_EVENTOS_TO_ALARMSERVICE";
    public static final String CREATE_DB_BROADCAST_RECEIVER = "com.app_oracao.CREATE_DB_BROADCAST_RECEIVER";
    public static final String EVENTO_BROADCAST_RECEIVER = "com.app_oracao.EVENTO_BROADCAST_RECEIVER";
    public static final String ALARM_BROADCAST_RECEIVER = "com.app_oracao.ALARM_BROADCAST_RECEIVER";

    private Intent intent = null;

    @Override
    public void onCreate() {
        super.onCreate();

        intent = new Intent(this, CreateDBService.class).setAction(ACTION_SEND_CREATE_DB_TO_CREATEDBSERVICE);
        startService(intent);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        onCreate();
    }
}
