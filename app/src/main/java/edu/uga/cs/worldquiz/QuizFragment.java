package edu.uga.cs.worldquiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
//        Button option1 = view.findViewById(R.id.option_1);
//        Button option2 = view.findViewById(R.id.option_2);
//        Button option3 = view.findViewById(R.id.option_3);
//        Button option4 = view.findViewById(R.id.option_4);
//
//        List<Button> optionButtons = Arrays.asList(option1, option2, option3, option4);
//
//        // Set the text for each option button
//        List<String> options = question.getOptions();
//        for (int i = 0; i < options.size(); i++) {
//            optionButtons.get(i).setText(options.get(i));
//
//            final int optionIndex = i;
//            optionButtons.get(i).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (optionIndex == question.getCorrectAnswerIndex()) {
//                        viewModel.updateScore();
//                    }
//
//                    // Move to next question
//                    viewModel.nextQuestion();
//
//                    if (!viewModel.isQuizComplete()) {
//                        // Navigate to the next question fragment
//                        getParentFragmentManager().beginTransaction()
//                                .replace(R.id.fragment_container,
//                                        newInstance(questionIndex + 1))
//                                .addToBackStack(null)
//                                .commit();
//                    } else {
//                        // Show results
//                        showResults();
//                    }
//                }
//            });
//        }

        // set up radio buttons
        RadioGroup optionGroup = view.findViewById(R.id.option_group);
        List<String> options = question.getOptions();
        RadioButton[] buttons = {
                view.findViewById(R.id.option_1),
                view.findViewById(R.id.option_2),
                view.findViewById(R.id.option_3)
        };

        for (int i = 0; i < options.size(); i++) {
            buttons[i].setText(options.get(i));
        }

        // add swipe detection
        view.setOnTouchListener(new OnSwipeTouchListener(requireContext()) {
            @Override
            public void onSwipeLeft() {
                // Go forward only if an option is selected
                RadioGroup optionGroup = view.findViewById(R.id.option_group);
                int selectedId = optionGroup.getCheckedRadioButtonId();
                if (selectedId == -1) return;

                int selectedIndex = -1;
                for (int i = 0; i < buttons.length; i++) { // finds selected answer
                    if (buttons[i].getId() == selectedId) {
                        selectedIndex = i;
                        break;
                    }
                }

                if (selectedIndex == question.getCorrectAnswerIndex()) { // updates score if answer is correct
                    viewModel.updateScore();
                }

                viewModel.nextQuestion();

                if (!viewModel.isQuizComplete()) { // checks if quiz is complete
                    getParentFragmentManager().beginTransaction() // move to next question
                            .replace(R.id.fragment_container, newInstance(questionIndex + 1))
                            .addToBackStack(null)
                            .commit();
                } else {
                    showResults();
                }
            }

            @Override
            public void onSwipeRight() {
                if (questionIndex > 0) {
                    // goes back one question
                    viewModel.previousQuestion();
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, newInstance(questionIndex - 1))
                            .addToBackStack(null)
                            .commit();
                } // if
            } // onSwipeRight
        });

        // Display current question number out of total
        TextView questionCounter = view.findViewById(R.id.question_counter);
        questionCounter.setText("Question " + (questionIndex + 1) + "/" +
                viewModel.getQuestions().size());
    }

    private void showResults() {
        CountryDbHelper dbHelper = CountryDbHelper.getInstance(requireContext());
        int score = viewModel.getScore().getValue() != null ? viewModel.getScore().getValue() : 0;
        dbHelper.insertQuizResult(score);

        // Navigate to results fragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ResultsFragment())
                .commit();
    }
}