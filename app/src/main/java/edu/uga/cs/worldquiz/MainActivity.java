package edu.uga.cs.worldquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import edu.uga.cs.worldquiz.CountryDbHelper;
import edu.uga.cs.worldquiz.DatabaseContract.CountryEntry;
import edu.uga.cs.worldquiz.ImportCsvTask;



/**
 * Main activity class that initializes the database on app startup
 */
public class MainActivity extends AppCompatActivity implements ImportCsvTask.OnImportCompleteListener {
    private static final String TAG = "MainActivity";

    private CountryDbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting buttons to properly relocate users
        Button quizButton = findViewById(R.id.start_button);
        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, QuizScreen.class);
                startActivity(intent);
            }
        });

        Button resultsButton = findViewById(R.id.results_button);
        resultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ResultsScreen.class);
                startActivity(intent);
            }
        });


        // Initialize database
        initializeDatabase();

    }

    /**
     * Initialize the database, checking if it exists and creating it if necessary
     */
    private void initializeDatabase() {
        dbHelper = CountryDbHelper.getInstance(this);

        // Check if database needs initialization
        if (!dbHelper.isDatabaseExists() || !dbHelper.isDataLoaded()) {
            Log.d(TAG, "Database doesn't exist or is empty, initializing...");
            // Initialize database with CSV data
            new ImportCsvTask(this, this).execute();
        } else {
            Log.d(TAG, "Database already initialized");
            // Database already exists, proceed with app initialization
            onDatabaseReady();
        }
    }

    /**
     * Called when database import is complete
     */
    @Override
    public void onImportComplete(boolean success) {
        if (success) {
            Log.d(TAG, "Database initialized successfully");
            Toast.makeText(this, "Database initialized successfully", Toast.LENGTH_SHORT).show();
            onDatabaseReady();
        } else {
            Log.e(TAG, "Failed to initialize database");
            Toast.makeText(this, "Error initializing database", Toast.LENGTH_LONG).show();
            // Handle error - perhaps retry or show error message
        }
    }

    /**
     * Called when database is ready to use (either already existed or was just created)
     */
    private void onDatabaseReady() {
        // Continue with app initialization that requires database access
        // For example, load data for UI, etc.
    }

}

