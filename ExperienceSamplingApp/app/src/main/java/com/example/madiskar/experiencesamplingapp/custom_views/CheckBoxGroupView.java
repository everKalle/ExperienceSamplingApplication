package com.example.madiskar.experiencesamplingapp.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.TableLayout;

import java.util.ArrayList;
import java.util.List;


public class CheckBoxGroupView extends TableLayout {

    List<CheckBox> checkboxes = new ArrayList<>();

    public CheckBoxGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void put(CheckBox checkBox) {
        checkboxes.add( checkBox);
        invalidate();
        requestLayout();
    }


    public List<?> getCheckboxesChecked(){

        List<CheckBox> checkeds = new ArrayList<>();
        for (CheckBox c : checkboxes){
            if(c.isChecked())
                checkeds.add(c);
        }

        return checkeds;
    }

    public int[] getCheckedBoxes(){

        int[] checked = new int[checkboxes.size()];
        for (int i = 0; i < checkboxes.size(); i++){
            if(checkboxes.get(i).isChecked())
                checked[i] = 1;
            else
                checked[i] = 0;
        }
        return checked;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        for(CheckBox c: checkboxes) {
            addView(c);
        }

        invalidate();
        requestLayout();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


}