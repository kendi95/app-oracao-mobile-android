package com.app_oracao.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_oracao";
    private static final int DATABASE_VERSION = 9;

    private static final String TABLE_USUARIO = "CREATE TABLE IF NOT EXISTS usuario (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "nome VARCHAR(255)," +
            "email VARCHAR(255))";

    private static final String TABLE_EVENTO = "CREATE TABLE IF NOT EXISTS evento (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "titulo VARCHAR(255)," +
            "data DATE," +
            "horas_inicio DATE," +
            "horas_fim DATE," +
            "usuario_id INTEGER," +
            "FOREIGN KEY(usuario_id) REFERENCES usuario(id))";

    private static final String DROP_TABLE_USUARIO = "DROP TABLE IF EXISTS usuario";

    private static final String DROP_TABLE_EVENTO = "DROP TABLE IF EXISTS evento";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_USUARIO);
        db.execSQL(TABLE_EVENTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_USUARIO);
        db.execSQL(DROP_TABLE_EVENTO);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        onCreate(db);
    }
}
