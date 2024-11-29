package com.example.mybestlocation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "best_location_db";
    private static final int DATABASE_VERSION = 1;

    // Table and columns names
    private static final String TABLE_POSITIONS = "Positions";
    private static final String COLUMN_ID = "idPosition";
    private static final String COLUMN_PSEUDO = "pseudo";
    private static final String COLUMN_NUMERO = "numero";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_LATITUDE = "latitude";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_POSITIONS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PSEUDO + " TEXT NOT NULL, "
                + COLUMN_NUMERO + " TEXT, "
                + COLUMN_LONGITUDE + " TEXT NOT NULL, "
                + COLUMN_LATITUDE + " TEXT NOT NULL)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSITIONS);
        onCreate(db);
    }

    // Méthode pour ajouter une nouvelle position
    public long addPosition(Position position) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PSEUDO, position.getPseudo());
        values.put(COLUMN_NUMERO, position.getNumero());
        values.put(COLUMN_LONGITUDE, position.getLongitude());
        values.put(COLUMN_LATITUDE, position.getLatitude());

        // Insertion dans la table
        return db.insert(TABLE_POSITIONS, null, values);
    }
//pour supprimer une position
public void deletePosition(int id) {
    SQLiteDatabase db = this.getWritableDatabase();
    int rowsAffected = db.delete(TABLE_POSITIONS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

    if (rowsAffected > 0) {
        Log.d("Database", "Position  supprimé avec ID: " + id);
    } else {
        Log.d("Database", "Aucun contact trouvé avec ce ID: " + id);
    }        db.close();
}
    // récupérer toutes les positions


    // Vous pouvez également ajouter d'autres méthodes pour mettre à jour, supprimer ou rechercher des positions.
}
