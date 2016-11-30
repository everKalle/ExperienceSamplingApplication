package com.example.madiskar.experiencesamplingapp;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;


public class SyncResultDataTask implements Runnable {

    private RunnableResponse response = null;
    private DBHandler mydb;
    private boolean networkAvailable;
    private String eventLink = "https://experiencesampling.herokuapp.com/index.php/study/store_event_results";
    private String answerLink = "https://experiencesampling.herokuapp.com/index.php/study/store_study_results";
    private String token;

    public SyncResultDataTask(boolean networkAvailable, DBHandler mydb, String token, RunnableResponse response) {
        this.response = response;
        this.mydb = mydb;
        this.networkAvailable = networkAvailable;
        this.token = token;
    }

    @Override
    public void run() {

        ArrayList<String[]> answers = mydb.getAllAnswers();
        ArrayList<String[]> eventResults = mydb.getAllEventResults();

        StringBuilder sb = new StringBuilder();

        if(!token.equals("none")) {
            for (String[] s : answers) {
                Log.i("SyncAllDataTask", "ANSWER:" + Arrays.asList(s).toString());
                String id = s[0];
                String studyId = s[1];
                String answerTxt = s[3];
                String data = null;
                try {
                    data = URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");
                    data += "&" + URLEncoder.encode("study_id", "UTF-8") + "=" + URLEncoder.encode(studyId, "UTF-8");
                    data += "&" + URLEncoder.encode("answers", "UTF-8") + "=" + URLEncoder.encode(answerTxt, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (data != null) {
                    String response = sendDataToServer(data, answerLink);
                    if (response.equals("success")) {
                        mydb.deleteAnswerEntry(Long.parseLong(id));
                        sb.append("success,");
                    } else {
                        //TODO: handle more server responses here
                        sb.append("fail,");
                        Log.i("SyncAllDataTask", "Error while trying to send data to server, skipping this row");
                    }
                } else {
                    sb.append("fail,");
                }

            }

            for (String[] s : eventResults) {
                Log.i("SyncAllDataTask", "EVENT:" + Arrays.asList(s).toString());
                String id = s[0];
                String eventId = s[1];
                String startTime = s[2];
                String endTime = s[3];
                String data = null;
                try {
                    data = URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");
                    data += "&" + URLEncoder.encode("event_id", "UTF-8") + "=" + URLEncoder.encode(eventId, "UTF-8");
                    data += "&" + URLEncoder.encode("start_time", "UTF-8") + "=" + URLEncoder.encode(startTime, "UTF-8");
                    data += "&" + URLEncoder.encode("end_time", "UTF-8") + "=" + URLEncoder.encode(endTime, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (data != null) {
                    String response = sendDataToServer(data, eventLink);
                    if (response.equals("success")) {
                        mydb.deleteEventResultEntry(Long.parseLong(id));
                        sb.append("success,");
                    } else {
                        //TODO: handle more server responses here
                        sb.append("fail,");
                        Log.i("SyncAllDataTask", "Error while trying to send data to server, skipping this row");
                    }
                } else {
                    sb.append("fail,");
                }
            }
        }
        if(sb.length() != 0)
            sb.deleteCharAt(sb.lastIndexOf(","));
        response.processFinish(sb.toString());
    }


    private String sendDataToServer(String dataToSend, String linkToSendTo) {
        String returnVal = null;
        if(networkAvailable) {
            HttpsURLConnection connection = null;
            OutputStreamWriter wr = null;
            BufferedReader reader = null;

            try {
                connection = (HttpsURLConnection) new URL(linkToSendTo).openConnection();
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
                wr.write(dataToSend);
                wr.flush();

                //read response
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    //break;
                }
                returnVal = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("SyncAllDataTask", "Error while trying to send data to server, skipping this row");
                return "skipped-this-row";
            } finally {
                if (wr != null) {
                    try {
                        wr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } else {
            Log.i("SyncAllDataTask", "Something wrong with connection, skipping this row");
            returnVal = "skipped-this-row";
        }
        return returnVal;
    }
}
