package studio.androiddev.puzzle;

import android.app.Application;
import android.content.Context;

import studio.androiddev.puzzle.dish.DishManager;
import studio.androiddev.puzzle.model.User;

/**
 * Puzzle
 * Created by ZQ on 2016/3/21.
 */
public class PuzzleApplication extends Application{

    private static Context mContext;

    private static User mUser;

    private static DishManager dm;

    @Override
    public void onCreate(){
        super.onCreate();
        if(mContext == null) mContext = getApplicationContext();
    }

    public static User getmUser() {
        return mUser;
    }

    public static void setmUser(User mUser) {
        PuzzleApplication.mUser = mUser;
    }

    public static Context getAppContext(){
        return mContext;
    }

    public static void initDishManager(int level){
        dm = new DishManager(level);
    }

    public static DishManager getDishManager(){
        return dm;
    }
}
