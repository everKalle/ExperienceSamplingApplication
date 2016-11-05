package com.example.madiskar.experiencesamplingapp;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.SeekBar;

public class VolumeControlActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seekbar_layout);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = -20;
        params.height = 550;
        params.width = 850;
        params.y = -10;

        this.getWindow().setAttributes(params);

        SeekBar seekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
        int currentValue = seekBar.getProgress();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

}
