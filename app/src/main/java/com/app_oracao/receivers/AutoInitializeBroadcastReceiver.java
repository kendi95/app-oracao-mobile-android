package com.app_oracao.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app_oracao.App;
import com.app_oracao.servicies.CreateDBService;

public class AutoInitializeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            context.startService(new Intent(context, CreateDBService.class).setAction(App.ACTION_SEND_CREATE_DB_TO_CREATEDBSERVICE));
        }
    }
}
