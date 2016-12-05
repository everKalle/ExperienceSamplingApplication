package com.example.madiskar.experiencesamplingapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;


import java.util.ArrayList;

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
        studyId = extras.getLong("StudyId");

        try {
            final int notificationId = extras.getInt("notificationId");
            NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notificationId);
        } catch (Exception e) {
            // do nothing
        }

        ArrayList<Question> questionsArr = mydb.getStudyQuestions(studyId);

        questions = new Question[questionsArr.size()];
        answers = new String[questions.length];

        for(int j = 0; j < questions.length; j++) {
            questions[j] = questionsArr.get(j);
            answers[j] = "-";
        }


        fragmentManager = getFragmentManager();

        final Button next = (Button) findViewById(R.id.nextquestionbutton);
        if(questions.length == 1)
            next.setText(R.string.submit);
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
                    next.setText(R.string.submit);
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
        back.setVisibility(View.INVISIBLE);
        final Context mContext = this;

        Button cancel = (Button) findViewById(R.id.cancel_questionnaire_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setTitle(getString(R.string.cancel_questionnaire));
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveAnswers("user-cancelled-this-questionnaire");
                        dialog.dismiss();
                        finish();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
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

        SaveAnswersTask saveAnswersTask = new SaveAnswersTask(token, Long.toString(studyId), answersAsString, isNetworkAvailable(), mydb, new RunnableResponse() {
            @Override
            public void processFinish(String output) {
            }
        });
        ExecutorSupplier.getInstance().forBackgroundTasks().execute(saveAnswersTask);
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
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
