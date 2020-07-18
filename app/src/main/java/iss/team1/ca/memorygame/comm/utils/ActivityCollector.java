package iss.team1.ca.memorygame.comm.utils;

import android.app.Activity;

import java.util.LinkedList;

public class ActivityCollector {

    public static LinkedList<Activity> activities = new LinkedList<Activity>();

    public static void addActivity(Activity activity)
    {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity)
    {
        activities.remove(activity);
    }

    public static void finishAll()
    {
        for(Activity activity:activities)
        {
            if(!activity.isFinishing())
            {
                activity.finish();
            }
        }
    }

    public static void goToMainActivity(){
        for(int i=activities.size()-1;i>0;i--){
            activities.get(i).finish();
            activities.remove(activities.get(i));
        }
    }
}
