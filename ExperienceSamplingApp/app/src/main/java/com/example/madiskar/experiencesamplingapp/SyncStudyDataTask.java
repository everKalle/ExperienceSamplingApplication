package com.example.madiskar.experiencesamplingapp;


import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class SyncStudyDataTask implements Runnable {

    private StudyDataSyncResponse response;
    private String link = "https://experiencesampling.herokuapp.com/index.php/study/get_participant_studies";
    private String token;
    private DBHandler mydb;


    public SyncStudyDataTask(String token, DBHandler mydb, StudyDataSyncResponse response) {
        this.token = token;
        this.response = response;
        this.mydb = mydb;
    }

    @Override
    public void run() {
        HttpsURLConnection connection = null;
        OutputStreamWriter wr = null;
        BufferedReader reader = null;
        ArrayList<Study> newStudies = new ArrayList<>();

        try {
            String data = URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");

            connection = (HttpsURLConnection) new URL(link).openConnection();
            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, null, new java.security.SecureRandom());
            connection.setSSLSocketFactory(sc.getSocketFactory());

            //send data
            connection.setRequestMethod("POST");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
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
                //break;
            }

            int updateCounter = 0;
            JSONArray jsonArray = DBHandler.parseJsonString(sb.toString());
            Study[] studies = DBHandler.jsonArrayToStudyArray(jsonArray);
            for(Study s : studies) {
                if(!mydb.isStudyInDb(s)) {
                    mydb.insertStudy(s);
                    newStudies.add(s);
                } else {
                    mydb.updateStudyEntry(s);
                    updateCounter ++;
                }
            }
            Log.i("SYNCED STUDY INFO", "New Studies: " + newStudies.size() + ", Updated Studies: " + updateCounter);
            response.processFinish(sb.toString(), newStudies);
        }
        catch (Exception e) {
            e.printStackTrace();
            response.processFinish("Exception: " + e.getMessage(), newStudies);
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

}
