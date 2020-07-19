package iss.team1.ca.memorygame.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import iss.team1.ca.memorygame.R;
import iss.team1.ca.memorygame.adapter.MyAdapter;
import iss.team1.ca.memorygame.comm.BaseActivity;
import iss.team1.ca.memorygame.modal.Img;
import iss.team1.ca.memorygame.service.MusicPlayerService;

public class FetchActivity extends BaseActivity implements ServiceConnection {

    EditText inputUrl;
    Button fetchBtn;
    Button playBtn;
    TextView progressTextView;
    ProgressBar mProgressBar;

    private GridView gridView;

    MusicPlayerService musicPlayerService;

    private MyAdapter gridViewAdapter=null;
    private List<Img> imgList=null;
    private boolean stopThread=false;
    private String websiteUrl = "";


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
        progressTextView = (TextView) findViewById(R.id.progressTxt);
        playBtn = (Button) findViewById(R.id.playBtn);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        gridView = (GridView) findViewById(R.id.grid_img);

        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Hide Android Keyboard
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }



                playBtn.setVisibility(View.GONE);

                if (mProgressBar.getProgress()!=0 && mProgressBar.getProgress()!=100){
                    stop();
                    resetGridView();
                }

                websiteUrl = inputUrl.getText().toString();
                if (websiteUrl.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a URL", Toast.LENGTH_LONG).show();
                } else if (!URLUtil.isValidUrl(websiteUrl)) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid URL", Toast.LENGTH_LONG).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                                String htmlString = readHtmlFromUrl(websiteUrl);
                                ArrayList<String> imgUrls = getImgUrls(htmlString);

                                if(htmlString.isEmpty()){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "No page found from this URL", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                else if (imgUrls.size()<20){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Not enough images to load from this URL", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressTextView.setVisibility(View.VISIBLE);
                                            progressTextView.setText("Downloading...");
                                        }
                                    });
                                    stopThread=false;
                                    downloadImgs(imgUrls);
                                }
                            }
                        }).start();
                    }
                }
            });
        inputUrl = findViewById(R.id.inputUrl);
        resetGridView();
    }

    private void resetGridView() {

        imgList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            imgList.add(new Img(R.drawable.ic_launcher_background));
        }

        gridViewAdapter=new MyAdapter<Img>((ArrayList)imgList,R.layout.item_grid_img){
            @Override
            public void bindView(ViewHolder holder, Img img) {
                holder.setImageBitmap(R.id.img,img.getRes());
            }
        };

        gridView.setAdapter(gridViewAdapter);

        if (imgList.get(0).getRes() != null) {
            setGridViewOnClick();
        } else {
            gridView.setEnabled(false);
        }
    }

    void setGridViewOnClick() {
        gridView.setEnabled(true);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        startPlayingActivity();
                    }
                });
                
                imgList.get(position).toggleSelected();

                Animation flip = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.flip);
                if (imgList.get(position).isSelected()) {
                    view.startAnimation(flip);
                    view.setAlpha(0.9f);
                    view.setBackgroundColor(Color.parseColor("#16BFC4"));
                } else {
                    view.startAnimation(flip);
                    view.setAlpha(1);
                    view.setBackgroundColor(Color.TRANSPARENT);
                }

                gridViewAdapter.notifyDataSetChanged();
                int selectedCount = getSelectedCount();
                if (selectedCount < 6) {
                    playBtn.setVisibility(View.GONE);
                    progressTextView.setVisibility(View.VISIBLE);
                    progressTextView.setText(selectedCount + "/6 images selected");
                    progressTextView.setTextColor(Color.BLACK);
                } else if (selectedCount > 6) {
                    playBtn.setVisibility(View.GONE);
                    progressTextView.setVisibility(View.VISIBLE);
                    progressTextView.setText(selectedCount + "/6 images selected");
                    progressTextView.setTextColor(Color.RED);
                } else {
                    playBtn.setVisibility(View.VISIBLE);
                    progressTextView.setVisibility(View.GONE);
                }
            }

        });
    }

    public int getSelectedCount() {
        int count = 0;
        for (int i = 0; i < 20; i++) {
            if (imgList.get(i).isSelected()) {
                count++;
            }
        }
        return count;
    }

    private void stop() {
        stopThread=true;
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
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }


    public ArrayList<String> getImgUrls(String htmlText){
        ArrayList<String> imgUrls = new ArrayList<String>();
        String imgRegex = "(?i)<img+\\s+?data-cfsrc\\s*=\\s*['\"](?!.*shutterstock)([^'\"]+)['\"][^>]*>";

        Pattern p = Pattern.compile(imgRegex);
        Matcher m = p.matcher(htmlText);

        while (m.find()) {
            imgUrls.add(m.group(1));
        }
        return imgUrls;
    }



    public void downloadImgs(ArrayList<String> imgUrls) {
        Bitmap bitmap = null;
        for (int i = 0; i < 20; i++) {
            if(stopThread)
                return;
            String imgUrl = imgUrls.get(i);
            try {
                InputStream in = null;
                URL url = new URL(imgUrl);
                int response = -1;

                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                response = httpConn.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    in = httpConn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    Img img = new Img();
                    img.setUid(i);
                    img.setRes(bitmap);
                    imgList.set(i, img);
                    final int indexImgDL = i + 1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gridViewAdapter=new MyAdapter<Img>((ArrayList)imgList,R.layout.item_grid_img){
                                @Override
                                public void bindView(ViewHolder holder, Img img) {
                                    holder.setImageBitmap(R.id.img,img.getRes());
                                }
                            };
                            gridView.setAdapter(gridViewAdapter);

                            int percentCompleted = indexImgDL * 5;
                            progressTextView.setText("Downloaded " + indexImgDL +" of 20 images");
                            progressTextView.setTextColor(Color.BLACK);

                            if (percentCompleted == 100) {
                                mProgressBar.setVisibility(View.GONE);
                                progressTextView.setText("Select 6 images to play" );
                            } else {
                                mProgressBar.setVisibility(View.VISIBLE);
                                mProgressBar.setProgress(percentCompleted);
                            }
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setGridViewOnClick();
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

    void startPlayingActivity() {
        saveSelectedBmps();
        Intent intent = new Intent(this, PlayingActivity.class);
        startActivity(intent);
    }

    void saveSelectedBmps() {
        int number = 0;
        for (int i = 0; i < 20; i++) {
            if (imgList.get(i).isSelected()) {
                String filename = "bitmap" + number;
                File file = new File(getApplicationContext().getFilesDir(), filename);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    imgList.get(i).getRes().compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                number++;
            }
        }
    }
}
