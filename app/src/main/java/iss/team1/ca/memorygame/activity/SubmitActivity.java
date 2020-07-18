package iss.team1.ca.memorygame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import iss.team1.ca.memorygame.R;
import iss.team1.ca.memorygame.comm.BaseActivity;
import iss.team1.ca.memorygame.comm.CommonConstant;
import iss.team1.ca.memorygame.comm.utils.ActivityCollector;
import iss.team1.ca.memorygame.comm.utils.HttpUtil;
import iss.team1.ca.memorygame.service.MusicPlayerService;

public class SubmitActivity extends BaseActivity implements ServiceConnection {

    Button submit;
    Button play_again;
    TextView player_name;
    TextView game_result;
    int score;
    String userId;
    MusicPlayerService musicPlayerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);


        Intent intent = getIntent();
        score = intent.getIntExtra("score", 10000);

        //Binding to music service to allow music change. Refer to onServiceConnected method
        Intent musicIntent = new Intent(this, MusicPlayerService.class);
        bindService(musicIntent, this, BIND_AUTO_CREATE);

        init();
    }

    private void init(){
        submit=(Button)findViewById(R.id.submit);
        play_again=(Button)findViewById(R.id.play_again);
        player_name=(TextView)findViewById(R.id.player_name);
        game_result=(TextView)findViewById(R.id.game_result);

        SharedPreferences pref = getSharedPreferences("user_info", MODE_PRIVATE);
        if(pref.contains("username")&&pref.contains("uid")){
            player_name.setText(pref.getString("username",""));
            userId=pref.getString("uid","0");
        }

        game_result.setText(score+"s");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,String> params=new HashMap<>();
                params.put("uid",userId+"");
                params.put("score",score+"");
                HttpUtil.getInstance()
                        .sendStringRequest(Request.Method.POST, CommonConstant.HttpUrl.SUBMIT_SCORE, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                ActivityCollector.finishAll();
                                startMainActivity();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        },params);
            }
        });

        play_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SubmitActivity.this,PlayingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startMainActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
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
            //musicPlayerService.playVictoryMusic();
            musicPlayerService.unpauseMusic();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
}
