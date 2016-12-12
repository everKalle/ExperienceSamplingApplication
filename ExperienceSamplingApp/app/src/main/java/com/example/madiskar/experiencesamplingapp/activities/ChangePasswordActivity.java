package com.example.madiskar.experiencesamplingapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.madiskar.experiencesamplingapp.background_tasks.ExecutorSupplier;
import com.example.madiskar.experiencesamplingapp.R;
import com.example.madiskar.experiencesamplingapp.background_tasks.ChangePasswordTask;
import com.example.madiskar.experiencesamplingapp.interfaces.RunnableResponse;
import com.example.madiskar.experiencesamplingapp.local_database.DBHandler;


public class ChangePasswordActivity extends AppCompatActivity {

    private Button confirmBtn;
    private Button cancelBtn;
    private EditText oldPwField;
    private EditText newPwdFieldConfirm;
    private EditText newPwdField;
    private SharedPreferences sharedPref;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
        sharedPref = getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_changepassword);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        confirmBtn = (Button) findViewById(R.id.pw_change_confirm);
        cancelBtn = (Button) findViewById(R.id.pw_change_cancel);

        oldPwField = (EditText) findViewById(R.id.old_password);
        newPwdField = (EditText) findViewById(R.id.new_password);
        newPwdFieldConfirm = (EditText) findViewById(R.id.new_password_confirm);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePwd();
            }
        });

    }


    private boolean changePwd() {

        if (!validateInput()) {
            confirmBtn.setEnabled(true);
            return false;
        }

        if(!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            confirmBtn.setEnabled(true);
            return false;
        }

        confirmBtn.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ChangePasswordActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.changing_password));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ChangePasswordTask changePasswordTask = new ChangePasswordTask(
                sharedPref.getString("token", "none"),
                DBHandler.hashSha256(oldPwField.getText().toString()),
                DBHandler.hashSha256(newPwdField.getText().toString()),
                new RunnableResponse() {
            @Override
            public void processFinish(String output) {
                updateButton(true);
                progressDialog.dismiss();
                if(output.equals("success")) {
                    showToast(getString(R.string.pw_change_success));
                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if(output.equals("invalid")) {
                    showToast(getString(R.string.pw_change_fail));
                } else if(output.equals("nothing")) {
                    showToast(getString(R.string.pw_change_fail));
                } else {
                    showToast(getString(R.string.pw_change_fail));
                }
            }
        });
        ExecutorSupplier.getInstance().forBackgroundTasks().execute(changePasswordTask);
        return true;
    }


    public void updateButton(final boolean value) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                confirmBtn.setEnabled(value);
            }
        });
    }


    public void showToast(final String message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }


    private boolean validateInput() {
        boolean isValid = true;

        String oldPwd = oldPwField.getText().toString();
        String newPwd = newPwdField.getText().toString();
        String newPwdConfirm = newPwdFieldConfirm.getText().toString();

        if(oldPwd.isEmpty()) {
            oldPwField.setError(getString(R.string.enter_pass));
            isValid = false;
        }

        if(newPwd.isEmpty() || newPwd.length() > 16 || newPwd.length() < 6) {
            newPwdField.setError(getString(R.string.enter_pass));
            Toast.makeText(getBaseContext(), R.string.pass_length, Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if(!newPwd.equals(newPwdConfirm)) {
            Toast.makeText(getBaseContext(), R.string.no_match, Toast.LENGTH_LONG).show();
            newPwdFieldConfirm.setError(getString(R.string.no_match));
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
