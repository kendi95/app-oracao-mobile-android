package com.app_oracao.activities;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.app_oracao.App;
import com.app_oracao.R;
import com.app_oracao.controllers.PedidoController;
import com.app_oracao.database.model.EventoDB;
import com.app_oracao.database.model.UsuarioDB;
import com.app_oracao.database.service.EventoService;
import com.app_oracao.database.service.UsuarioService;
import com.app_oracao.exceptions.StandardError;
import com.app_oracao.servicies.CreateDBService;
import com.app_oracao.utils.BaseURL;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class IntercessaoActivity extends AppCompatActivity {

    private TextView intercessao_multiline_descricao, intercessao_editText_motivo, intercessao_editText_data,
            intercessao_editText_autor;
    private TextView id_new_schedule_titulo, id_new_schedule_data_inicio, id_new_schedule_data_fim,
            id_new_schedule_horas_inicio, id_new_schedule_horas_fim;
    private FloatingActionButton intercessao_fab;
    private String motivo, descricao, autor;
    private String tipo, token, email, nome;
    private Date data;
    private Long id;
    private Calendar currentTime;
    private PedidoController controller;
    private Retrofit retrofit;
    private SimpleDateFormat formatWeek = new SimpleDateFormat("E");
    private SimpleDateFormat formatDay = new SimpleDateFormat("d");
    private SimpleDateFormat formatMonth = new SimpleDateFormat("MMMM");
    private SimpleDateFormat formatYear = new SimpleDateFormat("y");
    private SimpleDateFormat formatHour = new SimpleDateFormat("HH:mm");
    private EventoService service;
    private UsuarioService userService;
    private EventoDB eventoDB = new EventoDB();
    private List<Calendar> datas = new ArrayList<>();
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercessao);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getValuesOfIntent();
        initializeViews();

        intercessao_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTime = Calendar.getInstance();

                eventoDB.setData(currentTime.getTime().getTime());
                eventoDB.setHoras_inicio(currentTime.getTimeInMillis());
                eventoDB.setHoras_fim(currentTime.getTimeInMillis());

                View viewChild = getLayoutInflater().inflate(R.layout.new_schedule, null);

                findViewById(viewChild);
                setValueOfDateAndTimeInTextView();
                showDatePicker(id_new_schedule_data_inicio, id_new_schedule_data_fim);
                showTimePicker(id_new_schedule_horas_inicio, id_new_schedule_horas_fim);

                AlertDialog.Builder dialog = new AlertDialog.Builder(viewChild.getContext())
                        .setView(viewChild)
                        .setCancelable(false)
                        .setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveScheduling();
                                dialog.dismiss();
                            }
                        });
                dialog.create();
                dialog.show();
            }
        });

        setValuesOfIntent();

        retrofit = new Retrofit.Builder()
                .baseUrl(new BaseURL().getBASE_URL())
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(IntercessaoActivity.this, AppActivity.class)
            .putExtra("tipo", tipo)
            .putExtra("token", token)
            .putExtra("email", email)
            .putExtra("nome", nome));
        finish();
        return true;
    }

    private void getValuesOfIntent(){
        motivo = getIntent().getStringExtra("motivo");
        descricao = getIntent().getStringExtra("descricao");
        autor = getIntent().getStringExtra("autor");
        data = (Date) getIntent().getSerializableExtra("data");
        id = getIntent().getLongExtra("id", 0);

        tipo = getIntent().getStringExtra("tipo");
        token = getIntent().getStringExtra("token");
        email = getIntent().getStringExtra("email");
        nome = getIntent().getStringExtra("nome");
    }

    private void toAppActivity(){
        startActivity(new Intent(this, AppActivity.class)
            .putExtra("tipo", tipo)
            .putExtra("token", token)
            .putExtra("email", email)
            .putExtra("nome", nome));
        finish();
    }

    private void initializeViews(){
        intercessao_editText_motivo = findViewById(R.id.id_intercessao_editText_motivo);
        intercessao_multiline_descricao = findViewById(R.id.id_intercessao_multiline_descricao);
        intercessao_editText_data = findViewById(R.id.id_intercessao_editText_data);
        intercessao_editText_autor = findViewById(R.id.id_intercessao_editText_autor);
        intercessao_fab = findViewById(R.id.id_intercessao_fab);
    }

    private void setValuesOfIntent(){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        intercessao_editText_motivo.setText(motivo);
        intercessao_multiline_descricao.setText(descricao);
        intercessao_editText_autor.setText(autor);
        intercessao_editText_data.setText(format.format(data));
    }

    private void findViewById(View viewChild){
        id_new_schedule_titulo = viewChild.findViewById(R.id.id_new_schedule_titulo);
        id_new_schedule_data_inicio = viewChild.findViewById(R.id.id_new_schedule_data_inicio);
        id_new_schedule_horas_inicio = viewChild.findViewById(R.id.id_new_schedule_horas_inicio);
        id_new_schedule_horas_fim = viewChild.findViewById(R.id.id_new_schedule_horas_fim);
    }

    private void setValueOfDateAndTimeInTextView(){
        id_new_schedule_data_inicio.setText(
                formatWeek.format(currentTime.getTime())+", "+
                        formatDay.format(currentTime.getTime())+" de "+
                        formatMonth.format(currentTime.getTime())+" de "+
                        formatYear.format(currentTime.getTime()));
        id_new_schedule_horas_inicio.setText(formatHour.format(currentTime.getTime()));
        id_new_schedule_horas_fim.setText(formatHour.format(currentTime.getTime()));
    }

    private void showDatePicker(TextView tv1, TextView tv2){
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSelectDateListener listener = new OnSelectDateListener() {
                    @Override
                    public void onSelect(List<Calendar> calendars) {
                        for(int i = 0; i< calendars.size(); i++){
                            datas.add(calendars.get(i));
                        }
                    }
                };

                DatePickerBuilder builder = new DatePickerBuilder(v.getContext(), listener)
                        .date(Calendar.getInstance())
                        .headerColor(R.color.colorDatePickerRed)
                        .headerLabelColor(android.R.color.white)
                        .abbreviationsBarColor(R.color.colorPrimary)
                        .abbreviationsLabelsColor(android.R.color.white)
                        .anotherMonthsDaysLabelsColor(android.R.color.darker_gray)
                        .todayLabelColor(R.color.colorDatePickerBlue)
                        .pagesColor(R.color.colorDatePickerRed)
                        .selectionColor(android.R.color.darker_gray)
                        .selectionLabelColor(R.color.colorDatePickerRed)
                        .dialogButtonsColor(android.R.color.white)
                        .daysLabelsColor(android.R.color.white)
                        .pickerType(CalendarView.MANY_DAYS_PICKER);

                DatePicker datePicker = builder.build();
                datePicker.show();
            }
        });

    }

    private void showTimePicker(TextView tv1, TextView tv2){
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(IntercessaoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, hourOfDay, minute);
                        id_new_schedule_horas_inicio.setText(formatHour.format(calendar.getTime()));
                        eventoDB.setHoras_inicio(calendar.getTimeInMillis());
                    }
                }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true);
                dialog.show();
            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(IntercessaoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, hourOfDay, minute);
                        id_new_schedule_horas_fim.setText(formatHour.format(calendar.getTime()));
                        eventoDB.setHoras_fim(calendar.getTimeInMillis());
                    }
                }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true);
                dialog.show();
            }
        });
    }

    private void showAlertDialog(String title, String message, int icon){
        AlertDialog.Builder dialog = new AlertDialog.Builder(IntercessaoActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(icon)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        toAppActivity();
                    }
                });
        dialog.create();
        dialog.show();
    }

    private void setHorasInicio(Calendar data){
        Calendar calendar2 = Calendar.getInstance();

        calendar2.setTimeInMillis(eventoDB.getHoras_inicio());
        calendar2.set(Calendar.YEAR, data.get(Calendar.YEAR));
        calendar2.set(Calendar.MONTH, data.get(Calendar.MONTH));
        calendar2.set(Calendar.DAY_OF_MONTH, data.get(Calendar.DAY_OF_MONTH));
        calendar2.set(Calendar.SECOND, 0);

        eventoDB.setHoras_inicio(calendar2.getTimeInMillis());
    }

    private void setHorasFim(Calendar data){
        Calendar calendar2 = Calendar.getInstance();

        calendar2.setTimeInMillis(eventoDB.getHoras_fim());
        calendar2.set(Calendar.YEAR, data.get(Calendar.YEAR));
        calendar2.set(Calendar.MONTH, data.get(Calendar.MONTH));
        calendar2.set(Calendar.DAY_OF_MONTH, data.get(Calendar.DAY_OF_MONTH));
        calendar2.set(Calendar.SECOND, 0);

        eventoDB.setHoras_fim(calendar2.getTimeInMillis());
    }



    private void saveDataIntoDatabase(){
        service = new EventoService(IntercessaoActivity.this);

        userService = new UsuarioService(IntercessaoActivity.this);
        UsuarioDB usuarioDB = userService.findByEmail(email);
        if(usuarioDB.getId() == null){
            usuarioDB = new UsuarioDB(null, nome, email);
            if(userService.insert(usuarioDB)){
                for(int i=0; i<datas.size(); i++){
                    eventoDB.setData(datas.get(i).getTimeInMillis());
                    setHorasInicio(datas.get(i));
                    setHorasFim(datas.get(i));
                    if(id_new_schedule_titulo.getText().toString() == null){
                        eventoDB.setTitulo("Sem título");
                        service.insert(eventoDB, email, nome);
                    } else {
                        eventoDB.setTitulo(id_new_schedule_titulo.getText().toString());
                        service.insert(eventoDB, email, nome);
                    }
                }
                showAlertDialog("Sucesso", "EventoDB cadstrado com sucesso.", R.drawable.ic_check_circle_green_24dp);
                intent = new Intent(this, CreateDBService.class).setAction(App.ACTION_SEND_CREATE_DB_TO_CREATEDBSERVICE);
                startService(intent);
            }
        } else {
            for(int i=0; i<datas.size(); i++){
                eventoDB.setData(datas.get(i).getTimeInMillis());
                setHorasInicio(datas.get(i));
                setHorasFim(datas.get(i));
                if(id_new_schedule_titulo.getText().toString() == null){
                    eventoDB.setTitulo("Sem título");
                    service.insert(eventoDB, email, nome);
                } else {
                    eventoDB.setTitulo(id_new_schedule_titulo.getText().toString());
                    service.insert(eventoDB, email, nome);
                }
            }
            showAlertDialog("Sucesso", "EventoDB cadstrado com sucesso.", R.drawable.ic_check_circle_green_24dp);
            intent = new Intent(this, CreateDBService.class).setAction(App.ACTION_SEND_CREATE_DB_TO_CREATEDBSERVICE);
            startService(intent);
        }
    }

    private void saveScheduling(){
        controller = retrofit.create(PedidoController.class);
        Call<Void> call = controller.insertUsuariosIntoPedido(id, token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 204){
                    saveDataIntoDatabase();
                } else {
                    try{
                        Gson gson = new GsonBuilder().create();
                        StandardError error = new StandardError();
                        error = gson.fromJson(response.errorBody().string(), StandardError.class);
                        showAlertDialog("Erro: "+response.code(), error.getMessage(), R.drawable.ic_error_red_24dp);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showAlertDialog("Alerta", t.getMessage(), R.drawable.ic_error_red_24dp);
            }
        });
    }

}
