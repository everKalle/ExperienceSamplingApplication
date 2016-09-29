package com.example.madiskar.experiencesamplingapp;

/**
 * Created by Joosep on 25.09.2016.
 */
public class MultipleChoiceQuestion extends Question {

    private String[] choices;

    public MultipleChoiceQuestion(String question, String[] choices) {
        super(question);
        this.choices = choices;
    }

    public String[] getChoices() {
        return choices;
    }

}
