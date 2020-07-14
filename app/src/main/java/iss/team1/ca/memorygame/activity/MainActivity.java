package iss.team1.ca.memorygame.activity;

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void bindView(){
        btn=findViewById(R.id.btn);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Map<String,String> parmas=new HashMap<>();
        switch (id){
            case R.id.btn:
                HttpUtil.getInstance()
                        .sendStringRequest(Request.Method.POST, CommonConstant.HttpUrl.SENDMSG, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response!=null) {
                                    System.out.println(response);

                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        },parmas);
                break;
        }
    }
}
