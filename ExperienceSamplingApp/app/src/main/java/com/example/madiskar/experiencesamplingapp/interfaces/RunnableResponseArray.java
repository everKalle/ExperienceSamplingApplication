package com.example.madiskar.experiencesamplingapp.interfaces;

import com.example.madiskar.experiencesamplingapp.data_types.Study;

import java.util.ArrayList;

public interface RunnableResponseArray {

    void processFinish(String message, ArrayList<Study> studies);

}
