package edu.uga.cs.worldquiz;

import edu.uga.cs.worldquiz.DatabaseContract.CountryEntry;
import edu.uga.cs.worldquiz.DatabaseContract.QuizEntry;
import edu.uga.cs.worldquiz.DatabaseContract.ResultEntry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

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

    private static final String SQL_CREATE_QUIZZES =
            "CREATE TABLE " + QuizEntry.TABLE_NAME + " (" +
                    QuizEntry._ID + " INTEGER PRIMARY KEY," +
                    QuizEntry.COLUMN_NAME_TITLE + " TEXT," +
                    QuizEntry.COLUMN_NAME_DATE + " TEXT)";

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

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all tables
        db.execSQL(SQL_CREATE_COUNTRIES);
        db.execSQL(SQL_CREATE_QUIZZES);
        db.execSQL(SQL_CREATE_RESULTS);
    }

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
}