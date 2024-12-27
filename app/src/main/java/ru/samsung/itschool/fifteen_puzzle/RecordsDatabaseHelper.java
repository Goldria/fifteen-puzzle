package ru.samsung.itschool.fifteen_puzzle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class RecordsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "records.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_RECORDS = "records";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PLAYER_NAME = "player_name";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_MOVES = "moves";
    private static final String COLUMN_FIELD_SIZE = "field_size";

    public RecordsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_RECORDS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PLAYER_NAME + " TEXT, " +
                COLUMN_TIME + " INTEGER, " +
                COLUMN_MOVES + " INTEGER, " +
                COLUMN_FIELD_SIZE + " INTEGER, " +
                "UNIQUE(" + COLUMN_PLAYER_NAME + ", " + COLUMN_FIELD_SIZE + ") ON CONFLICT REPLACE)";
        db.execSQL(createTableQuery);
    }

    public List<String[]> getFilteredRecords(int fieldSize, String sortColumn) {
        List<String[]> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_PLAYER_NAME + ", MIN(" + COLUMN_TIME + ") AS " + COLUMN_TIME +
                ", MIN(" + COLUMN_MOVES + ") AS " + COLUMN_MOVES +
                " FROM " + TABLE_RECORDS +
                " WHERE " + COLUMN_FIELD_SIZE + " = ?" +
                " GROUP BY " + COLUMN_PLAYER_NAME +
                " ORDER BY " + sortColumn + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(fieldSize)});

        if (cursor.moveToFirst()) {
            do {
                String playerName = cursor.getString(0);
                String time = cursor.getString(1) + " сек";
                String moves = cursor.getString(2) + " ходов";
                records.add(new String[]{playerName, time, moves});
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return records;
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(db);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(db);
    }

    public void addRecord(String playerName, int time, int moves, int fieldSize) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYER_NAME, playerName);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_MOVES, moves);
        values.put(COLUMN_FIELD_SIZE, fieldSize);

        db.insert(TABLE_RECORDS, null, values);
        db.close();
    }
}
