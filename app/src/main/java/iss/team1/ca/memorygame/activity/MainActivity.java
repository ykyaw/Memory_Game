package iss.team1.ca.memorygame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import iss.team1.ca.memorygame.R;
import iss.team1.ca.memorygame.comm.CommonConstant;
import iss.team1.ca.memorygame.comm.utils.HttpUtil;

public class MainActivity extends AppCompatActivity  {

    Button play;
    Button credits;
    Button score;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=MainActivity.this;

        bindView();
    }

    private void bindView(){
        play=(Button) findViewById(R.id.play);
        credits=(Button) findViewById(R.id.credits);
        score=(Button) findViewById(R.id.score);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,FetchActivity.class);
                startActivity(intent);
            }
        });
    }

//    @Override
//    public void onClick(View view) {
//        int id = view.getId();
//        System.out.println("click id: "+id);
//        Map<String,String> parmas=new HashMap<>();
//        Intent intent=null;
//        switch (id){
//            case R.id.play:
//                System.out.println("click play");
//                intent=new Intent(context,FetchActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.credits:
//                intent=new Intent(context,CreditActivity.class);
//                startActivity(intent);
//                break;
//        }
//    }
}
