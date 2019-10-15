package com.app_oracao.database.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;

import com.app_oracao.R;
import com.app_oracao.database.Database;
import com.app_oracao.database.model.EventoDB;
import com.app_oracao.database.model.UsuarioDB;

import java.util.ArrayList;
import java.util.List;

public class EventoService {

    private SQLiteDatabase db;
    private Database database;
    private UsuarioService usuarioService;
    private List<EventoDB> eventoDBS = new ArrayList<>();
    private EventoDB eventoDB;
    private Context context;

    public EventoService(Context context){
        database = new Database(context);
        usuarioService = new UsuarioService(context);
        this.context = context;
    }

    public void insert(EventoDB eventoDB, String email, String nome){
        db = database.getWritableDatabase();
        long result;

        UsuarioDB usuarioDB = usuarioService.findByEmail(email);

        if(usuarioDB == null){
            usuarioDB = new UsuarioDB(null, nome, email);
            usuarioService.insert(usuarioDB);
            usuarioDB = usuarioService.findByEmail(email);

            ContentValues value = new ContentValues();
            value.put("titulo", eventoDB.getTitulo());
            value.put("data", eventoDB.getData());
            value.put("horas_inicio", eventoDB.getHoras_inicio());
            value.put("horas_fim", eventoDB.getHoras_fim());
            value.put("usuario_id", usuarioDB.getId());
            result = db.insert("evento", null, value);
            if(result == -1){
                db.close();
                showAlertDialog(context, "Erro", "Não foi possível cadastrar um eventoDB.", R.drawable.ic_error_red_24dp);
            } else {
                db.close();
            }
        } else {
            ContentValues value = new ContentValues();
            value.put("titulo", eventoDB.getTitulo());
            value.put("data", eventoDB.getData());
            value.put("horas_inicio", eventoDB.getHoras_inicio());
            value.put("horas_fim", eventoDB.getHoras_fim());
            value.put("usuario_id", usuarioDB.getId());
            result = db.insert("evento", null, value);
            if(result == -1){
                db.close();
                showAlertDialog(context, "Erro", "Não foi possível cadastrar um eventoDB.", R.drawable.ic_error_red_24dp);
            } else {
                db.close();
            }
        }
    }

    public List<EventoDB> findAll(){
        db = database.getReadableDatabase();
        String sql = "SELECT * FROM evento";
        Cursor cursor = db.rawQuery(sql,null);

        int id = cursor.getColumnIndexOrThrow("id");
        int titulo = cursor.getColumnIndexOrThrow("titulo");
        int data = cursor.getColumnIndexOrThrow("data");
        int horas_inicio = cursor.getColumnIndexOrThrow("horas_inicio");
        int horas_fim = cursor.getColumnIndexOrThrow("horas_fim");
        int usuario_id = cursor.getColumnIndexOrThrow("usuario_id");

        if(cursor.moveToFirst()){
            do{
                eventoDB = new EventoDB();
                eventoDB.setId(Integer.parseInt(cursor.getString(id)));
                eventoDB.setTitulo(cursor.getString(titulo));
                eventoDB.setData(Long.parseLong(cursor.getString(data)));
                eventoDB.setHoras_inicio(Long.parseLong(cursor.getString(horas_inicio)));
                eventoDB.setHoras_fim(Long.parseLong(cursor.getString(horas_fim)));
                eventoDB.setUsuarioId(Integer.parseInt(cursor.getString(usuario_id)));
                eventoDBS.add(eventoDB);
            }while(cursor.moveToNext());
        }
        db.close();
        return eventoDBS;
    }

    private void showAlertDialog(Context context, String title, String message, int icon) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(icon)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.create();
        dialog.show();
    }
}
