package com.example.cc10624finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_cardmemorization";
    private static final String TABLE_NAME = "tbl_score";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TIME = "Time";
    private static final String COLUMN_DIFFICULTY = "Difficulty";
    private static final String COLUMN_SCORE = "Score";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TIME + " TEXT,"
                + COLUMN_DIFFICULTY + " TEXT,"
                + COLUMN_SCORE + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addScore(Score score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, score.getTime());
        values.put(COLUMN_DIFFICULTY, score.getDifficulty());
        values.put(COLUMN_SCORE, score.getScore());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Score> getAllScores() {
        List<Score> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_DIFFICULTY + " IN ('Normal', 'Hard')" +
                " ORDER BY " + COLUMN_DIFFICULTY + " DESC, " +
                COLUMN_SCORE + " DESC, " +
                COLUMN_TIME + " ASC" +
                " LIMIT 5";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int time = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                String difficulty = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIFFICULTY));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
                Score scoreObject = new Score(time, difficulty, score);
                scores.add(scoreObject);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return scores;
    }


}
