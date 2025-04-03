package edu.uga.cs.worldquiz;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Iterator;

/**
 * ViewModel class for the Quiz functionality.
 * Manages the quiz questions, score, and current question index.
 * This class is responsible for maintaining quiz state and providing
 * data to the UI components through LiveData objects.
 */
public class QuizViewModel extends AndroidViewModel {
    /**
     * LiveData for the current score.
     * Stores the user's current quiz score, which is incremented for each correct answer.
     */
    private MutableLiveData<Integer> score = new MutableLiveData<>(0);

    /**
     * Returns the LiveData for the current score.
     * @return LiveData containing the current score.
     */
    public LiveData<Integer> getScore() {
        return score;
    }

    /**
     * LiveData for the current question index.
     * Tracks which question the user is currently viewing.
     */
    private MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>(0);

    /**
     * Returns the LiveData for the current question index.
     * @return LiveData containing the current question index.
     */
    public LiveData<Integer> getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    /**
     * List of quiz questions.
     * Contains the Question objects generated for the current quiz session.
     */
    private List<Question> questions;

    /**
     * Map of country-continent pairs used to generate questions.
     * Keys are country names and values are the corresponding continent names.
     */
    private Map<String, String> countryContinentPairs;

    /**
     * Database helper instance.
     * Provides access to the database containing country and continent information.
     */
    private CountryDbHelper dbHelper;

    /**
     * Constructor for the QuizViewModel.
     * Initializes the database helper, retrieves random country-continent pairs, and creates the quiz questions.
     * @param application The application instance.
     */
    public QuizViewModel(Application application) {
        super(application);

        // Initialize the database helper with application context
        dbHelper = CountryDbHelper.getInstance(application);

        // Get random country-continent pairs
        countryContinentPairs = dbHelper.getRandomCountryContinentPairs(6);

        // Initialize the questions list
        questions = new ArrayList<>();

        // Create country-continent questions
        createCountryQuestions();
    }

    /**
     * Creates the country-continent questions based on the retrieved country-continent pairs.
     * For each country, generates a question asking which continent it belongs to,
     * with one correct answer and two random incorrect continent options.
     */
    private void createCountryQuestions() {
        // Create questions based on the country-continent pairs
        String[] continents = {"Africa", "Antarctica", "Asia", "Oceania", "Europe", "North America", "South America"};
        Random random = new Random();

        // For each country-continent pair
        Iterator<Map.Entry<String, String>> iterator = countryContinentPairs.entrySet().iterator();
        while (iterator.hasNext() && questions.size() < 6) {
            Map.Entry<String, String> entry = iterator.next();
            String country = entry.getKey();
            String correctContinent = entry.getValue();

            // Create answer options (including the correct continent)
            List<String> options = new ArrayList<>();
            options.add(correctContinent);

            // Add 2 random wrong continents
            while (options.size() < 3) {
                String randomContinent = continents[random.nextInt(continents.length)];
                if (!randomContinent.equals(correctContinent) && !options.contains(randomContinent)) {
                    options.add(randomContinent);
                }
            }

            // Shuffle the options
            java.util.Collections.shuffle(options);

            // Find the index of the correct answer
            int correctAnswerIndex = options.indexOf(correctContinent);

            // Format options with numbering
            for (int i = 1; i < options.size() + 1; i++) {
                String newString = i + ". " + options.get(i-1);
                options.set(i-1, newString);
            }

            // Create and add the question
            Question question = new Question(
                    "In which continent is " + country + " located?",
                    options,
                    correctAnswerIndex
            );

            questions.add(question);
        }
    }

    /**
     * Returns the list of quiz questions.
     * @return List of Question objects for the current quiz.
     */
    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * Navigates to the next question in the quiz.
     * Increments the currentQuestionIndex to move forward in the quiz sequence.
     */
    public void nextQuestion() {
        currentQuestionIndex.setValue(currentQuestionIndex.getValue() + 1);
    }

    /**
     * Updates the score when an answer is correct.
     * Increments the current score by 1 point.
     */
    public void updateScore() {
        score.setValue(score.getValue() + 1);
    }

    /**
     * Checks if the quiz is complete (all questions have been answered).
     * @return True if the quiz is complete, false otherwise.
     */
    public boolean isQuizComplete() {
        return currentQuestionIndex.getValue() >= questions.size();
    }

    /**
     * Starts a new quiz by resetting the score, question index, and generating new questions.
     * This method can be called to restart the quiz or start a fresh quiz.
     */
    public void startNewQuiz() {
        score.setValue(0); // reset score
        currentQuestionIndex.setValue(0);

        countryContinentPairs = dbHelper.getRandomCountryContinentPairs(6); // get new questions/pairs

        questions.clear();
        createCountryQuestions();
    }

    /**
     * Navigates to the previous question in the quiz, if available.
     * Decrements the currentQuestionIndex to move backward in the quiz sequence.
     * Will not go below zero (the first question).
     */
    public void previousQuestion() {
        Integer index = currentQuestionIndex.getValue();
        if (index != null && index > 0) {
            currentQuestionIndex.setValue(index - 1);
        }
    }
}