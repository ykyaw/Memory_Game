package iss.team1.ca.memorygame.activity;


import androidx.appcompat.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luolc.emojirain.EmojiRainLayout;
import com.muddzdev.styleabletoast.StyleableToast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import iss.team1.ca.memorygame.R;
import iss.team1.ca.memorygame.adapter.PlayAdapter;
import iss.team1.ca.memorygame.comm.BaseActivity;
import iss.team1.ca.memorygame.comm.utils.ActivityCollector;
import iss.team1.ca.memorygame.modal.Img;
import iss.team1.ca.memorygame.service.MusicPlayerService;

import static java.lang.Integer.parseInt;


public class PlayingActivity extends BaseActivity implements ServiceConnection {

    //Chronometer
    private Chronometer chronometer;
    private boolean isChronometerRunning;
    private long pauseOffset;
    //For playing
    private ImageView currentView = null;
    private ImageView previousView = null;
    private int matches = 0;
    private ArrayList<Img> images;
    private Bitmap[] drawable;
    private int[] pos= new int[]{0,1,2,3,4,5,0,1,2,3,4,5};
    private int currentPos = -1;
    //Music Player
    MusicPlayerService musicPlayerService;
    private EmojiRainLayout mContainer;
    private boolean secondcardopen = false;
    private int backpress;



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
        shuffleArray(pos);
        //Assign Match Counter
        updateMatchCount(matches);

        init();
    }
    //Initializing the game
    private void init(){
        retrieveImages(); //retrieve bitmap images from previous activity
        drawable = new Bitmap[]{ //assigns bitmap images to a bitmap array
                images.get(0).getRes(),
                images.get(1).getRes(),
                images.get(2).getRes(),
                images.get(3).getRes(),
                images.get(4).getRes(),
                images.get(5).getRes(),
        };
        GridView gridView = (GridView)findViewById(R.id.gridViewPlay);
        PlayAdapter playAdapter = new PlayAdapter(this);
        gridView.setAdapter(playAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int position, long id) {
                final Animation flip = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.flip);
                if(secondcardopen == true){
                    return;
                }
                if(currentPos <0){
                    currentPos = position;
                    currentView = (ImageView) view;
                    ((ImageView)view).setImageBitmap(drawable[pos[position]]);
                    ((ImageView)view).startAnimation(flip);
                }
                else{
                    secondcardopen = true;
                    if(currentPos == position){
                        ((ImageView)view).setImageResource(R.drawable.turtlecard);
                        ((ImageView)view).startAnimation(flip);
                        secondcardopen = false;
                    }
                    else if (pos[currentPos] != pos[position]){
                        ((ImageView)view).setImageBitmap(drawable[pos[position]]);
                        ((ImageView)view).startAnimation(flip);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                currentView.setImageResource(R.drawable.turtlecard);
                                ((ImageView)view).setImageResource(R.drawable.turtlecard);
                                currentView.startAnimation(flip);
                                ((ImageView)view).startAnimation(flip);
                                secondcardopen = false;
                            }
                        }, 500);
                        triggernoMatchToast();
                    }
                    else{
                        secondcardopen = false;
                        currentView.setOnClickListener(null);
                        ((ImageView)view).setOnClickListener(null);
                        ((ImageView)view).setImageBitmap(drawable[pos[position]]);
                        ((ImageView)view).startAnimation(flip);
                        matches++;
                        updateMatchCount(matches);
                        triggermatchtoast();
                        if(matches==6){
                            chronometer.stop();
                            musicPlayerService.playVictoryMusic();
                            victoryAnimation();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    endGame();
                                }
                            }, 4000);
                        }
                    }
                    currentPos=-1;
                }
            }
        });
    }
    private void retrieveImages(){
        images = new ArrayList<Img>();
        for(int i=0; i<6; i++){
            String filename = "bitmap" + i;
            File file = new File(getApplicationContext().getFilesDir(), filename);
            try {
                FileInputStream fis = new FileInputStream(file);
                Bitmap res = BitmapFactory.decodeStream(fis);
                images.add(new Img(i, res));
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        Collections.shuffle(images);
    }

    private void updateMatchCount(int count){
        TextView matchcount = findViewById(R.id.matchCount);
        matchcount.setText(count + "/6");
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
    private void endGame(){
        Intent intent=new Intent(this,SubmitActivity.class);
        //elapsedMillis = (int) (SystemClock.elapsedRealtime() - chronometer.getBase());
        String time = chronometer.getText().toString();
        int score = parseStrTimeToIntScore(time);
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicPlayerService.MyBinder binder = (MusicPlayerService.MyBinder) iBinder;
        if(binder != null) {
            musicPlayerService = binder.getService();
            musicPlayerService.playGameMusic();
        }
    }

    static void shuffleArray(int[] array) {
        int index, temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    private void victoryAnimation(){
        mContainer= (EmojiRainLayout)findViewById(R.id.rain);
        mContainer.addEmoji(R.drawable.confetti);
        mContainer.addEmoji(R.drawable.fireworks);
        mContainer.addEmoji(R.drawable.popper);
        mContainer.addEmoji(R.drawable.confetti);
        mContainer.addEmoji(R.drawable.fireworks);
        mContainer.addEmoji(R.drawable.popper);
        mContainer.stopDropping();
        mContainer.setPer(10);
        mContainer.setDuration(7200);
        mContainer.setDropDuration(2400);
        mContainer.setDropFrequency(500);
        mContainer.startDropping();

    }

    private void triggermatchtoast(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StyleableToast.makeText(getApplicationContext(),"Match!",R.style.successToast).show();
            }
        });
    }
    private void triggernoMatchToast(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StyleableToast.makeText(getApplicationContext(),"Try again!",R.style.tryagainToast).show();
            }
        });
    }

    private int parseStrTimeToIntScore(String time){
        String min = time.substring(0,2);
        int minutes = parseInt(min);
        String sec = time.substring(3);
        int seconds = parseInt(sec);
        return seconds + minutes*60;
    }

    public void onBackPressed(){
        if(backpress<1){
            Toast.makeText(getApplicationContext(), " Press Back again to return to Main Menu ", Toast.LENGTH_SHORT).show();
        }
        backpress++;
        if (backpress>1) {
            ActivityCollector.goToMainActivity();
        }
    }
    
    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }

}
