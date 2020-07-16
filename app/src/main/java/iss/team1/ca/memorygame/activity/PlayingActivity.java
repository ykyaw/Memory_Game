package iss.team1.ca.memorygame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.GridView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import iss.team1.ca.memorygame.R;
import iss.team1.ca.memorygame.adapter.MyAdapter;
import iss.team1.ca.memorygame.modal.Img;

public class PlayingActivity extends AppCompatActivity implements ServiceConnection {

    //Chronometer
    private Chronometer chronometer;
    private boolean isChronometerRunning;
    private long pauseOffset;

    //Music Player
    MusicPlayerService musicPlayerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        //Binding to music service to allow music change. Refer to onServiceConnected method
        Intent musicIntent = new Intent(this, MusicPlayerService.class);
        bindService(musicIntent, this, BIND_AUTO_CREATE);

        //Assign chronometer
        chronometer = findViewById(R.id.chronometer);
        if(!isChronometerRunning){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    chronometer.start();
                }
            }).start();
            isChronometerRunning = true;
        }

    }


    @Override
    public void onPause(){
        super.onPause();
        if(musicPlayerService!=null){
            musicPlayerService.pauseMusic();
        }
        if(isChronometerRunning){
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime()-chronometer.getBase();
            isChronometerRunning = false;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(musicPlayerService!=null){
            musicPlayerService.unpauseMusic();
        }
        if(!isChronometerRunning){
            chronometer.setBase(SystemClock.elapsedRealtime()-pauseOffset);
            chronometer.start();
            isChronometerRunning = true;
        }
    }


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicPlayerService.MyBinder binder = (MusicPlayerService.MyBinder) iBinder;
        if(binder != null) {
            musicPlayerService = binder.getService();
            musicPlayerService.playGameMusic();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
}
