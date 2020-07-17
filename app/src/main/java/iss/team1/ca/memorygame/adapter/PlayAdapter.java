package iss.team1.ca.memorygame.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import iss.team1.ca.memorygame.R;

public class PlayAdapter extends BaseAdapter {

    private Context context;
    public PlayAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return 12; //we have 12 images to play
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View cview, ViewGroup viewGroup) {
        ImageView imageView;
        if(cview == null){
            imageView = new ImageView(this.context);
            imageView.setLayoutParams(new GridView.LayoutParams(350,350));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }else {
            imageView = (ImageView) cview;
        }
        imageView.setImageResource(R.drawable.turtlecard);
        return imageView;
    }
}
