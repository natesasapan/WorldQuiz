package edu.uga.cs.worldquiz;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ResultsFragment extends Fragment {
    private QuizViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.results_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);

        // Display the final score
        TextView resultText = view.findViewById(R.id.result_text);
        resultText.setText("You scored " + viewModel.getScore().getValue() +
                " out of " + viewModel.getQuestions().size() + "!");

        // Restart button
        Button restartButton = view.findViewById(R.id.restart_button);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.startNewQuiz(); // start a new quiz

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new QuizFragment())
                        .commit();
            }
        });

        // home button
        Button homeButton = view.findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back to MainActivity
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish(); // finish the current activity
            }
        });

    }
}