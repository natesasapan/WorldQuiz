package edu.uga.cs.worldquiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Arrays;
import java.util.List;

public class QuizFragment extends Fragment {
    private QuizViewModel viewModel;
    private int questionIndex;

    private static final String ARG_QUESTION_INDEX = "question_index";

    public static QuizFragment newInstance(int questionIndex) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_QUESTION_INDEX, questionIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionIndex = getArguments().getInt(ARG_QUESTION_INDEX, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quiz_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);

        // Check if we've reached the end of the quiz
        if (viewModel.isQuizComplete()) {
            showResults();
            return;
        }

        Question question = viewModel.getQuestions().get(questionIndex);

        // Set up the question text
        TextView questionText = view.findViewById(R.id.question_text);
        questionText.setText(question.getQuestionText());

        // Set up the answer buttons
        Button option1 = view.findViewById(R.id.option_1);
        Button option2 = view.findViewById(R.id.option_2);
        Button option3 = view.findViewById(R.id.option_3);
        Button option4 = view.findViewById(R.id.option_4);

        List<Button> optionButtons = Arrays.asList(option1, option2, option3, option4);

        // Set the text for each option button
        List<String> options = question.getOptions();
        for (int i = 0; i < options.size(); i++) {
            optionButtons.get(i).setText(options.get(i));

            final int optionIndex = i;
            optionButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (optionIndex == question.getCorrectAnswerIndex()) {
                        viewModel.updateScore();
                    }

                    // Move to next question
                    viewModel.nextQuestion();

                    if (!viewModel.isQuizComplete()) {
                        // Navigate to the next question fragment
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container,
                                        newInstance(questionIndex + 1))
                                .addToBackStack(null)
                                .commit();
                    } else {
                        // Show results
                        showResults();
                    }
                }
            });
        }

        // Display current question number out of total
        TextView questionCounter = view.findViewById(R.id.question_counter);
        questionCounter.setText("Question " + (questionIndex + 1) + "/" +
                viewModel.getQuestions().size());
    }

    private void showResults() {
        // Navigate to results fragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ResultsFragment())
                .commit();
    }
}