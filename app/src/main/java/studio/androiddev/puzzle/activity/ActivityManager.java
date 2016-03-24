package studio.androiddev.puzzle.activity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * The manager to manage activities
 * Puzzle
 * Created by ZQ on 2016/3/24.
 */
public class ActivityManager {

    /**
     * A list that holds all activities.
     */
    public static List<Activity> activities = new ArrayList<>();

    /**
     * Add Activity to the list.
     * @param activity The Activity you want to add in.
     */
    public static void addActivity(Activity activity){ activities.add(activity); }

    /**
     * Remove Activity from the list.
     * @param activity The Activity you want to remove.
     */
    public static void removeActivity(Activity activity){ activities.remove(activity); }

    /**
     * Finish all the activities to finish the app.
     */
    public static void finishAll(){
        for(Activity activity : activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
