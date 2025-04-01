package edu.uga.cs.worldquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultsScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_screen);

        // This activity doesn't do anything yet - it just exists

        TextView resultHistoryView = findViewById(R.id.result_history);

        new AsyncTask<Void, Void, String>() { // fetches quiz results from the database
            @Override
            protected String doInBackground(Void... voids) { // stores all the quiz results in a string
                CountryDbHelper dbHelper = CountryDbHelper.getInstance(ResultsScreen.this);
                StringBuilder resultBuilder = new StringBuilder();

                for (String result : dbHelper.getAllQuizResults()) {
                    resultBuilder.append(result).append("\n");
                }

                return resultBuilder.toString();
            }

            @Override
            protected void onPostExecute(String resultText) {
                resultHistoryView.setText(resultText.isEmpty() ? "No past results yet." : resultText);
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