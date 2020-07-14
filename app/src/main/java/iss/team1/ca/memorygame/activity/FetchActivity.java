package iss.team1.ca.memorygame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;

import iss.team1.ca.memorygame.R;
import iss.team1.ca.memorygame.comm.CommonConstant;
import iss.team1.ca.memorygame.comm.utils.HttpUtil;

public class FetchActivity extends AppCompatActivity {

    private static final String SAMPLE_IMG_URL="https://image.shutterstock.com/image-photo/mountains-during-sunset-beautiful-natural-260nw-407021107.jpg";

    private GridView grid_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);

        bindView();


    }

    private void bindView(){
        grid_img=(GridView)findViewById(R.id.grid_img);
    }


}
