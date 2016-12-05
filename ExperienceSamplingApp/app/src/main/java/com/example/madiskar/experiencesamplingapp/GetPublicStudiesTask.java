package com.example.madiskar.experiencesamplingapp;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class GetPublicStudiesTask implements Runnable {

    private RunnableResponseArray response = null;
    private String link = "https://experiencesampling.herokuapp.com/index.php/study/get_public_studies";
    private ArrayList<Study> myStudies;
    private DBHandler mydb;

    public GetPublicStudiesTask(DBHandler mydb, RunnableResponseArray response) {
        this.response = response;
        this.mydb = mydb;
    }

    @Override
    public void run() {

        HttpsURLConnection connection = null;
        BufferedReader reader = null;

        try {
            connection = (HttpsURLConnection) new URL(link).openConnection();
            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, null, new java.security.SecureRandom());
            connection.setSSLSocketFactory(sc.getSocketFactory());

            connection.setRequestMethod("GET");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(20000);
            connection.setDoOutput(false);

            //read response
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            myStudies = mydb.getAllStudies();
            response.processFinish(sb.toString(), jsonArrayToPublicStudyArray(DBHandler.parseJsonString(sb.toString())));
        }
        catch (Exception e) {
            e.printStackTrace();
            response.processFinish("Exception: " + e.getMessage(), new ArrayList<Study>() );
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null) {
                connection.disconnect();
            }
        }
    }


    private boolean checkStudyList(long studyId) {
        for(Study s : myStudies) {
            if(s.getId() == studyId)
                return true;
        }
        return false;
    }


    private ArrayList<Study> jsonArrayToPublicStudyArray(JSONArray jsonArray) {
        ArrayList<Study> studies = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonStudy = jsonArray.getJSONObject(i);
                long studyID = jsonStudy.getLong("id");
                if(checkStudyList(studyID))
                    continue;
                String name = jsonStudy.getString("study-title");
                int notificationsPerDay = jsonStudy.getInt("study-beeps-per-day");
                int minTimeBetweenNotifications = jsonStudy.getInt("study-min-time-between-beeps");
                Calendar realStartDate = DBHandler.stringToCalendar(jsonStudy.getString("study-start-date"));
                BeepFerePeriod defaultBeepFree = DBHandler.stringToBeepFree(0, jsonStudy.getString("study-beep-end-time"), jsonStudy.getString("study-beep-start-time"));
                int postponeTime = jsonStudy.getInt("study-postpone-time");
                boolean allowPostpone = (jsonStudy.getInt("study-allow-postpone") == 1);
                boolean studyDurationForUser = (jsonStudy.getInt("study-duration-for-user") == 1);
                Calendar beginDate = DBHandler.stringToCalendar(DBHandler.calendarToString(Calendar.getInstance()));
                Calendar endDate = DBHandler.stringToCalendar(DBHandler.calendarToString(Calendar.getInstance()));
                if(beginDate.before(realStartDate)) {
                    beginDate = realStartDate;
                    endDate = realStartDate;
                }
                Calendar realEndDate = DBHandler.stringToCalendar(jsonStudy.getString("study-end-date"));
                int studyLengthForUser = jsonStudy.getInt("study-duration-time");
                if(studyDurationForUser) {
                    endDate.add(Calendar.MINUTE, studyLengthForUser);
                    if(endDate.after(realEndDate))
                        endDate = realEndDate;
                } else {
                    endDate = realEndDate;
                }
                if(beginDate.after(endDate))
                    continue;

                JSONArray questionArray = jsonStudy.getJSONArray("questions");
                Question[] qs = new Question[questionArray.length()];
                for(int j = 0; j < questionArray.length(); j++) {
                    JSONObject jsonQuestion = questionArray.getJSONObject(j);
                    String title = jsonQuestion.getString("question-title");
                    String type = jsonQuestion.getString("question-type");
                    Question q = null;
                    if(type.equals("freetext")) {
                        q = new FreeTextQuestion(studyID, title);
                    } else if(type.equals("multichoice")) {
                        String choicesAsString = jsonQuestion.getString("question-multichoices");
                        choicesAsString = choicesAsString.substring(1, choicesAsString.length() - 1);
                        String[] choices = choicesAsString.split(Pattern.quote(","));
                        for(int k = 0; k < choices.length; k++) {
                            choices[k] = choices[k].substring(1, choices[k].length() - 1);
                        }
                        int singleChoice = jsonQuestion.getInt("question-multichoice-single-choice");
                        q = new MultipleChoiceQuestion(studyID, singleChoice, title, choices);
                    }
                    qs[j] = q;
                }
                Questionnaire qnaire = new Questionnaire(studyID, qs);

                JSONArray eventArray = jsonStudy.getJSONArray("events");
                Event[] events = new Event[eventArray.length()];
                for(int g = 0; g < eventArray.length(); g++) {
                    JSONObject jsonEvent = eventArray.getJSONObject(g);
                    long eventID = jsonEvent.getLong("id");
                    String title = jsonEvent.getString("event-title");
                    int controltime = jsonEvent.getInt("event-control-time");
                    String controltimeUnit = jsonEvent.getString("event-control-time-unit");
                    events[g] = new Event(eventID, studyID, title, controltime, controltimeUnit);
                }

                studies.add(new Study(studyID, name, qnaire, beginDate, endDate, studyLengthForUser, notificationsPerDay,
                        postponeTime, allowPostpone, minTimeBetweenNotifications, events, defaultBeepFree, true));
            }

        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return studies;
    }


}
