package studio.androiddev.puzzle.bgm;

/**
 * Created by Administrator on 2016/3/29.
 */

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

import studio.androiddev.puzzle.R;

public class MusicServer extends Service {

    private MediaPlayer mediaPlayer;
    private MyBinder myBinder=new MyBinder();
    private int songList[]={R.raw.a, R.raw.b, R.raw.c};
    private int songIndex = 0;
    @Override   public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        onStart(intent,0);
        return myBinder;
    }
    @Override
    public  void onStart(Intent intent,int startId){
        super.onStart(intent, startId);

        if(mediaPlayer==null){
// R.raw.mmp是资源文件，MP3格式的
            try {
                songplay();
                mediaPlayer.setOnCompletionListener(new CompletionListener());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private final class CompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            try {
                nextsong();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public void nextsong() throws IOException {

        if (songIndex < songList.length - 1) {
            songIndex = songIndex + 1;
            songplay();
        }
        else {
            songIndex = 0;
            songplay();
        }


    }
    private void songplay() throws IOException {
        try {
            if(mediaPlayer!=null)
                mediaPlayer.release();
            mediaPlayer=MediaPlayer.create(this, songList[songIndex]);
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
// TODO Auto-generated method stub
        super.onDestroy();
        mediaPlayer.stop();
    }
    public class MyBinder extends Binder{
        public MusicServer getService(){
            return MusicServer.this;
        }
    }
}