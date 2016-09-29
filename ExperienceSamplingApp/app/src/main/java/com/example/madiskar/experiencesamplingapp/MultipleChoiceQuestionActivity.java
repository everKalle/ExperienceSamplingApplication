package com.example.madiskar.experiencesamplingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MultipleChoiceQuestionActivity extends AppCompatActivity {

    private RadioButton[] radioButtons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_multiple_choice_layout);

        Bundle b = getIntent().getExtras();

        TextView textView = (TextView) findViewById(R.id.questionText);
        textView.setText(b.getString("QUESTION"));

        String[] choices = b.getStringArray("CHOICES");

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        //LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);

        radioButtons = new RadioButton[choices.length];

        for (int i = 0; i < choices.length; i++) {
            radioButtons[i] = new RadioButton(this);
            radioButtons[i].setText(choices[i]);
            radioGroup.addView(radioButtons[i]);
        }
    }
}
