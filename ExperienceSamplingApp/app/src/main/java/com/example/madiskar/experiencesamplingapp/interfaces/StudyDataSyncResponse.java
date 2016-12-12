package com.example.madiskar.experiencesamplingapp.interfaces;

import com.example.madiskar.experiencesamplingapp.data_types.Study;

import java.util.ArrayList;

public interface StudyDataSyncResponse {

    void processFinish(String output, ArrayList<Study> newStudies, ArrayList<Study> allStudies, ArrayList<Study> updatedStudies, ArrayList<Study> oldStudies, ArrayList<Study> cancelledStudies);
}