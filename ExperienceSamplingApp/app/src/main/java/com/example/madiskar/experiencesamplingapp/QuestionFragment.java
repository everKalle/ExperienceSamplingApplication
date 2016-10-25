package com.example.madiskar.experiencesamplingapp;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */

public class QuestionFragment extends Fragment {

    private String qType;
    //private OnFragmentInteractionListener mListener;

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

        //Log.i(this.getClass().toString(), qType);
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
        //Log.i(this.getClass().toString(), "Freetext layoutis");
        TextView textfield = (TextView) view.findViewById(R.id.questionText_FreeText);
        textfield.setText(text);
    }


    private void prepareMultipleChoiceLayout(View view, String text, boolean singleChoice, String[] choices) {
        //Log.i(this.getClass().toString(), "Multichoice layoutis");

        if(singleChoice) {
            TextView textfield = (TextView) view.findViewById(R.id.questionText_Single);
            textfield.setText(text);
            RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupSingle);

            //Log.i("Radiobuttons", "Creating radiobuttons " + choices.length);
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

            //Log.i("Checkboxes", "Creating checkboxes " + choices.length);
            for (int i = 0; i < choices.length; i++) {
                CheckBox cb = new CheckBox(view.getContext());
                cb.setTag(i);
                cb.setText(choices[i]);
                checkGroup.put(cb);
            }
        }
    }

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    */
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    */
}
