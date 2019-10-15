package com.app_oracao.servicies;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.app_oracao.App;
import com.app_oracao.database.model.EventoDB;
import com.app_oracao.database.model.UsuarioDB;
import com.app_oracao.database.service.UsuarioService;
import com.app_oracao.receivers.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmService extends Service {

    private List<Calendar> datas;
    private List<EventoDB> eventoDBS;
    private List<UsuarioDB> usuarioDBS;
    private UsuarioDB usuarioDB;
    private UsuarioService service;
    private SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        datas = new ArrayList<>();
        usuarioDB = new UsuarioDB();
        service = new UsuarioService(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        setAlarm(intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void setAlarm(final Intent intent){
        if(intent.getAction().equals(App.ACTION_SEND_EVENTOS_TO_ALARMSERVICE)){
            eventoDBS = new ArrayList<>();
            eventoDBS = (List<EventoDB>) intent.getSerializableExtra("eventoDBS");
            setEventoToAlarm();
        }
    }

    private void setEventoToAlarm(){
        usuarioDBS = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
                List<Calendar> hours = new ArrayList<>();
                List<Calendar> datas = new ArrayList<>();
                List<AlarmManager> alarms = new ArrayList<>();
                usuarioDBS = service.findAll();
                for(int i = 0; i< eventoDBS.size(); i++){

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(eventoDBS.get(i).getHoras_inicio());

                    if(new Date(System.currentTimeMillis()).after(calendar.getTime())){
                        continue;
                    }

                    if(calendar.get(Calendar.HOUR_OF_DAY) < (new Date(System.currentTimeMillis()).getHours())){
                        continue;
                    }

                    if(calendar.get(Calendar.MINUTE) < (new Date(System.currentTimeMillis()).getMinutes())){
                        continue;
                    }

                    Log.i("eventoDBS", "passou");
                    Log.i("eventoDBS", f2.format(eventoDBS.get(i).getData())+" - "+f.format(eventoDBS.get(i).getHoras_inicio()));

                    Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                    intent.setAction(App.ALARM_BROADCAST_RECEIVER);
                    intent.putExtra("titulo", eventoDBS.get(i).getTitulo());
                    intent.putExtra("horario_inicio", calendar.getTimeInMillis());
                    intent.putExtra("horario_fim", eventoDBS.get(i).getHoras_fim());

                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarms.add(manager);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), i, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    for(int z=0; z < alarms.size(); z++){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            alarms.get(z).setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        }
                    }

                }

            }
        }).start();

    }

}
