package edu.uga.cs.worldquiz;

import java.util.List;

/**
 * Quiz questions.
 */
public class Question {
    private String questionText;
    private List<String> options;
    private int correctAnswerIndex;

    /**
     * Creates a new question object.
     * @param questionText The question.
     * @param options List of answer/option choices.
     * @param correctAnswerIndex Index of the correct answer.
     */
    public Question(String questionText, List<String> options, int correctAnswerIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    /**
     * Gets the question text.
     * @return The question text.
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * Gets list of answer choices.
     * @return The list of answer choices.
     */
    public List<String> getOptions() {
        return options;
    }

    /**
     * Gets the index of the correct answer.
     * @return The index of the correct answer.
     */
    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
}