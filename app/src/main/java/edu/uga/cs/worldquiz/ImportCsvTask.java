package edu.uga.cs.worldquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import edu.uga.cs.worldquiz.DatabaseContract.CountryEntry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * AsyncTask to import CSV data into SQLite database without blocking the main thread
 */
public class ImportCsvTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "ImportCsvTask";

    private Context context;
    private CountryDbHelper dbHelper;
    private OnImportCompleteListener listener;

    /**
     * Interface for callbacks when import is complete
     */
    public interface OnImportCompleteListener {
        void onImportComplete(boolean success);
    }

    /**
     * Constructor
     * @param context Application context
     */
    public ImportCsvTask(Context context) {
        this.context = context;
        this.dbHelper = CountryDbHelper.getInstance(context);
    }

    /**
     * Constructor with listener for completion callback
     * @param context Application context
     * @param listener Callback listener
     */
    public ImportCsvTask(Context context, OnImportCompleteListener listener) {
        this.context = context;
        this.dbHelper = CountryDbHelper.getInstance(context);
        this.listener = listener;
    }

    /**
     * Imports the CSV files in the background.
     * @param voids The parameters of the task.
     *
     * @return true if the import was successful, false otherwise
     */
    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Import country-continent data first
            boolean countriesImported = importCountryContinentData(db);

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error importing CSV", e);
            return false;
        }
    }

    /**
     * Import country and continent data from CSV
     * @param db SQLiteDatabase instance
     * @return true if successful, false otherwise
     */
    private boolean importCountryContinentData(SQLiteDatabase db) {
        try {
            // Read country_continent.csv file from assets
            InputStream is = context.getAssets().open("country_continent.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            db.beginTransaction();

            try {

                while ((line = reader.readLine()) != null) {
                    String[] row = line.split(",");
                    if (row.length >= 2) {
                        ContentValues values = new ContentValues();
                        values.put(CountryEntry.COLUMN_NAME_COUNTRY, row[0].trim());
                        values.put(CountryEntry.COLUMN_NAME_CONTINENT, row[1].trim());

                        long newRowId = db.insert(CountryEntry.TABLE_NAME, null, values);

                        if (newRowId == -1) {
                            Log.e(TAG, "Error inserting country: " + row[0]);
                        }
                    }
                }
                db.setTransactionSuccessful();
                return true;
            } finally {
                db.endTransaction();
                reader.close();
                is.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error importing country-continent data", e);
            return false;
        }
    }

    /**
     * Called when the background tasks completes.
     * @param success The result of the operation computed by {@link #doInBackground}.
     *
     */
    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            Log.d(TAG, "CSV import completed successfully");
        } else {
            Log.e(TAG, "CSV import failed");
        }

        // Notify listener if it exists
        if (listener != null) {
            listener.onImportComplete(success);
        }
    }
}