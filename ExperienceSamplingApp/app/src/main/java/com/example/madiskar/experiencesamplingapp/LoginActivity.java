package com.example.madiskar.experiencesamplingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText pwdField;
    private Button loginbtn;
    private Button signupbtn;
    private SharedPreferences sharedPref;
    private HttpsURLConnection connection;
    private OutputStreamWriter wr;
    private BufferedReader reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        sharedPref = getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
        int loggedIn = sharedPref.getInt("LoggedIn", 0);

        if(loggedIn == 1) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else {

            setContentView(R.layout.activity_login);

            emailField = (EditText) findViewById(R.id.email_input);
            pwdField = (EditText) findViewById(R.id.password_input);

            loginbtn = (Button) findViewById(R.id.button_login);
            signupbtn = (Button) findViewById(R.id.button_signup);

            loginbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login();
                }
            });

            signupbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    private boolean login() {

        if (!validateInput()) {
            Toast.makeText(getBaseContext(), getString(R.string.wrong_format), Toast.LENGTH_LONG).show();
            loginbtn.setEnabled(true);
            return false;
        }

        if(!isNetworkAvailable()) {
            Toast.makeText(getBaseContext(), getString(R.string.no_internet) , Toast.LENGTH_LONG).show();
            loginbtn.setEnabled(true);
            return false;
        }

        loginbtn.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    String link = "https://experiencesampling.herokuapp.com/index.php/participant/login";
                    String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

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
                    return "Exception: " + e.getMessage();
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

            @Override
            protected void onPostExecute(final String result) {
                //Log.i("LOGGING-IN PROCESS", result);
                if(result.equals("nothing")) {
                    loginbtn.setEnabled(true);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.log_fail), Toast.LENGTH_LONG).show();

                } else if(result.equals("invalid")) {
                    loginbtn.setEnabled(true);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.wrong), Toast.LENGTH_LONG).show();

                } else if(!result.equals(getString(R.string.not_established))) {
                    final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    progressDialog.dismiss();

                    final ProgressDialog fetchDataDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
                    fetchDataDialog.setIndeterminate(true);
                    fetchDataDialog.setMessage(getString(R.string.fetching));
                    fetchDataDialog.setCanceledOnTouchOutside(false);
                    fetchDataDialog.show();
                    GetParticipantStudiesTask task1 = new GetParticipantStudiesTask(result, new RunnableResponse() {
                        @Override
                        public void processFinish(String output) {
                            if(output.equals("invalid_token")) {
                                loginbtn.setEnabled(true);
                                fetchDataDialog.dismiss();
                                Toast.makeText(getApplicationContext(), getString(R.string.auth_fail), Toast.LENGTH_LONG).show();
                            } else if(output.equals("nothing")) {
                                loginbtn.setEnabled(true);
                                fetchDataDialog.dismiss();
                                Toast.makeText(getApplicationContext(), getString(R.string.fetch_fail), Toast.LENGTH_LONG).show();
                            } else {
                                DBHandler mydb = DBHandler.getInstance(getApplicationContext());
                                Log.i("LOGGING SERVER RESPONSE", output);
                                mydb.clearTables();
                                JSONArray jsonArray = DBHandler.parseJsonString(output);
                                Study[] studies = DBHandler.jsonArrayToStudyArray(jsonArray);

                                Calendar cInstance = Calendar.getInstance();
                                for (Study s : studies) { // add studies to local db and also set up alarms
                                    if(!cInstance.after(s.getEndDate())) {
                                        mydb.insertStudy(s);
                                    }
                                }
                                for (Study s : studies) {
                                    if(!cInstance.after(s.getEndDate())) {
                                        ResponseReceiver rR = new ResponseReceiver(s);
                                        rR.setupAlarm(getApplicationContext(), true);
                                    }
                                }

                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("token", result);
                                editor.putInt("LoggedIn", 1);
                                editor.putString("username", emailField.getText().toString());
                                editor.apply();
                                fetchDataDialog.dismiss();
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                    ExecutorSupplier.getInstance().forBackgroundTasks().execute(task1);

                } else {
                    loginbtn.setEnabled(true);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.log_fail), Toast.LENGTH_SHORT).show();
                }
            }

        }.execute(emailField.getText().toString(), DBHandler.hashSha256(pwdField.getText().toString()));
        return true;
    }

    private boolean validateInput() {
        boolean isValid = true;

        String email = emailField.getText().toString();
        String pwd = pwdField.getText().toString();

        if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError(getString(R.string.enter_valid));
            isValid = false;
        }

        if(pwd.isEmpty() || pwd.length() > 16 || pwd.length() < 6) {
            pwdField.setError(getString(R.string.enter_pass));
            isValid = false;
        }
        return isValid;

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
