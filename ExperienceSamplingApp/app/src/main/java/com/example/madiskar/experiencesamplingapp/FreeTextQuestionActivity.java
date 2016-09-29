package com.example.madiskar.experiencesamplingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FreeTextQuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_free_text_layout);

        Bundle b = getIntent().getExtras();

        TextView textView = (TextView) findViewById(R.id.questionText);
        textView.setText(b.getString("QUESTION"));

        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        EditText editText = new EditText(this);
        editText.setLayoutParams(params);
        layout.addView(editText);
    }
}
