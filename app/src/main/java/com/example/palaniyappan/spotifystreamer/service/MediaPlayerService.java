package com.example.palaniyappan.spotifystreamer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.SeekBar;

import com.example.palaniyappan.spotifystreamer.PlaybackViewHolder;
import com.example.palaniyappan.spotifystreamer.Utility;

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
    PlaybackViewHolder mViewHolder;
    Handler mCompletionHandler;
    Handler mTimeUpdateHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    public void setSongUrl(String previewUrl) {
        this.mPreviewUrl = previewUrl;
    }

    public void setViewHolder(PlaybackViewHolder viewHolder) {
        this.mViewHolder = viewHolder;
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
        if(mMediaPlayer != null) {
            int total = mMediaPlayer.getDuration();
            //mPlayerSeekBar.setMax(total);
            while (currentPosition <= total) {
                currentPosition = mMediaPlayer.getCurrentPosition();
                mPlayerSeekBar.setProgress(currentPosition);
                Message msg = new Message();
                msg.obj = Utility.convertMilliSecToDesiredFormat(currentPosition);
                mTimeUpdateHandler.sendMessage(msg);
                //mTimeUpdateHandler.send;
            /*mViewHolder.playCurrentTimeView.
                    setText(Utility.convertMilliSecToDesiredFormat(currentPosition));*/
            }
        }
    }

    public void setCompletionHandler(Handler handler) {
        mCompletionHandler = handler;
    }

    public void setTimeUpdateHandler(Handler handler) {
        mTimeUpdateHandler = handler;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        //mMediaPlayer.release();
        return false;
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        player.start();
        int total = player.getDuration();
        mPlayerSeekBar.setMax(total);
        mViewHolder.totalPlayTimeView.setText(Utility.convertMilliSecToDesiredFormat(total));
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mCompletionHandler.sendEmptyMessage(0);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    synchronized public void playSong() {
        synchronized (this) {
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(mPreviewUrl);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            } catch(IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.prepareAsync();
        }
    }

    public void togglePlayPauseTrack() {
        if(mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mViewHolder.playTrackImageView.setImageResource(
                    android.R.drawable.ic_media_play);
        } else {
            mMediaPlayer.start();
            mViewHolder.playTrackImageView.setImageResource(
                    android.R.drawable.ic_media_pause);
        }
    }

    public class MusicBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }
}
