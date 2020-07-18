package iss.team1.ca.memorygame.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import iss.team1.ca.memorygame.R;
import iss.team1.ca.memorygame.bean.User;
import iss.team1.ca.memorygame.comm.BaseActivity;
import iss.team1.ca.memorygame.comm.CommonConstant;
import iss.team1.ca.memorygame.comm.utils.HttpUtil;
import iss.team1.ca.memorygame.comm.utils.JSONUtil;
import iss.team1.ca.memorygame.service.MusicPlayerService;

public class MainActivity extends BaseActivity implements ServiceConnection {

    Button play;
    Button credits;
    Button howtoplay;
    Button score;
    TextView name;

    MusicPlayerService musicPlayerService;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=MainActivity.this;

        //Binding to music service to allow music change. Refer to onServiceConnected method
        Intent musicIntent = new Intent(this, MusicPlayerService.class);
        bindService(musicIntent, this, BIND_AUTO_CREATE);

        init();
    }

    private void init(){
        play=(Button) findViewById(R.id.play);
        credits=(Button) findViewById(R.id.credits);
        howtoplay=(Button) findViewById(R.id.howtoplay);
        score=(Button) findViewById(R.id.score);
        name=(TextView) findViewById(R.id.name);


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAccount()){
                    startPlayActivity();
                }else{
                    customEditTextDialog(view);
                }
            }
        });

        score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,HighScoreActivity.class);
                startActivity(intent);
            }
        });

        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,CreditActivity.class);
                startActivity(intent);
            }
        });

        howtoplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,HowtoplayActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkAccount(){
        SharedPreferences pref = getSharedPreferences("user_info", MODE_PRIVATE);
        if(pref.contains("username")&&pref.contains("uid")){
            name.setText("Welcome, "+pref.getString("username","")+" ^_^");
            return true;
        }
        return false;
    }

    private void startPlayActivity(){
        Intent intent=new Intent(context,FetchActivity.class);
        startActivity(intent);
    }

    /**
     * custom EditText dialog
     **/
    public void customEditTextDialog(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                android.R.style.Theme_Material_Light_Dialog_Alert);
        View Tittleview = getLayoutInflater().inflate(
                R.layout.custom_edit_dialog, null);
        TextView textView = (TextView) Tittleview.findViewById(R.id.create_account_title);

        textView.setText("CREATE ACCOUNT");
        // customize tittle
        builder.setCustomTitle(Tittleview);

        View contentView = getLayoutInflater().inflate(
                R.layout.custom_edit_dialog_content, null);
        final EditText username = (EditText) contentView.findViewById(R.id.username);

        builder.setView(contentView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String,String> params=new HashMap<>();
                if(username.getText().toString().equals("")){
                    Toast.makeText(context,"username can not be empty",Toast.LENGTH_LONG).show();
                    return;
                }
                params.put("username",username.getText().toString());
                HttpUtil.getInstance()
                        .sendStringRequest(Request.Method.POST, CommonConstant.HttpUrl.CREATE_ACCOUNT, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                User user= (User) JSONUtil.JsonToObject(response,User.class);
                                SharedPreferences pref=getSharedPreferences("user_info",MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();

                                editor.putString("username",user.getUsername());
                                editor.putString("uid",user.getUid()+"");
                                editor.commit();

                                startPlayActivity();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Toast.makeText(context, "create account fail", Toast.LENGTH_SHORT).show();
                            }
                        },params);
            }
        }).setNegativeButton("Cancle", null)
                .create().show();

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
        checkAccount();
        if(musicPlayerService!=null){
            musicPlayerService.unpauseMusic();
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicPlayerService.MyBinder binder = (MusicPlayerService.MyBinder) iBinder;
        if(binder != null) {
            musicPlayerService = binder.getService();
            musicPlayerService.playMusic(1);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

}
