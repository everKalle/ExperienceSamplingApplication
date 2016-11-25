package com.example.madiskar.experiencesamplingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.v("praegu", String.valueOf(currentVolume));

        if (currentVolume == 0) {
            Intent intent = new Intent(this, VolumeDialog.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = -38;
        params.height = 450;
        params.width = 900;
        params.y = -20;

        setContentView(R.layout.seekbar_layout);

        this.getWindow().setAttributes(params);

        seekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
        highText = (TextView) findViewById(R.id.highTextView);

        spref = getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);

        if (spref.getInt("volume",-1) != -1)
            seekBar.setProgress(spref.getInt("volume",0));
        else {
            seekBar.setProgress(50);
            SharedPreferences.Editor editor = spref.edit();
            editor.putInt("volume", 50);
            editor.apply();
        }
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