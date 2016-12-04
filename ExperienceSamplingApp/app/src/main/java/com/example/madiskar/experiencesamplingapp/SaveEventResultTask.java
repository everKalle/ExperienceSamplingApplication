package com.example.madiskar.experiencesamplingapp;


import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;


public class SaveEventResultTask implements Runnable {

    private RunnableResponse response = null;
    private String link = "https://experiencesampling.herokuapp.com/index.php/study/store_event_results";
    private boolean networkAvailable;
    private DBHandler mydb;
    private String token;
    private String event_id;
    private String start_time;
    private String end_time;


    public SaveEventResultTask(String token, String event_id, String start_time, String end_time, boolean networkAvailable, DBHandler mydb, RunnableResponse response) {
        this.response = response;
        this.networkAvailable = networkAvailable;
        this.mydb = mydb;
        this.token = token;
        this.event_id = event_id;
        this.start_time = start_time;
        this.end_time = end_time;
    }


    @Override
    public void run() {
        if (networkAvailable) {
            HttpsURLConnection connection = null;
            OutputStreamWriter wr = null;
            BufferedReader reader = null;

            try {
                String data = URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");
                data += "&" + URLEncoder.encode("event_id", "UTF-8") + "=" + URLEncoder.encode(event_id, "UTF-8");
                data += "&" + URLEncoder.encode("start_time", "UTF-8") + "=" + URLEncoder.encode(start_time, "UTF-8");
                data += "&" + URLEncoder.encode("end_time", "UTF-8") + "=" + URLEncoder.encode(end_time, "UTF-8");

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
                response.processFinish(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("SaveEventResultTask", "Error while trying to send data to server, saving to local instead");
                if(!token.equals("none")) {
                    mydb.insertEventResult(Long.parseLong(event_id), start_time, end_time);
                    response.processFinish("saved-to-local");
                } else {
                    response.processFinish("invalid_token");
                }
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
            if(!token.equals("none")) {
                mydb.insertEventResult(Long.parseLong(event_id), start_time, end_time);
                response.processFinish("saved-to-local");
            } else {
                response.processFinish("invalid_token");
            }
        }
    }

}
