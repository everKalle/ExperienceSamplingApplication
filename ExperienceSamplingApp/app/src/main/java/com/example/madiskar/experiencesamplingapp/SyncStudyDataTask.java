package com.example.madiskar.experiencesamplingapp;


import android.content.Context;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class SyncStudyDataTask implements Runnable {

    private StudyDataSyncResponse response;
    private String link = "https://experiencesampling.herokuapp.com/index.php/study/get_participant_studies";
    private String token;
    private DBHandler mydb;
    private boolean onlyUpdateLocalStudies;


    public SyncStudyDataTask(String token, DBHandler mydb, boolean onlyUpdateLocalStudies, StudyDataSyncResponse response) {
        this.token = token;
        this.response = response;
        this.mydb = mydb;
        this.onlyUpdateLocalStudies = onlyUpdateLocalStudies;
    }

    @Override
    public void run() {
        HttpsURLConnection connection = null;
        OutputStreamWriter wr = null;
        BufferedReader reader = null;
        ArrayList<Study> newStudies = new ArrayList<>();
        ArrayList<Study> updatedStudies = new ArrayList<>();
        ArrayList<Study> oldStudies = new ArrayList<>();
        ArrayList<Study> cancelledStudies = new ArrayList<>();

        try {
            String data = URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");

            connection = (HttpsURLConnection) new URL(link).openConnection();
            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, null, new java.security.SecureRandom());
            connection.setSSLSocketFactory(sc.getSocketFactory());

            //send data
            connection.setRequestMethod("POST");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(20000);
            connection.setDoOutput(true);

            wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(data);
            wr.flush();

            //read response
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray jsonArray = DBHandler.parseJsonString(sb.toString());
            ArrayList<Study> studies = DBHandler.jsonArrayToStudyArray(jsonArray, false);

            if(onlyUpdateLocalStudies) {
                int updateCounter = 0;
                for(Study s : studies) {
                    if(mydb.isStudyInDb(s)) {
                        if(Calendar.getInstance().after(s.getEndDate())) {
                            cancelledStudies.add(mydb.getStudy(s.getId()));
                            continue;
                        }
                        Study oldStudy = mydb.getStudy(s.getId());
                        if(notificationDataChanged(oldStudy, s)) {
                            updatedStudies.add(s);
                            oldStudies.add(oldStudy);
                        }
                        mydb.updateStudyEntry(s);
                        updateCounter ++;
                    }
                }
                Log.i("SYNCED STUDY INFO", "Updated Studies: " + updateCounter);
                response.processFinish(sb.toString(), newStudies, mydb.getAllStudies(), updatedStudies, oldStudies, cancelledStudies);
            } else {
                int updateCounter = 0;
                for(Study s : studies) {
                    if(!mydb.isStudyInDb(s)) {
                        if(!Calendar.getInstance().after(s.getEndDate())) {
                            mydb.insertStudy(s);
                            newStudies.add(s);
                        }
                    } else {
                        if(Calendar.getInstance().after(s.getEndDate())) {
                            Log.i("Study over", "hetkekuup2ev: " + DBHandler.calendarToString(Calendar.getInstance()) + " lopukuupaev: " + DBHandler.calendarToString(s.getEndDate()));
                            cancelledStudies.add(mydb.getStudy(s.getId()));
                            continue;
                        }
                        Study oldStudy = mydb.getStudy(s.getId());
                        if(notificationDataChanged(oldStudy, s)) {
                            updatedStudies.add(s);
                            oldStudies.add(oldStudy);
                        }
                        mydb.updateStudyEntry(s);
                        updateCounter ++;
                    }
                }
                Log.i("SYNCED STUDY INFO", "New Studies: " + newStudies.size() + ", Updated Studies: " + updateCounter);
                response.processFinish(sb.toString(), newStudies, mydb.getAllStudies(), updatedStudies, oldStudies, cancelledStudies);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response.processFinish("Exception: " + e.getMessage(), newStudies, mydb.getAllStudies(), updatedStudies, oldStudies, cancelledStudies);
        } finally {
            if(wr != null) {
                try {
                    wr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

    private boolean notificationDataChanged(Study oldS, Study newS) {
        int old_interval = oldS.getMinTimeBetweenNotifications();
        int updated_interval = newS.getMinTimeBetweenNotifications();
        if(!(old_interval == updated_interval)) {
            return true;
        } else {
            return false;
        }
    }

}