package com.example.mybestlocation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
        values.put(COLUMN_NUMERO, position.getNumber());
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
        }
        db.close();
    }

    // Method to get all positions
    public List<Position> getAllPositions() {
        List<Position> positionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to select all positions
        String selectQuery = "SELECT * FROM " + TABLE_POSITIONS;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int pseudoIndex = cursor.getColumnIndex(COLUMN_PSEUDO);
            int numeroIndex = cursor.getColumnIndex(COLUMN_NUMERO);
            int longitudeIndex = cursor.getColumnIndex(COLUMN_LONGITUDE);
            int latitudeIndex = cursor.getColumnIndex(COLUMN_LATITUDE);

            // Check if any of the column indices are invalid
            if (idIndex == -1 || pseudoIndex == -1 || numeroIndex == -1 || longitudeIndex == -1 || latitudeIndex == -1) {
                Log.e("DatabaseHelper", "One or more column names are invalid. Please check your database schema.");
                cursor.close();
                return positionList; // return empty list or handle it accordingly
            }

            do {
                // Use the column indices to extract data from the cursor
                int id = cursor.getInt(idIndex);
                String pseudo = cursor.getString(pseudoIndex);
                String numero = cursor.getString(numeroIndex);
                String longitude = cursor.getString(longitudeIndex);
                String latitude = cursor.getString(latitudeIndex);

                Position position = new Position(id, pseudo, numero, longitude, latitude);
                positionList.add(position);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return positionList;
    }

}
