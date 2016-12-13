package com.example.madiskar.experiencesamplingapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.madiskar.experiencesamplingapp.custom_views.CheckBoxGroupView;
import com.example.madiskar.experiencesamplingapp.R;


public class QuestionFragment extends Fragment {

    private String qType;

    public QuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        qType = args.getString("type");
        int qNr = args.getInt("qNr");
        int totalNr = args.getInt("totalNr");
        String text = args.getString("text");
        String [] choices = args.getStringArray("options");
        getActivity().setTitle("Question " + qNr + "/" + totalNr);
        View v;

        if(qType.equals("FREETEXT")) {
            v = inflater.inflate(R.layout.fragment_freetextquestion, container, false);
            prepareFreeTextLayout(v, text);
        }
        else if(qType.equals("MULTIPLECHOICE")) {
            v = inflater.inflate(R.layout.fragment_multiplechoicequestion, container, false);
            prepareMultipleChoiceLayout(v, text, false, choices);
        }
        else {
            v = inflater.inflate(R.layout.fragment_radiomultiplechoicequestion, container, false);
            prepareMultipleChoiceLayout(v, text, true, choices);
        }
        return v;
    }


    private void prepareFreeTextLayout(View view, String text) {
        TextView textfield = (TextView) view.findViewById(R.id.questionText_FreeText);
        textfield.setText(text);
    }


    private void prepareMultipleChoiceLayout(View view, String text, boolean singleChoice, String[] choices) {

        if(singleChoice) {
            TextView textfield = (TextView) view.findViewById(R.id.questionText_Single);
            textfield.setText(text);
            RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupSingle);

            for (int i = 0; i < choices.length; i++) {
                RadioButton rb = new RadioButton(view.getContext());
                rb.setText(choices[i]);
                rb.setId(i);
                radioGroup.addView(rb);
            }
        } else {
            TextView textfield = (TextView) view.findViewById(R.id.questionText_Multi);
            textfield.setText(text);
            CheckBoxGroupView checkGroup = (CheckBoxGroupView) view.findViewById(R.id.checkBoxGroup);

            for (int i = 0; i < choices.length; i++) {
                CheckBox cb = new CheckBox(view.getContext());
                cb.setTag(i);
                cb.setText(choices[i]);
                checkGroup.put(cb);
            }
        }
    }
}
