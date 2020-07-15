package iss.team1.ca.memorygame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import iss.team1.ca.memorygame.R;
import iss.team1.ca.memorygame.comm.CommonConstant;
import iss.team1.ca.memorygame.comm.utils.HttpUtil;

public class SubmitActivity extends AppCompatActivity {

    Button submit;
    TextView player_name;
    TextView game_result;
    int score;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        Intent intent = getIntent();
        score = intent.getIntExtra("score", 10000);

        init();
    }

    private void init(){
        submit=(Button)findViewById(R.id.submit);
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
    }

    private void startMainActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
