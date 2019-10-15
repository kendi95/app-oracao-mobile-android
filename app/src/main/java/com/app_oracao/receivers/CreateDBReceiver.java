package com.app_oracao.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.app_oracao.App;
import com.app_oracao.servicies.EventoDBService;

public class CreateDBReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equalsIgnoreCase(App.CREATE_DB_BROADCAST_RECEIVER)){
            if(intent.getBooleanExtra("isOpen", true)){
                context.startService(new Intent(context, EventoDBService.class).setAction(App.ACTION_SEND_EVENTOS_TO_EVENTODBSERVICE));
            } else {
                Toast.makeText(context, "False", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Não há dados recebidos direto do broadcast", Toast.LENGTH_LONG).show();
        }

    }
}
