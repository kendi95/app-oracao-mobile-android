package com.app_oracao.database.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app_oracao.database.Database;
import com.app_oracao.database.model.UsuarioDB;

import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    private SQLiteDatabase db;
    private Database database;
    private Context context;

    public UsuarioService(Context context){
        this.context = context;
        database = new Database(context);
    }

    public boolean insert(UsuarioDB usuarioDB){
        db = database.getWritableDatabase();
        long result;

        ContentValues value = new ContentValues();
        value.put("nome", usuarioDB.getNome());
        value.put("email", usuarioDB.getEmail());
        result = db.insert("usuario", null, value);
        if(result == -1){
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }

    public UsuarioDB findByEmail(String email){
        db = database.getReadableDatabase();

        UsuarioDB usuarioDB = new UsuarioDB();
        String sql = "SELECT * FROM usuario WHERE email = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{email});

        if(cursor.moveToFirst()){
            do{
                usuarioDB.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id"))));
                usuarioDB.setNome(cursor.getString(cursor.getColumnIndexOrThrow("nome")));
                usuarioDB.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return usuarioDB;
    }

    public UsuarioDB findById(String id){
        db = database.getReadableDatabase();
        UsuarioDB usuarioDB = new UsuarioDB();
        String sql = "SELECT * FROM usuario WHERE id = "+id;
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor.moveToFirst()){
            do {
                usuarioDB.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id"))));
                usuarioDB.setNome(cursor.getString(cursor.getColumnIndexOrThrow("nome")));
                usuarioDB.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return usuarioDB;
    }

    public List<UsuarioDB> findAll(){
        db = database.getReadableDatabase();

        List<UsuarioDB> usuarioDBS = new ArrayList<>();
        UsuarioDB usuarioDB = new UsuarioDB();
        String sql = "SELECT * FROM usuario";
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor.moveToFirst()){
            do {
                usuarioDB.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id"))));
                usuarioDB.setNome(cursor.getString(cursor.getColumnIndexOrThrow("nome")));
                usuarioDB.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                usuarioDBS.add(usuarioDB);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return usuarioDBS;
    }
}
