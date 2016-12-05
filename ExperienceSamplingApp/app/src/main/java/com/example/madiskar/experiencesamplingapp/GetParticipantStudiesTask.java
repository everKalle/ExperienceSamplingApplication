package com.example.madiskar.experiencesamplingapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;


public class GetParticipantStudiesTask implements Runnable {

    private RunnableResponse response = null;
    private String link = "https://experiencesampling.herokuapp.com/index.php/study/get_participant_studies";
    private String token;


    public GetParticipantStudiesTask(String token, RunnableResponse response) {
        this.response = response;
        this.token = token;
    }


    @Override
    public void run() {

        HttpsURLConnection connection = null;
        OutputStreamWriter wr = null;
        BufferedReader reader = null;

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
            response.processFinish(sb.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            response.processFinish("Exception: " + e.getMessage());
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
