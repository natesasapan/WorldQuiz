package edu.uga.cs.worldquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Past results page.
 */
public class ResultsScreen extends AppCompatActivity {

    /**
     * Sets up UI and loads quiz results if there is any.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_screen);

        // This activity doesn't do anything yet - it just exists

        TextView resultHistoryView = findViewById(R.id.result_history);

        new AsyncTask<Void, Void, String>() { // fetches quiz results from the database

            /**
             * Background thread.
             * @param voids The parameters of the task.
             *
             * @return A string with past quiz results.
             */
            @Override
            protected String doInBackground(Void... voids) { // stores all the quiz results in a string
                CountryDbHelper dbHelper = CountryDbHelper.getInstance(ResultsScreen.this);
                StringBuilder resultBuilder = new StringBuilder();

                for (String result : dbHelper.getAllQuizResults()) {
                    resultBuilder.append(result).append("\n");
                }

                return resultBuilder.toString();
            }

            /**
             * Displays quiz results.
             * @param resultText The result of the operation computed by {@link #doInBackground}.
             *
             */
            @Override
            protected void onPostExecute(String resultText) {
                resultHistoryView.setText(resultText.isEmpty() ? "No results yet." : resultText);
            }
        }.execute();


        // home button
        Button homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back to MainActivity
                Intent intent = new Intent(ResultsScreen.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }); // home button on click listener
    }
}