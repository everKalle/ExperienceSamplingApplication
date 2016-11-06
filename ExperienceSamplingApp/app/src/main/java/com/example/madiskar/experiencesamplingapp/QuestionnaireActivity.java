package com.example.madiskar.experiencesamplingapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;


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
    private String token;
    private DBHandler mydb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        mydb = DBHandler.getInstance(getApplicationContext());

        SharedPreferences spref = getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
        token = spref.getString("token", "none");

        Bundle extras = getIntent().getExtras();
        Questionnaire questionnaire = extras.getParcelable("QUESTIONNAIRE");
        //TODO: get questions from database instead of Bundle

        final int notificationId = extras.getInt("notificationId");
        NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);

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
                if(currentQType.equals("FREETEXT")){ // REGULAR FREETEXT QUESTION
                    String answer = ((EditText)findViewById(R.id.inputText)).getText().toString();
                    if(answer.equals(""))
                        answer = "user-did-not-answer";
                    else {
                        answer = formatInput(answer.trim());
                    }
                    addAnswer(currentQNumber - 1, answer.trim());

                } else if(currentQType.equals("MULTIPLECHOICE")) { // REGULAR MULTICHOICE QUESTION WITH CHECKBOXES
                    CheckBoxGroupView cbgv = (CheckBoxGroupView) findViewById(R.id.checkBoxGroup);
                    StringBuilder sb = new StringBuilder();
                    int cCount = 0;
                    int[] checked = cbgv.getCheckedBoxes();
                    String[] multiChoices = ((MultipleChoiceQuestion) questions[currentQNumber - 1]).getChoices();
                    for(int i = 0; i < checked.length; i++) {
                        if ( (checked[i] == 1)) {
                            cCount ++;
                            String answer = multiChoices[i];
                            sb.append(formatInput(answer)).append(",");
                        }
                    }
                    if(sb.length() != 0) {
                        sb.deleteCharAt(sb.lastIndexOf(","));
                        if(cCount > 1) {
                            sb.insert(0, "\"");
                            sb.append("\"");
                        }
                        addAnswer(currentQNumber-1, sb.toString().trim());
                    } else {
                        addAnswer(currentQNumber-1, "user-did-not-answer");
                    }

                } else if(currentQType.equals("MULTIPLECHOICE_RADIO")) { // RADIO BUTTON MULTICHOICE QUESTION
                    RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroupSingle);
                    String answer;
                    int checkedId = rg.indexOfChild(findViewById(rg.getCheckedRadioButtonId()));
                    if(checkedId != -1) {
                        answer = ((MultipleChoiceQuestion) questions[ currentQNumber-1 ]).getChoices()[ checkedId ];
                    } else {
                        answer = "user-did-not-answer";
                    }
                    answer = formatInput(answer);
                    addAnswer(currentQNumber-1, answer.trim());
                }

                if(currentQNumber == answers.length-1) {
                    next.setText("Submit");
                } else if(currentQNumber == answers.length) {
                    saveAnswers(null);
                    finish();
                }
                if(currentQNumber != answers.length) {
                    currentQNumber++;
                    switchFragment(questions[currentQNumber - 1], currentQNumber, questions.length);
                }
            }
        });
        Button back = (Button) findViewById(R.id.previousquestionbutton);
        back.setVisibility(View.INVISIBLE); // TODO: Implement back button functionality

        Button cancel = (Button) findViewById(R.id.cancel_questionnaire_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnswers("user-cancelled-this-questionnaire");
                finish();
            }
        });

        switchFragment(questions[0], currentQNumber, questions.length);

    }


    private void saveAnswers(String alternative) {
        String answersAsString;
        if (alternative == null) {
            answersAsString = answersToString();
        } else {
            answersAsString = alternative;
        }

        SaveAnswersTask saveAnswersTask = new SaveAnswersTask(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                //Log.i("SERVER SAVE RESPONSE", output);
                if (output.equals("invalid_study")) {
                    Toast.makeText(getApplicationContext(), "This study no longer exists", Toast.LENGTH_LONG).show();
                } else if (output.equals("invalid_token")) {
                    Toast.makeText(getApplicationContext(), "Account authentication failed", Toast.LENGTH_LONG).show();
                } else if (output.equals("nothing")) {
                    Log.i("Answers to server: ", "Faulty query");
                } else if (output.equals("success")) {
                    Log.i("Answers to server: ", "Success");
                } else if (output.equals("saved-to-local")) {
                    Log.i("Answers to server: ", "Internet connection unavailable, saving to local storage");
                } else {
                    Log.i("Answers to server: ", "Something bad happened");
                }
            }
        }, isNetworkAvailable(), mydb);
        saveAnswersTask.execute(token, Long.toString(studyId), answersAsString);
        Log.i("SAVING ANSWERS", answersAsString);
    }


    private String answersToString() {
        StringBuilder sb = new StringBuilder();
        for(String s : answers)
            sb.append(s).append(";");
        sb.deleteCharAt(sb.lastIndexOf(";"));
        return sb.toString();
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


    public String formatInput(String input) {
        StringBuilder sb = new StringBuilder();

        String text = input;
        if(text.contains("\"")) {
            text = text.replaceAll("\"", "\"\"");
        }

        if(text.contains(",") || text.contains(";") || text.contains("\\n") || text.contains("\\t") || text.contains("\\b") || text.contains("\\f") || text.contains("\\r")) {
            sb.append("\"").append(text).append("\"");
        } else {
            sb.append(text);
        }
        return sb.toString();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


}
