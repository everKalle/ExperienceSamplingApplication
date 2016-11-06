package com.example.madiskar.experiencesamplingapp;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class SaveAnswersTask extends AsyncTask<String, Void, String> {

    private AsyncResponse response = null;
    private String link = "https://experiencesampling.herokuapp.com/index.php/study/store_study_results";
    private boolean networkAvailable;
    private DBHandler mydb;


    public SaveAnswersTask(AsyncResponse response, boolean networkAvailable, DBHandler mydb) {
        this.response = response;
        this.networkAvailable = networkAvailable;
        this.mydb = mydb;
    }


    @Override
    protected String doInBackground(String... params) {

        if(networkAvailable) {
            HttpsURLConnection connection = null;
            OutputStreamWriter wr = null;
            BufferedReader reader = null;

            try {
                String data = URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");
                data += "&" + URLEncoder.encode("study_id", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");
                data += "&" + URLEncoder.encode("answers", "UTF-8") + "=" + URLEncoder.encode(escapeChars(params[2]), "UTF-8");

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
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("SaveAnswersTask", "Error while trying to send data to server, saving to local instead");
                if(!params[0].equals("none")) {
                    mydb.insertAnswer(Integer.parseInt(params[1]), escapeChars(params[2]), DBHandler.calendarToString(Calendar.getInstance()));
                    return "saved-to-local";
                } else {
                    return "invalid_token";
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
            if(!params[0].equals("none")) {
                mydb.insertAnswer(Integer.parseInt(params[1]), escapeChars(params[2]), DBHandler.calendarToString(Calendar.getInstance()));
                return "saved-to-local";
            } else
                return "invalid_token";
        }
    }


    @Override
    protected void onPostExecute(String result) {
        response.processFinish(result);
    }


    private String escapeChars(String text) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String replacement = "";
            switch (c) {
                case '\n':
                    replacement = "\\n";
                    break;
                case '\t':
                    replacement = "\\t";
                    break;
                case '\b':
                    replacement = "\\b";
                    break;
                case '\\':
                    replacement = "\\\\";
                    break;
                case '\f':
                    replacement = "\\f";
                    break;
                case '\r':
                    replacement = "\\r";
                    break;
            }
            if(!replacement.equals(""))
                sb.append(replacement);
            else
                sb.append(c);

        }
        return sb.toString();
    }

}
