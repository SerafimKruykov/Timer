package com.example.timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SeekBar seekBar;
    private TextView textView;
    private Button button;
    private boolean isRunning;
    private CountDownTimer countDownTimer;
    private int defaultInterval;
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        isRunning = false;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        seekBar.setMax(600);
        setIntervalFromSharedPreferences(sharedPreferences);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               long progressInMillis = progress *1000;
                upDateTimer(progressInMillis);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


    }

    public void timerStart(View view) {


       if(!isRunning) {
           seekBar.setEnabled(false);
           button.setText("Stop");
           isRunning = true;

           countDownTimer = new CountDownTimer(seekBar.getProgress()*1000, 1000) {
               @Override
               public void onTick(long millisUntilFinished) {
                   upDateTimer(millisUntilFinished);
               }

               @Override
               public void onFinish() {

                   SharedPreferences sharedPreferences =
                           PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                   if(sharedPreferences.getBoolean("enable_sound", true)){

                       String melodyName = sharedPreferences.getString("timer_melody", "melody_1");

                       if(melodyName.equals("melody_1")){
                           MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.melody_1);
                           mediaPlayer.start();}
                       else if (melodyName.equals("melody_2")){
                           MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.melody_2);
                           mediaPlayer.start();}
                       else {MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.melody_3);
                           mediaPlayer.start();}



                       }
                   else {resetTimer();}


               }

           };
           countDownTimer.start();
       }

       else {
          resetTimer();
       }


    }

    private void resetTimer(){
        countDownTimer.cancel();
        seekBar.setEnabled(true);
        button.setText("Start");

        isRunning = false;
        setIntervalFromSharedPreferences(sharedPreferences);

    }

    private void upDateTimer(long millisUntilFinished){

        int minutes = (int)millisUntilFinished/60000;
        int seconds = (int)millisUntilFinished/1000 - minutes*60;

        String minutesString ;
        String secondsString ;

        if(minutes < 10){
            minutesString = "0"+minutes;
        }else {minutesString = ""+minutes;}

        if(seconds < 10){
            secondsString = "0"+seconds;
        }else {secondsString = ""+seconds;}

        textView.setText(minutesString + ":" + secondsString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent openSettings = new Intent(this, SettingsActivity.class);
            startActivity(openSettings);
            return true;
        } else if(id == R.id.action_about){
            Intent openAbout = new Intent(this, AboutActivity.class);
            startActivity(openAbout);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setIntervalFromSharedPreferences(SharedPreferences sharedPreferences){


        defaultInterval = Integer.valueOf(sharedPreferences.getString("default_interval", "30")) ;
        long defaultIntervalInmillis= defaultInterval*1000;
        upDateTimer(defaultIntervalInmillis);
        seekBar.setProgress(defaultInterval);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("default_interval")){
            setIntervalFromSharedPreferences(sharedPreferences);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

}