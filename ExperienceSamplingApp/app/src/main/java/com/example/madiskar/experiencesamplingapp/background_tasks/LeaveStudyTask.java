package com.example.madiskar.experiencesamplingapp.background_tasks;

import com.example.madiskar.experiencesamplingapp.local_database.DBHandler;
import com.example.madiskar.experiencesamplingapp.interfaces.RunnableResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;


public class LeaveStudyTask implements Runnable {

    private RunnableResponse response = null;
    private String link = "https://experiencesampling.herokuapp.com/index.php/study/participant_leave_study";
    private DBHandler mydb;
    private String token;
    private String study_id;


    public LeaveStudyTask(String token, String study_id, DBHandler mydb, RunnableResponse response) {
        this.response = response;
        this.mydb = mydb;
        this.token = token;
        this.study_id = study_id;
    }


    @Override
    public void run() {

        HttpsURLConnection connection = null;
        OutputStreamWriter wr = null;
        BufferedReader reader = null;

        try {
            String data = URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");
            data += "&" + URLEncoder.encode("study_id", "UTF-8") + "=" + URLEncoder.encode(study_id, "UTF-8");

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
                //break;
            }
            response.processFinish(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.processFinish("Exception: " + e.getMessage());
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
    }

}
