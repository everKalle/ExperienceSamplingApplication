package com.example.madiskar.experiencesamplingapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class QuestionnaireActivity extends AppCompatActivity {

    private String[] answers;
    private int currentQNumber = 1;
    private FragmentManager fragmentManager;
    private Question[] questions;
    private String currentQType;
    private long studyId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        Bundle extras = getIntent().getExtras();
        Questionnaire questionnaire = extras.getParcelable("QUESTIONNAIRE");

        studyId = questionnaire.getStudyId();
        questions = questionnaire.getQuestions();

        answers = new String[questions.length];
        for(int i = 0; i < questions.length; i++)
            answers[i] = "-";

        fragmentManager = getFragmentManager();

        final Button next = (Button) findViewById(R.id.nextquestionbutton);
        if(questions.length == 1)
            next.setText("Submit");
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentQType.equals("FREETEXT")){
                    String answer = ((EditText)findViewById(R.id.inputText)).getText().toString();
                    if(answer.equals(""))
                        answer = "User-did-not-answer";
                    else if(answer.contains(",")) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("\"").append(answer).append("\"");
                        answer = sb.toString();
                    }
                    addAnswer(currentQNumber - 1, answer.trim());

                } else if(currentQType.equals("MULTIPLECHOICE")) {
                    CheckBoxGroupView cbgv = (CheckBoxGroupView) findViewById(R.id.checkBoxGroup);
                    StringBuilder sb = new StringBuilder();
                    int[] checked = cbgv.getCheckedBoxes();
                    for(int i = 0; i < checked.length; i++) {
                        if ( (checked[i] == 1)) {
                            String answer = ((MultipleChoiceQuestion) questions[currentQNumber - 1]).getChoices()[i];
                            if(answer.contains(";"))
                                sb.append("\"").append(answer).append("\"").append(";");
                            else
                                sb.append(answer).append(";");
                        }
                    }
                    if(sb.length() != 0) {
                        sb.deleteCharAt(sb.lastIndexOf(";"));
                        addAnswer(currentQNumber-1, sb.toString().trim());
                    } else {
                        addAnswer(currentQNumber-1, "User-did-not-answer");
                    }

                } else if(currentQType.equals("MULTIPLECHOICE_RADIO")) {
                    RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroupSingle);
                    String answer;
                    int checkedId = rg.getCheckedRadioButtonId();
                    if(checkedId != -1) {
                        answer = ((MultipleChoiceQuestion) questions[ currentQNumber-1 ]).getChoices()[ checkedId-1 ];
                    } else {
                        answer = "User-did-not-answer";
                    }
                    if(answer.contains(",")) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("\"").append(answer).append("\"");
                        answer = sb.toString();
                    }
                    addAnswer(currentQNumber-1, answer.trim());
                }

                if(currentQNumber == answers.length-1) {
                    next.setText("Submit");
                } else if(currentQNumber == answers.length) {
                    Log.i("ANSWERS", "saving answers");
                    DBHandler mydb = DBHandler.getInstance(getApplicationContext());
                    mydb.insertAnswer(studyId, answers, DBHandler.calendarToString(Calendar.getInstance()));
                    /*ArrayList<String> test = mydb.getAnswers(studyId);
                    for(String s : test)
                        Log.i("TESTING ANSWERS", s);
                    */
                    finish();
                }
                currentQNumber ++;
                switchFragment(questions[currentQNumber-1], currentQNumber, questions.length);
            }
        });
        Button back = (Button) findViewById(R.id.previousquestionbutton);
        back.setVisibility(View.INVISIBLE);
        // TODO: Implement back button functionality

        switchFragment(questions[0], currentQNumber, questions.length);

    }

    private void addAnswer(int id, String answer) {
        answers[id] = answer;
    }


    private void switchFragment(Question q, int currentQNumber, int totalQuestions) {
        if (q instanceof FreeTextQuestion) {
            currentQType = "FREETEXT";
            Fragment fragment = new QuestionFragment();
            Bundle args = new Bundle();
            args.putString("text", q.getText());
            args.putString("type", "FREETEXT");
            args.putStringArray("options", new String [] {} );
            args.putInt("qNr", currentQNumber);
            args.putInt("totalNr", totalQuestions);
            fragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.question_content, fragment)
                    .commit();
        }
        else if (q instanceof MultipleChoiceQuestion) {
            Fragment fragment = new QuestionFragment();
            Bundle args = new Bundle();
            args.putString("text", q.getText());
            if(((MultipleChoiceQuestion) q).getSingleChoice() == 1) {
                currentQType = "MULTIPLECHOICE_RADIO";
                args.putString("type", "MULTIPLECHOICE_RADIO");
                args.putStringArray("options", ((MultipleChoiceQuestion) q).getChoices());
                args.putInt("qNr", currentQNumber);
                args.putInt("totalNr", totalQuestions);
            } else {
                currentQType = "MULTIPLECHOICE";
                args.putString("type", "MULTIPLECHOICE");
                args.putStringArray("options", ((MultipleChoiceQuestion) q).getChoices());
                args.putInt("qNr", currentQNumber);
                args.putInt("totalNr", totalQuestions);
            }
            fragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.question_content, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        //Disable back button
    }

}

