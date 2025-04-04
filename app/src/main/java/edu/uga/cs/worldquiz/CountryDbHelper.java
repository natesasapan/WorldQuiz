package edu.uga.cs.worldquiz;

import edu.uga.cs.worldquiz.DatabaseContract.CountryEntry;
import edu.uga.cs.worldquiz.DatabaseContract.QuizEntry;
import edu.uga.cs.worldquiz.DatabaseContract.ResultEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

/**
 * SQLite database helper class to manage database creation and version management
 */
public class CountryDbHelper extends SQLiteOpenHelper {
    // Database version and name constants
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Countries.db";

    // Singleton instance
    private static CountryDbHelper instance;
    private Context context;

    // SQL statements for table creation
    private static final String SQL_CREATE_COUNTRIES =
            "CREATE TABLE " + CountryEntry.TABLE_NAME + " (" +
                    CountryEntry._ID + " INTEGER PRIMARY KEY," +
                    CountryEntry.COLUMN_NAME_COUNTRY + " TEXT," +
                    CountryEntry.COLUMN_NAME_CONTINENT + " TEXT)";

    // SQL statements for creating quizzes table
    private static final String SQL_CREATE_QUIZZES =
            "CREATE TABLE " + QuizEntry.TABLE_NAME + " (" +
                    QuizEntry._ID + " INTEGER PRIMARY KEY," +
                    QuizEntry.COLUMN_NAME_TITLE + " TEXT," +
                    QuizEntry.COLUMN_NAME_DATE + " TEXT)";

    // SQL statements for results table
    private static final String SQL_CREATE_RESULTS =
            "CREATE TABLE " + ResultEntry.TABLE_NAME + " (" +
                    ResultEntry._ID + " INTEGER PRIMARY KEY," +
                    ResultEntry.COLUMN_NAME_QUIZ_ID + " INTEGER," +
                    ResultEntry.COLUMN_NAME_SCORE + " INTEGER," +
                    ResultEntry.COLUMN_NAME_DATE + " TEXT," +
                    "FOREIGN KEY (" + ResultEntry.COLUMN_NAME_QUIZ_ID + ") REFERENCES " +
                    QuizEntry.TABLE_NAME + "(" + QuizEntry._ID + "))";

    // SQL statements for table deletion
    private static final String SQL_DELETE_COUNTRIES =
            "DROP TABLE IF EXISTS " + CountryEntry.TABLE_NAME;

    private static final String SQL_DELETE_QUIZZES =
            "DROP TABLE IF EXISTS " + QuizEntry.TABLE_NAME;

    private static final String SQL_DELETE_RESULTS =
            "DROP TABLE IF EXISTS " + ResultEntry.TABLE_NAME;

    /**
     * Get singleton instance of the database helper
     * @param context Application context
     * @return Instance of CountryDbHelper
     */
    public static synchronized CountryDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CountryDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Private constructor to enforce singleton pattern
     * @param context Application context
     */
    private CountryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Executes SQL statements.
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all tables
        db.execSQL(SQL_CREATE_COUNTRIES);
        db.execSQL(SQL_CREATE_QUIZZES);
        db.execSQL(SQL_CREATE_RESULTS);
    }

    /**
     * Called when the database needs to be changed and updated.
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables and recreate them
        db.execSQL(SQL_DELETE_RESULTS);
        db.execSQL(SQL_DELETE_QUIZZES);
        db.execSQL(SQL_DELETE_COUNTRIES);
        onCreate(db);
    }

    /**
     * Check if the database file exists
     * @return true if database exists, false otherwise
     */
    public boolean isDatabaseExists() {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    /**
     * Check if data is already loaded in the countries table
     * @return true if data exists, false otherwise
     */
    public boolean isDataLoaded() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + CountryEntry.TABLE_NAME, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    /**
     * Get random country-continent pairs
     * @param count Number of random countries to retrieve
     * @return A Map with country names as keys and their continents as values
     */
    public Map<String, String> getRandomCountryContinentPairs(int count) {
        Map<String, String> countryContinentMap = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // We need both country and continent columns
        String[] projection = {
                CountryEntry.COLUMN_NAME_COUNTRY,
                CountryEntry.COLUMN_NAME_CONTINENT
        };

        // Get random countries
        Cursor cursor = db.query(
                CountryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                "RANDOM()",
                String.valueOf(count)
        );

        try {
            // Process all rows
            while (cursor != null && cursor.moveToNext()) {
                // Get column indices
                int nameIndex = cursor.getColumnIndexOrThrow(CountryEntry.COLUMN_NAME_COUNTRY);
                int continentIndex = cursor.getColumnIndexOrThrow(CountryEntry.COLUMN_NAME_CONTINENT);

                // Extract data
                String countryName = cursor.getString(nameIndex);
                String continent = cursor.getString(continentIndex);

                // Add to map (country name as key, continent as value)
                countryContinentMap.put(countryName, continent);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return countryContinentMap;
    }

    /**
     * Retrieves all quiz results from the database in descending order by date.
     * @return A list of formatted strings, each resulting in a past quiz result.
     */
    public List<String> getAllQuizResults() {
        List<String> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT r.score, r.date FROM " + DatabaseContract.ResultEntry.TABLE_NAME + " r ORDER BY r.date DESC",
                null
        );

        while (cursor.moveToNext()) {
            int score = cursor.getInt(0);
            String date = cursor.getString(1);
            results.add("Date: " + date + " | Score: " + score + "/6");
        }

        cursor.close();
        return results;
    }

    /**
     * Inserts a new quiz result into the database.
     * The method stores both a quiz entry and a corresponding result entry.
     * @param score The final score achieved in the quiz (out of 6).
     */
    public void insertQuizResult(int score) {
        SQLiteDatabase db = this.getWritableDatabase();

        // gets the date and time of the quiz completion and formats it
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        // inserts to the quizzes table
        ContentValues quizValues = new ContentValues();
        quizValues.put(DatabaseContract.QuizEntry.COLUMN_NAME_TITLE, "Quiz");
        quizValues.put(DatabaseContract.QuizEntry.COLUMN_NAME_DATE, date);
        long quizId = db.insert(DatabaseContract.QuizEntry.TABLE_NAME, null, quizValues);

        // inserts to the results table
        ContentValues resultValues = new ContentValues();
        resultValues.put(DatabaseContract.ResultEntry.COLUMN_NAME_QUIZ_ID, quizId);
        resultValues.put(DatabaseContract.ResultEntry.COLUMN_NAME_SCORE, score);
        resultValues.put(DatabaseContract.ResultEntry.COLUMN_NAME_DATE, date);
        db.insert(DatabaseContract.ResultEntry.TABLE_NAME, null, resultValues);
    }

}