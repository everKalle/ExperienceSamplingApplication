package com.example.madiskar.experiencesamplingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

public class VolumeControlActivity extends FragmentActivity {

    public SeekBar seekBar;
    public TextView highText;
    private SharedPreferences spref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seekbar_layout);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = -20;
        params.height = 550;
        params.width = 1050;
        params.y = -10;

        this.getWindow().setAttributes(params);

        seekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
        highText = (TextView) findViewById(R.id.highTextView);

        spref = getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);

        seekBar.setProgress(spref.getInt("volume",0));
        highText.setText(String.valueOf(seekBar.getProgress()));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int currentValue;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentValue = i;
                highText.setText(String.valueOf(i));
                SharedPreferences.Editor editor = spref.edit();
                editor.putInt("volume", i);
                editor.apply();
                seekBar.setProgress(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                highText.setText(String.valueOf(currentValue));
                SharedPreferences.Editor editor = spref.edit();
                editor.putInt("volume", currentValue);
                editor.apply();
                seekBar.setProgress(currentValue);
            }
        });

    }

}