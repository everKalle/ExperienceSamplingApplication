package com.example.madiskar.experiencesamplingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;


public class RegisterActivity extends AppCompatActivity {

    private Button registerbtn;
    private Button cancelbtn;
    private EditText emailField;
    private EditText pwdField;
    private EditText pwdFieldConfirm;
    private HttpsURLConnection connection;
    private OutputStreamWriter wr;
    private BufferedReader reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        registerbtn = (Button) findViewById(R.id.register_confirm);
        cancelbtn = (Button) findViewById(R.id.register_cancel);

        emailField = (EditText) findViewById(R.id.email_input_register);
        pwdField = (EditText) findViewById(R.id.password_input_register);
        pwdFieldConfirm = (EditText) findViewById(R.id.password_input_register_confirm);

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean validateInput() {
        boolean isValid = true;

        String email = emailField.getText().toString();
        String pwd = pwdField.getText().toString();
        String pwdConfirm = pwdFieldConfirm.getText().toString();

        if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError(getString(R.string.enter_valid));
            isValid = false;
        }

        if(pwd.isEmpty() || pwd.length() > 16 || pwd.length() < 6) {
            pwdField.setError(getString(R.string.enter_pass));
            Toast.makeText(getBaseContext(), R.string.pass_length, Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if(!pwd.equals(pwdConfirm)) {
            Toast.makeText(getBaseContext(), R.string.no_match, Toast.LENGTH_LONG).show();
            pwdFieldConfirm.setError(getString(R.string.no_match));
            isValid = false;
        }

        return isValid;
    }


    private boolean register() {

        if (!validateInput()) {
            registerbtn.setEnabled(true);
            return false;
        }

        if(!isNetworkAvailable()) {
            Toast.makeText(getBaseContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
            registerbtn.setEnabled(true);
            return false;
        }

        registerbtn.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.creating_account));
        progressDialog.show();

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    String link = "https://experiencesampling.herokuapp.com/index.php/participant/register";
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
            protected void onPostExecute(String result) {
                Log.i("REGISTERING PROCESS", result);
                if(result.equals("nothing")) {
                    registerbtn.setEnabled(true);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.register_fail, Toast.LENGTH_LONG).show();
                } else if(result.equals("exists")) {
                    registerbtn.setEnabled(true);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.used_email, Toast.LENGTH_LONG).show();
                } else if(result.equals("success")) {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.create_success, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                } else {
                    registerbtn.setEnabled(true);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.register_fail, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(emailField.getText().toString(), DBHandler.hashSha256(pwdField.getText().toString()));
        return true;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
