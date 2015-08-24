package com.androiddevelopment.spotifystreamer2;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class Utils extends Service implements MediaPlayer.OnCompletionListener  {

    public static String PLAY = ".PLAY";
    public static String PAUSE = ".PAUSE";
    public static String STOP = ".STOP";
    public static String GET_SEEK = ".GET_SEEK";
    public static String UPDATE_SEEK = ".UPDATE_SEEK";
    public static String CHANGE_SEEK = ".CHANGE_SEEK";
    public static String GET_MAX = ".GET_MAX";
    public static String UPDATE_MAX = ".UPDATE_MAX";
    private boolean startPlaying = false;

    private MediaPlayer mMediaPlayer = null;
    private final Handler handler = new Handler();
    private Intent intent;

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            if(mMediaPlayer != null) {
                sendSeek();
            }
            handler.postDelayed(this, 200);
        }
    };

    private BroadcastReceiver receiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mMediaPlayer = new MediaPlayer();
        intent = new Intent(UPDATE_SEEK);
        IntentFilter filter = new IntentFilter();
        filter.addAction(PLAY);
        filter.addAction(PAUSE);
        filter.addAction(STOP);
        filter.addAction(GET_MAX);
        filter.addAction(GET_SEEK);
        filter.addAction(CHANGE_SEEK);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals(PLAY)) {
                    Log.i("service","play media");
                    if(mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                        mMediaPlayer.start();
                    }
                    else if(mMediaPlayer == null) {
                        startPlaying = true;
                    }
                }
                else if(action.equals(PAUSE)) {
                    Log.i("service","pause media");
                    if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                    }
                }
                else if(action.equals(STOP)) {
                }
                else if(action.equals(GET_SEEK)) {
                    sendSeek();
                }
                else if(action.equals(GET_MAX)) {
                    sendMax();
                }
                else if(action.equals(CHANGE_SEEK)) {
                    int seek = intent.getIntExtra("seekto", 0);
                    if(mMediaPlayer != null) {
                        mMediaPlayer.seekTo(seek);
                    }
                }
            }
        };
        registerReceiver(receiver, filter);
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            String url = intent.getStringExtra("previewUrl");
            handler.removeCallbacks(sendUpdatesToUI);
            handler.postDelayed(sendUpdatesToUI, 200);

            try {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        sendMax();
                        if(startPlaying)
                            mMediaPlayer.start();
                    }
                });
                mMediaPlayer.prepareAsync();

            } catch (IOException e) {
                Log.w("IO Exception: ", e.toString());
            }
        }
        return START_STICKY;
    }
    public void onDestroy() {
        Log.i("service","destroying service");
        handler.removeCallbacks(sendUpdatesToUI);
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
        unregisterReceiver(receiver);
    }
    public void onCompletion(MediaPlayer _mediaPlayer) {
        stopSelf();
    }
    private void sendMax() {
        if(mMediaPlayer != null) {
            Intent maxIntent = new Intent(UPDATE_MAX);
            maxIntent.putExtra("max", mMediaPlayer.getDuration());
            sendBroadcast(maxIntent);
        }
    }
    private void sendSeek() {
        try{
            intent.putExtra("playerPosition", mMediaPlayer.getCurrentPosition());
            sendBroadcast(intent);
        }
        catch(Exception e) {
            Log.w("service", e.toString());
        }
    }
}