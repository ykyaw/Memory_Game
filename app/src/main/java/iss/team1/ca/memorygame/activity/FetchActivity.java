package iss.team1.ca.memorygame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import iss.team1.ca.memorygame.R;
import iss.team1.ca.memorygame.adapter.MyAdapter;
import iss.team1.ca.memorygame.comm.CommonConstant;
import iss.team1.ca.memorygame.comm.utils.HttpUtil;
import iss.team1.ca.memorygame.modal.Img;

public class FetchActivity extends AppCompatActivity {

    private static final String SAMPLE_IMG_URL="https://image.shutterstock.com/image-photo/mountains-during-sunset-beautiful-natural-260nw-407021107.jpg";

    private GridView grid_img;

    private MyAdapter gridViewAdapter=null;
    private List<Img> imgList=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);

        init();


    }

    private void init(){
        grid_img=(GridView)findViewById(R.id.grid_img);

        imgList=new ArrayList<>();
        for(int i=0;i<20;i++){
            imgList.add(new Img(R.drawable.ic_launcher_background));
        }

        gridViewAdapter=new MyAdapter<Img>((ArrayList)imgList,R.layout.item_grid_img){
            @Override
            public void bindView(ViewHolder holder, Img img) {
                holder.setImageResource(R.id.img,img.getUid());
            }
        };

        grid_img.setAdapter(gridViewAdapter);

    }


}
