package iss.team1.ca.memorygame.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import iss.team1.ca.memorygame.R;

public class MusicPlayerService extends Service {

    MediaPlayer player;
    private IBinder mBinder = new MyBinder();
    private Handler mHandler;
    private Boolean mIsPaused;
    public int currentTrack;

    public MusicPlayerService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mHandler = new Handler();
        mIsPaused=true;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        public MusicPlayerService getService(){
            return MusicPlayerService.this;
        }
    }


    public void playMusic(final int music){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(music ==1)playMainMusic();
                if(music ==2)playGameMusic();
                if(music ==3)playVictoryMusic();
            }
        }).start();
    }

    public void playMainMusic(){
        if(player !=null){
            player.stop();
            player.release();
        }
        player = MediaPlayer.create(this, R.raw.main_music);
        currentTrack = 1;
        player.setLooping(true);
        player.start();

    }
    public void playGameMusic(){
        if(player !=null){
            player.stop();
            player.release();
        }
        player = MediaPlayer.create(this, R.raw.game_music);
        currentTrack = 2;
        player.setLooping(true);
        player.start();
    }
    public void playVictoryMusic(){
        if(player !=null){
            player.stop();
            player.release();
        }
        player = MediaPlayer.create(this, R.raw.victory_music);
        currentTrack = 3;
        player.setLooping(true);
        player.start();
    }

    public void pauseMusic(){
        player.pause();
        mIsPaused=true;
    }

    public void unpauseMusic(){
        player.start();
        mIsPaused=false;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }
    

}
