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

public class QuizViewModel extends AndroidViewModel {
    // Current score
    private MutableLiveData<Integer> score = new MutableLiveData<>(0);
    public LiveData<Integer> getScore() {
        return score;
    }

    // Current question index
    private MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>(0);
    public LiveData<Integer> getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    // Questions list
    private List<Question> questions;
    private Map<String, String> countryContinentPairs;

    // Database helper
    private CountryDbHelper dbHelper;

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

    public List<Question> getQuestions() {
        return questions;
    }

    // Navigate to next question
    public void nextQuestion() {
        currentQuestionIndex.setValue(currentQuestionIndex.getValue() + 1);
    }

    // Update score when answer is correct
    public void updateScore() {
        score.setValue(score.getValue() + 1);
    }

    // Check if the quiz is complete
    public boolean isQuizComplete() {
        return currentQuestionIndex.getValue() >= questions.size();
    }

    public void startNewQuiz() {
        score.setValue(0); // reset score
        currentQuestionIndex.setValue(0);

        countryContinentPairs = dbHelper.getRandomCountryContinentPairs(6); // get new questions/pairs

        questions.clear();
        createCountryQuestions();
    }

    public void previousQuestion() {
        Integer index = currentQuestionIndex.getValue();
        if (index != null && index > 0) {
            currentQuestionIndex.setValue(index - 1);
        }
    }

}