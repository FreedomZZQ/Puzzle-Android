package studio.androiddev.puzzle;

import android.app.Application;
import android.content.Context;

/**
 * Puzzle
 * Created by ZQ on 2016/3/21.
 */
public class PuzzleApplication extends Application{

    private static Context mContext;

    @Override
    public void onCreate(){
        super.onCreate();
        if(mContext == null) mContext = getApplicationContext();
    }

    public static Context getAppContext(){ return mContext; }
}
