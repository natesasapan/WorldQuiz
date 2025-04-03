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

/**
 * Fragment that shows quiz results.
 */
public class ResultsFragment extends Fragment {
    private QuizViewModel viewModel;

    /**
     * Inflates the results screen layout.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.results_fragment, container, false);
    }

    /**
     * Initializes UI elements and logic.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
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

        // "View All Results" button
        Button viewAllResultsButton = view.findViewById(R.id.view_all_results_button);
        viewAllResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ResultsScreen activity
                Intent intent = new Intent(requireActivity(), ResultsScreen.class);
                startActivity(intent);
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