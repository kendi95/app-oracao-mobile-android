package com.app_oracao.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app_oracao.App;
import com.app_oracao.database.model.EventoDB;
import com.app_oracao.servicies.AlarmService;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

public class EventoReceiver extends BroadcastReceiver {

    private SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(App.EVENTO_BROADCAST_RECEIVER)){
            Intent alarmService = new Intent(context, AlarmService.class);
            alarmService.setAction(App.ACTION_SEND_EVENTOS_TO_ALARMSERVICE);
            List<EventoDB> eventoDBS = (List<EventoDB>) intent.getSerializableExtra("eventoDBS");
            alarmService.putExtra("eventoDBS", (Serializable) eventoDBS);
            context.startService(alarmService);
        }
    }
}
