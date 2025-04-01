package edu.uga.cs.worldquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

public class QuizScreen extends AppCompatActivity {

    private QuizViewModel viewModel;

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