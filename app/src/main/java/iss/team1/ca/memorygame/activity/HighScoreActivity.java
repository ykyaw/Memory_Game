package iss.team1.ca.memorygame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import iss.team1.ca.memorygame.R;
import iss.team1.ca.memorygame.adapter.MyAdapter;
import iss.team1.ca.memorygame.bean.Score;
import iss.team1.ca.memorygame.bean.User;
import iss.team1.ca.memorygame.comm.CommonConstant;
import iss.team1.ca.memorygame.comm.utils.ApplicationUtil;
import iss.team1.ca.memorygame.comm.utils.HttpUtil;
import iss.team1.ca.memorygame.comm.utils.JSONUtil;

public class HighScoreActivity extends AppCompatActivity implements ServiceConnection {

    private ListView highScoreList;
    MusicPlayerService musicPlayerService;

    private MyAdapter highScoreAdapter=null;

    private List<Score> scoreList=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        //Binding to music service to allow music to unpause. Refer to onServiceConnected method
        Intent musicIntent = new Intent(this, MusicPlayerService.class);
        bindService(musicIntent, this, BIND_AUTO_CREATE);
        init();
    }

    private void init(){
        highScoreList=(ListView)findViewById(R.id.high_score_list);

        //data initial
        HttpUtil.getInstance()
                .sendStringRequest(CommonConstant.HttpUrl.HIGH_SCORES,new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response!=null) {
                            scoreList= (List<Score>) JSONUtil.JsonToObject(response, new TypeToken<List<Score>>() {}.getType());

                            //adapter initial
                            highScoreAdapter=new MyAdapter<Score>((ArrayList) scoreList,R.layout.item_score) {
                                @Override
                                public void bindView(ViewHolder holder, Score score) {
                                    holder.setText(R.id.high_score_username,score.getOwner().getUsername());
                                    holder.setText(R.id.high_score,score.getScore()+"s");
                                }
                            };

                            //bind list adapter
                            highScoreList.setAdapter(highScoreAdapter);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("error");
                        error.printStackTrace();
                        System.out.println(error.getMessage());

                    }
                });
    }

    @Override
    public void onPause(){
        super.onPause();
        if(musicPlayerService!=null){
            musicPlayerService.pauseMusic();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(musicPlayerService!=null){
            musicPlayerService.unpauseMusic();
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicPlayerService.MyBinder binder = (MusicPlayerService.MyBinder) iBinder;
        if(binder != null) {
            musicPlayerService = binder.getService();
            musicPlayerService.unpauseMusic();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

}
