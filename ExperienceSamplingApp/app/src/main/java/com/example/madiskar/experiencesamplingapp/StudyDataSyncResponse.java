package com.example.madiskar.experiencesamplingapp;

import java.util.ArrayList;

/**
 * Created by madiskar on 13/11/2016.
 */

public interface StudyDataSyncResponse {

    void processFinish(String output, ArrayList<Study> newStudies);
}
