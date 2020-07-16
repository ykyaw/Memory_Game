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
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import iss.team1.ca.memorygame.R;
import iss.team1.ca.memorygame.adapter.PlayAdapter;
import iss.team1.ca.memorygame.modal.Img;
import iss.team1.ca.memorygame.service.MusicPlayerService;


public class PlayingActivity extends AppCompatActivity implements ServiceConnection {

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
    //private ArrayList<Integer> pos;
    private int[] pos= new int[]{0,1,2,3,4,5,0,1,2,3,4,5};
    private int currentPos = -1;
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
                if(currentPos <0){
                    currentPos = position;
                    currentView = (ImageView) view;
                    ((ImageView)view).setImageBitmap(drawable[pos[position]]);
                }
                else{
                    if (pos[currentPos] != pos[position]){
                        ((ImageView)view).setImageBitmap(drawable[pos[position]]);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                currentView.setImageResource(R.drawable.backofcard);
                                ((ImageView)view).setImageResource(R.drawable.backofcard);
                                //Possible toast for not matching
                            }
                        }, 500);
                    }
                    else{
                        ((ImageView)view).setImageBitmap(drawable[pos[position]]);
                        matches++;
                        Toast.makeText(getApplicationContext(), "Match!", Toast.LENGTH_SHORT).show();
                        updateMatchCount(matches);
                        if(matches==6){
                            endGame();
                        }
                    }
                    currentPos=-1;
                }
            }
        });
    }

    private void retrieveImages(){
        images = new ArrayList<Img>();
        //REMEMBER TO CODE TO FLUSH SEEDED FILES AND REPLACE WITH FETCHACTIVITY FILES
        //REMEMBER TO CODE TO FLUSH SEEDED FILES AND REPLACE WITH FETCHACTIVITY FILES
        //REMEMBER TO CODE TO FLUSH SEEDED FILES AND REPLACE WITH FETCHACTIVITY FILES
        //REMEMBER TO CODE TO FLUSH SEEDED FILES AND REPLACE WITH FETCHACTIVITY FILES
        //REMEMBER TO CODE TO FLUSH SEEDED FILES AND REPLACE WITH FETCHACTIVITY FILES

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
        int elapsedMillis = (int) (SystemClock.elapsedRealtime() - chronometer.getBase());
        int score = elapsedMillis/1000;
        intent.putExtra("score", score);
        startActivity(intent);
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

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }



}
