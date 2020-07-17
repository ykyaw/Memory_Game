package iss.team1.ca.memorygame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import iss.team1.ca.memorygame.R;
import iss.team1.ca.memorygame.adapter.MyAdapter;
import iss.team1.ca.memorygame.modal.Img;
import iss.team1.ca.memorygame.service.MusicPlayerService;

public class FetchActivity extends AppCompatActivity implements ServiceConnection {

    private static final String SAMPLE_IMG_URL="https://image.shutterstock.com/image-photo/mountains-during-sunset-beautiful-natural-260nw-407021107.jpg";

    EditText inputUrl;
    Button fetchBtn;
    ProgressBar progressBar;
    private String websiteUrl = "";

    private GridView gridView;
    MusicPlayerService musicPlayerService;

    private MyAdapter gridViewAdapter=null;
    private List<Img> imgList=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);

        //Binding to music service to allow music to unpause. Refer to onServiceConnected method
        Intent musicIntent = new Intent(this, MusicPlayerService.class);
        bindService(musicIntent, this, BIND_AUTO_CREATE);

        init();
    }

    private void init(){
        fetchBtn = (Button) findViewById(R.id.fetchBtn);
        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                websiteUrl = inputUrl.getText().toString();
                if (websiteUrl.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a url", Toast.LENGTH_LONG).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // clearImgs();
                            String htmlString = readHtmlFromUrl(websiteUrl);
                            if (htmlString.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "Error 404. Page Not Found.", Toast.LENGTH_LONG).show();
                            }
                            ArrayList<String> imgUrls = getImgUrls(htmlString);
                            downloadImgs(imgUrls);
                        }
                    }).start();
                }
            }
        });
        inputUrl = findViewById(R.id.inputUrl);

        gridView = (GridView) findViewById(R.id.grid_img);

        imgList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            imgList.add(new Img(R.drawable.ic_launcher_background));
        }

        gridViewAdapter=new MyAdapter<Img>((ArrayList)imgList,R.layout.item_grid_img){
            @Override
            public void bindView(ViewHolder holder, Img img) {
                holder.setImageResource(R.id.img,img.getUid());

            }
        };

        gridView.setAdapter(gridViewAdapter);

    }

    public String readHtmlFromUrl(String websiteUrl) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            URL url = new URL(websiteUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            //get input stream to read bits
            InputStream inputstream = conn.getInputStream();
            Scanner scanner=new Scanner(inputstream);
            while(scanner.hasNext()){
                stringBuffer.append(scanner.nextLine());
            }
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return stringBuffer.toString();
        }
    }

    public ArrayList<String> getImgUrls(String htmlText){
        ArrayList<String> imgUrls = new ArrayList<String>();
        String imgRegex = "(?i)<img+\\s+?data-cfsrc\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";

        Pattern p = Pattern.compile(imgRegex);
        Matcher m = p.matcher(htmlText);

        while (m.find()) {
            imgUrls.add(m.group(1));
        }
        return imgUrls;
    }

    public void downloadImgs(ArrayList<String> imgUrls) {
        int imageLen = 0;
        int totalSoFar = 0;
        int readLen = 0;
        Bitmap bitmap = null;
        int lastPercent = 0;
        byte[] imgBytes;
        for (int i = 0; i < 20; i++) {
            String imgUrl = imgUrls.get(i);
            try {
                URL url = new URL(imgUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                imageLen = conn.getContentLength();
                imgBytes = new byte[imageLen];

                InputStream in = url.openStream();
                BufferedInputStream bufIn = new BufferedInputStream(in, 2048);

                byte[] data = new byte[1024];
                while ((readLen = bufIn.read(data)) != -1) {
                    System.arraycopy(data, 0, imgBytes, totalSoFar, readLen);
                    totalSoFar += readLen;

                    int percent = Math.round(totalSoFar * 100) / imageLen;
                    if (percent - lastPercent >= 10) {
                        lastPercent = percent;
                    }
                }
                bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imageLen);
                //progressupdate
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
