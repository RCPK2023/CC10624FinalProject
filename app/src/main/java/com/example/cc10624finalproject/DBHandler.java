package com.example.cc10624finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_cardmemorization";

    // Score table
    private static final String TABLE_SCORE = "tbl_score";
    private static final String COLUMN_SCORE_ID = "id";
    private static final String COLUMN_TIME = "Time";
    private static final String COLUMN_DIFFICULTY = "Difficulty";
    private static final String COLUMN_SCORE = "Score";

    // User table
    private static final String TABLE_USER = "tbl_user";
    private static final String COLUMN_USER_ID = "user_id";

    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_PLAYER_SCORE = "player_score";

    private static final String PREF_NAME = "UserPref";
    private static final String KEY_CURRENT_USER_ID = "currentUserId";

    private static final String TABLE_USER_SCORE = "tbl_user_score";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_SCORE_TABLE = "CREATE TABLE " + TABLE_SCORE + " ("
                + COLUMN_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TIME + " INTEGER, "
                + COLUMN_DIFFICULTY + " TEXT, "
                + COLUMN_SCORE + " INTEGER)";
        db.execSQL(CREATE_SCORE_TABLE);

        // New junction table for linking users and scores
        String CREATE_USER_SCORE_TABLE = "CREATE TABLE " + TABLE_USER_SCORE + " ("
                + COLUMN_USER_ID + " INTEGER, "
                + COLUMN_SCORE_ID + " INTEGER, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "), "
                + "FOREIGN KEY(" + COLUMN_SCORE_ID + ") REFERENCES " + TABLE_SCORE + "(" + COLUMN_SCORE_ID + "))";
        db.execSQL(CREATE_USER_SCORE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE);
        onCreate(db);
    }

    public void addScore(int userId, Score score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues scoreValues = new ContentValues();
        scoreValues.put(COLUMN_TIME, score.getTime());
        scoreValues.put(COLUMN_DIFFICULTY, score.getDifficulty());
        scoreValues.put(COLUMN_SCORE, score.getScore());
        long scoreId = db.insert(TABLE_SCORE, null, scoreValues);

        if (scoreId != -1) {
            // Insert a new record into the USER_SCORE junction table
            ContentValues userScoreValues = new ContentValues();
            userScoreValues.put(COLUMN_USER_ID, userId);
            userScoreValues.put(COLUMN_SCORE_ID, scoreId);
            db.insert(TABLE_USER_SCORE, null, userScoreValues);
        }

        db.close();
    }



    public List<UserScore> getTopScores() {
        List<UserScore> userScores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a limit for each difficulty
        int limitPerDifficulty = 10;

        // Create the query
        String query = "SELECT u." + COLUMN_USER_ID + ", u." + COLUMN_NAME + ", u." + COLUMN_PASSWORD +
                ", s." + COLUMN_SCORE_ID + ", s." + COLUMN_TIME + ", s." + COLUMN_DIFFICULTY + ", s." + COLUMN_SCORE + ", top_score" +
                " FROM " + TABLE_USER + " u" +
                " JOIN " + TABLE_USER_SCORE + " us ON u." + COLUMN_USER_ID + " = us." + COLUMN_USER_ID +
                " JOIN (" +
                "     SELECT " + COLUMN_SCORE_ID + ", " + COLUMN_TIME + ", " + COLUMN_DIFFICULTY + ", " + COLUMN_SCORE + ", MAX(" + COLUMN_SCORE + ") as top_score" +
                "     FROM " + TABLE_SCORE +
                "     GROUP BY " + COLUMN_SCORE_ID + ", " + COLUMN_TIME + ", " + COLUMN_DIFFICULTY + ", " + COLUMN_SCORE +
                " ) as s ON s." + COLUMN_SCORE_ID + " = us." + COLUMN_SCORE_ID +
                " WHERE s." + COLUMN_DIFFICULTY + " IN ('Normal', 'Hard')" +
                " ORDER BY CASE WHEN s." + COLUMN_DIFFICULTY + " = 'Normal' THEN 1 ELSE 2 END, top_score DESC, s." + COLUMN_TIME + " ASC" +
                " LIMIT " + (limitPerDifficulty * 2);



        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
                int scoreId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE_ID));
                int time = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                String difficulty = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIFFICULTY));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
                userScores.add(new UserScore(userId, name, password, scoreId, time, difficulty, score));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userScores;
    }



    public List<Score> getScorePlayer(int playerId) {
        List<Score> filteredScores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT s.* FROM " + TABLE_SCORE + " s INNER JOIN " + TABLE_USER_SCORE + " us ON us." + COLUMN_SCORE_ID + " = s." + COLUMN_SCORE_ID +
                " WHERE us." + COLUMN_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(playerId)});

        if (cursor.moveToFirst()) {
            do {
                int time = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                String difficulty = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIFFICULTY));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
                Score scoreObject = new Score(time, difficulty, score);
                filteredScores.add(scoreObject);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return filteredScores;
    }





    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_PASSWORD, user.getPassword());

        db.insert(TABLE_USER, null, values);
        db.close();
    }


    public void createUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_PASSWORD, user.getPassword());;

        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getUserId());
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_PASSWORD, user.getPassword());

        db.update(TABLE_USER, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(user.getUserId())});
        db.close();
    }

    public void deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USER;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int userID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
                int playerScoreId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_SCORE));

                int playerScore = getUserScore(db, playerScoreId);

                User user = new User(userID, name, password);
                users.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return users;
    }

    private int getUserScore(SQLiteDatabase db, int userId) {
        int userScore = 0;
        String query = "SELECT " + COLUMN_SCORE + " FROM " + TABLE_SCORE + " s INNER JOIN " + TABLE_USER_SCORE + " us ON us." + COLUMN_SCORE_ID + " = s." + COLUMN_SCORE_ID +
                " WHERE us." + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            userScore = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
        }
        cursor.close();
        return userScore;
    }


    public int getUserIdByPlayerNameAndPassword(String playerName, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1;

        String query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USER +
                " WHERE " + COLUMN_NAME + " = ?" +
                " AND " + COLUMN_PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{playerName, password});

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
        }

        cursor.close();
        db.close();

        return userId;
    }

    public List<String> getAllUserNames() {
        List<String> userNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_USER_ID + ", " + COLUMN_NAME + " FROM " + TABLE_USER;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                userNames.add(userId + ": " + name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userNames;
    }

    public boolean hasExistingPlayer() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USER;
        Cursor cursor = db.rawQuery(query, null);
        boolean hasUsers = cursor.moveToFirst();
        cursor.close();
        db.close();
        return hasUsers;
    }

    public boolean isExistingPlayer(String playerName, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean exists = false;

        String query = "SELECT COUNT(*) FROM " + TABLE_USER +
                " WHERE " + COLUMN_NAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{playerName, password});

        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            exists = (count > 0);
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return exists;
    }
    public void setCurrentUser(Context context, int userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_CURRENT_USER_ID, userId);
        editor.apply();
    }

    public int getCurrentUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_CURRENT_USER_ID, -1);
    }

    public int getMostRecentUserId() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USER + " ORDER BY " + COLUMN_USER_ID + " ASC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return userId;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));

            user = new User(userId, name, password);
        }

        cursor.close();
        db.close();
        return user;
    }



}
