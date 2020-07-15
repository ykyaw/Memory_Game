package iss.team1.ca.memorygame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import iss.team1.ca.memorygame.R;
import iss.team1.ca.memorygame.adapter.MyAdapter;
import iss.team1.ca.memorygame.bean.Score;
import iss.team1.ca.memorygame.bean.User;

public class HighScoreActivity extends AppCompatActivity {

    private ListView highScoreList;

    private MyAdapter highScoreAdapter=null;

    private List<User> scoreList=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        init();
    }

    private void init(){
        highScoreList=(ListView)findViewById(R.id.high_score_list);

        //data initial
        scoreList=new ArrayList<>();
        for(int i=0;i<10;i++){
            List<Score> scores=new ArrayList<>();
            scores.add(new Score(i,new User(i),Long.parseLong(i+""),Long.parseLong(i+"")));
            User user = new User(i, i + "", scores);
            scoreList.add(user);
        }

        //adapter initial
        highScoreAdapter=new MyAdapter<User>((ArrayList) scoreList,R.layout.item_score) {
            @Override
            public void bindView(ViewHolder holder, User user) {
                holder.setText(R.id.high_score_username,user.getUsername());
                holder.setText(R.id.high_score,user.getScores().get(0).getScore()+"s");
            }
        };

        //bind list adapter
        highScoreList.setAdapter(highScoreAdapter);
    }
}
