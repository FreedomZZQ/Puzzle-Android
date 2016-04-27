package studio.androiddev.puzzle.utils;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * puzzle
 * Created by ZQ on 2016/4/24.
 */
public class GameTimer {
    public final static int MESSAGE_TIMER = 0x100;
    private static final int INTERVAL_TIME = 1000;
    private Handler[] mHandler;
    private Timer mTimer;
    private TimerTask mTimerTask;

    private int what;
    private boolean mTimerStart = false;

    public void recycle(){
        stopTimer();
        mTimer = null;
        mTimerTask = null;
        for(Handler handler : mHandler){
            handler = null;
        }
        mHandler = null;
    }

    public GameTimer(Handler... handler){
        this.mHandler = handler;
        this.what = MESSAGE_TIMER;

        mTimer = new Timer();
    }

    public void startTimer(){
        if(mHandler == null || mTimerStart){
            return;
        }
        mTimerTask = new MyTimerTask();
        mTimer.schedule(mTimerTask, INTERVAL_TIME, INTERVAL_TIME);
        mTimerStart = true;
    }

    public void stopTimer(){
        if(!mTimerStart){
            return;
        }
        mTimerStart = false;
        if(mTimerTask != null){
            mTimerTask.cancel();
            mTimerTask = null;
        }
        mTimer.cancel();

    }

    class MyTimerTask extends TimerTask{
        @Override
        public void run(){
            if(mHandler != null){
                for(Handler handler : mHandler){
                    Message msg = handler.obtainMessage(what);
                    msg.sendToTarget();
                }
            }
        }
    }
}
