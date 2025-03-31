package edu.uga.cs.worldquiz;

import android.provider.BaseColumns;

/**
 * Contract class that defines the database schema
 */
public class DatabaseContract {
    // Private constructor to prevent instantiation
    private DatabaseContract() {}

    /**
     * Inner class defining the countries table schema
     */
    public static class CountryEntry implements BaseColumns {
        public static final String TABLE_NAME = "countries";
        public static final String COLUMN_NAME_COUNTRY = "country";
        public static final String COLUMN_NAME_CONTINENT = "continent";
    }

    /**
     * Inner class defining the quizzes table schema
     */
    public static class QuizEntry implements BaseColumns {
        public static final String TABLE_NAME = "quizzes";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DATE = "date";

    }

    /**
     * Inner class defining the results table schema
     */
    public static class ResultEntry implements BaseColumns {
        public static final String TABLE_NAME = "results";
        public static final String COLUMN_NAME_QUIZ_ID = "quiz_id";
        public static final String COLUMN_NAME_SCORE = "score";
        public static final String COLUMN_NAME_DATE = "date";

    }
}