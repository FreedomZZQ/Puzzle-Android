package studio.androiddev.puzzle;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.leakcanary.LeakCanary;

import cn.bmob.v3.Bmob;
import studio.androiddev.puzzle.dish.DishManager;
import studio.androiddev.puzzle.model.User;
import studio.androiddev.puzzle.utils.StaticValue;

/**
 * Puzzle
 * Created by ZQ on 2016/3/21.
 */
public class PuzzleApplication extends Application{

    private static Context mContext;

    private static User mUser;

    private static DishManager dm;

    public static int getLevel() {
        return level;
    }

    public static void setLevel(int level) {
        if(level < 3) return;
        PuzzleApplication.level = level;
        initDishManager();
    }

    private static int level = 4;

    @Override
    public void onCreate(){
        super.onCreate();
        LeakCanary.install(this);
        Bmob.initialize(this, StaticValue.bmobId);
        if(mContext == null) mContext = getApplicationContext();
        SharedPreferences pref = getSharedPreferences(StaticValue.SP_NAME, MODE_PRIVATE);
        setLevel(pref.getInt(StaticValue.SP_LEVEL, 4));
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

    public static void initDishManager(){
        dm = new DishManager(level);
    }

    public static DishManager getDishManager(){
        return dm;
    }
}
