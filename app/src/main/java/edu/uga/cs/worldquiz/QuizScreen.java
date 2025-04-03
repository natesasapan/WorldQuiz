package edu.uga.cs.worldquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

/**
 * The quiz screen.
 */
public class QuizScreen extends AppCompatActivity {

    private QuizViewModel viewModel;

    /**
     * Called when activity is created.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_screen);

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        // Start with the first question
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, QuizFragment.newInstance(0))
                    .commit();
        }
    }
}