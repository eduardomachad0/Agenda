package com.example.agenda.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

//estende a classe auxiliar de SQLite
public class DatabaseHelper extends SQLiteOpenHelper {
    //propriedades como constantes - nome e versao do database
    private static final String DB_NAME = "contatos.db";
    private static final int DB_VERSION = 2;

    //construtor
    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //sobrepoe o metodo onCreate - faz a criacao do database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE contato (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "email TEXT, " +
                "telefone TEXT NOT NULL," +
                "foto TEXT);");
    }

    //sobrepoe o metodo onUpgrade - quando a base ja existe, e muda a versao, por exemplo
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE contato ADD COLUMN foto TEXT");
        }
        db.execSQL("DROP TABLE IF EXISTS contato");
        onCreate(db);
    }
}