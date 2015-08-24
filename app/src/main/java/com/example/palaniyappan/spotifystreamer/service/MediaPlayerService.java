package com.example.palaniyappan.spotifystreamer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.SeekBar;

import java.io.IOException;

/**
 * Created by Pal on 8/23/15.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    MediaPlayer mMediaPlayer = null;
    String mPreviewUrl;
    private final IBinder mMusicBind = new MusicBinder();
    SeekBar mPlayerSeekBar;

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    public void setSongUrl(String previewUrl) {
        this.mPreviewUrl = previewUrl;
    }

    public void bindProgressBar(SeekBar seekBar) {
        this.mPlayerSeekBar = seekBar;
        this.mPlayerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                    Log.v("SeekBarTest", String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void handleSeekBar() {
        int currentPosition = 0;
        int total = mMediaPlayer.getDuration();
        Log.v("SeekBarTest", String.valueOf(total));
        //mPlayerSeekBar.setMax(total);
        while (mMediaPlayer != null && currentPosition < total) {
            currentPosition = mMediaPlayer.getCurrentPosition();
            Log.v("SeekBarTest", String.valueOf(currentPosition));
            mPlayerSeekBar.setProgress(currentPosition);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        player.start();
        int total = player.getDuration();
        Log.v("SeekBarTest", String.valueOf(total));
        mPlayerSeekBar.setMax(total);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    public void playSong() {
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(mPreviewUrl);
        } catch(IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
    }



    public class MusicBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }
}
