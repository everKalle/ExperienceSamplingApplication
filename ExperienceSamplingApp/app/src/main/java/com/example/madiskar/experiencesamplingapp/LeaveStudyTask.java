package com.example.madiskar.experiencesamplingapp;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;


public class LeaveStudyTask extends AsyncTask<String, Void, String> {

    private AsyncResponse response = null;
    private String link = "https://experiencesampling.herokuapp.com/index.php/study/participant_leave_study";
    private DBHandler mydb;


    public LeaveStudyTask(AsyncResponse response, DBHandler mydb) {
        this.response = response;
        this.mydb = mydb;
    }


    @Override
    protected String doInBackground(String... params) {

        HttpsURLConnection connection = null;
        OutputStreamWriter wr = null;
        BufferedReader reader = null;

        try {
            String data = URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");
            data += "&" + URLEncoder.encode("study_id", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

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
            mydb.deleteStudyEntry(Long.parseLong(params[1]));
            return sb.toString();
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
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


    @Override
    protected void onPostExecute(String result) {
        response.processFinish(result);
    }
}
