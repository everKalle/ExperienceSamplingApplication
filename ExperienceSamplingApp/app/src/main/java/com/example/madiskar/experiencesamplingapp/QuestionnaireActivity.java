package com.example.madiskar.experiencesamplingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class QuestionnaireActivity extends AppCompatActivity {

    private ArrayList<TextView> textViews;
    private ArrayList<EditText> editTexts = new ArrayList<>();
    RadioButton[] radioButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionnaire_layout);

        Bundle extras = getIntent().getExtras();
        Questionnaire questionnaire = (Questionnaire) extras.getParcelable("QUESTIONNAIRE");

        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (Question q : questionnaire.getQuestions()) {
            try {
                Log.v("LAASDADKOAKDOAKS", q.getText());
            } catch (Exception e) {
                Log.v("NULLLLL", "lape");
            }
        }

        FreeTextQuestion[] freeTextQuestions = questionnaire.getFreeTextQuestions();
        MultipleChoiceQuestion[] multipleChoiceQuestions = questionnaire.getMultipleChoiceQuestions();

        Log.v("SUUR POISS 1", String.valueOf(freeTextQuestions.length));
        Log.v("Suur POiss 2",  String.valueOf(multipleChoiceQuestions.length));

        // Järjestame küsimused õigesti:

        Question[] orderedQuestions = new Question[questionnaire.getQuestions().length];

        int freeCounter = 0;
        int multipleCounter = 0;
        for (int i = 0; i < questionnaire.getQuestions().length; i++) {
            if (questionnaire.getQuestions()[i].getText().equals(freeTextQuestions[freeCounter].getText()))
                orderedQuestions[i] = freeTextQuestions[freeCounter++];
            else
                orderedQuestions[i] = multipleChoiceQuestions[multipleCounter++];
            Log.v("TESTING BIG BOI", questionnaire.getQuestions()[i].getText());
        }

        for (int i = 0; i < orderedQuestions.length; i++)
            Log.v("kyss", orderedQuestions[i].getText());

        for (Question q: orderedQuestions) {
            if (q instanceof FreeTextQuestion) {
                TextView textView = new TextView(this);
                textView.setText(q.getText());
                layout.addView(textView);
                EditText editText = new EditText(this);
                editText.setLayoutParams(params);
                layout.addView(editText);
                editTexts.add(editText);
            }
            else {
                MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;
                RadioGroup rg = new RadioGroup(this);
                for (int k = 0; k < mcq.getChoices().length; k++) {
                    radioButtons[k] = new RadioButton(this);
                    radioButtons[k].setText(mcq.getChoices()[k]);
                    rg.addView(radioButtons[k]);
                }
            }
        }
    }
}
