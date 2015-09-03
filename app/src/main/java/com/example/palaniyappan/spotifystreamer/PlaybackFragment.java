package com.example.palaniyappan.spotifystreamer;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.palaniyappan.spotifystreamer.service.MediaPlayerService;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

/**
 * Created by Pal on 8/19/15.
 */
public class PlaybackFragment extends DialogFragment {
    private Map<Integer, TopTrackParcelable> mTopTracksMap;
    private Integer mCurrentTrackPosition;
    private TopTrackParcelable mSelectedTrack;
    private PlaybackViewHolder mPlaybackViewHolder;
    private List<TopTrackParcelable> topTracksList;
    private MediaPlayerService mMediaPlayerService;
    private Intent mPlayIntent;
    private boolean mMusicBound=false;
    private boolean mScreenRotated = false;
    private Bundle mSavedData;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UpdateSeekBarTask task = new UpdateSeekBarTask();
            task.execute();
        }
    };

    private Handler mTrackCompletionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //playNextTrack();
        }
    };

    private Handler mTimeUpdateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateSeekTime((String)msg.obj);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View rootView = inflater.inflate(R.layout.playback_layout, container, false);

        Bundle args = getArguments();
        if(args != null) {
            mSelectedTrack = args.getParcelable(
                    SpotifyStreamerConstants.SELECTED_TRACK_DETAILS);

            topTracksList = args.getParcelableArrayList(
                    SpotifyStreamerConstants.KEY_TOP_TRACK_RESULT);
            mCurrentTrackPosition = args.getInt(
                    SpotifyStreamerConstants.KEY_CURRENT_SELECTED_TRACK_POSITION, 1);

            if(topTracksList != null) {
                mTopTracksMap = Utility.getTopTracksMap(topTracksList);
            }

            mHandler.postDelayed(run, 1000);
            // Set various UI fields from the Arguments received from Parent Activity
            mPlaybackViewHolder = new PlaybackViewHolder(rootView);
            loadPlaybackView();

            mPlaybackViewHolder.previousTrackImageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mCurrentTrackPosition == 1) {
                                mCurrentTrackPosition = topTracksList.size();
                            } else {
                                mCurrentTrackPosition--;
                            }
                            mSelectedTrack = mTopTracksMap.get(mCurrentTrackPosition);

                            // Update the view with appropriate data
                            loadPlaybackView();
                            if (mMediaPlayerService != null) {
                                mMediaPlayerService.setSongUrl(mSelectedTrack.getPreviewUrl());
                                mMediaPlayerService.playSong();
                            }
                        }
                    });

            mPlaybackViewHolder.nextTrackImageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mCurrentTrackPosition == topTracksList.size()) {
                                mCurrentTrackPosition = 1;
                            } else {
                                mCurrentTrackPosition++;
                            }
                            mSelectedTrack = mTopTracksMap.get(mCurrentTrackPosition);

                            // Update the view with appropriate data
                            loadPlaybackView();
                            if(mMediaPlayerService != null) {
                                mMediaPlayerService.setSongUrl(mSelectedTrack.getPreviewUrl());
                                mMediaPlayerService.playSong();
                            }
                        }
                    });

            mPlaybackViewHolder.playTrackImageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mMediaPlayerService.togglePlayPauseTrack();
                        }
                    });

            if(savedInstanceState != null
                    && savedInstanceState.getParcelable("currentState") != null) {
                mScreenRotated = true;
                mSavedData = savedInstanceState.getParcelable("currentState");
                //updateSeekTime(String.valueOf(seekBarProgress));
            }

        }

        return rootView;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    //connect to the service
    private ServiceConnection mMusicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.MusicBinder binder = (MediaPlayerService.MusicBinder)service;
            //get service
            mMediaPlayerService = binder.getService();
            mMediaPlayerService.bindProgressBar(mPlaybackViewHolder.playProgressBar);
            mMediaPlayerService.setViewHolder(mPlaybackViewHolder);
            mMediaPlayerService.setCompletionHandler(mTrackCompletionHandler);
            mMediaPlayerService.setTimeUpdateHandler(mTimeUpdateHandler);
            //pass list
            mMediaPlayerService.setSongUrl(mSelectedTrack.getPreviewUrl());
            mMusicBound = true;
            mMediaPlayerService.playSong();
            mPlaybackViewHolder.playTrackImageView.setImageResource(
                    android.R.drawable.ic_media_pause);
            if(mScreenRotated && mSavedData != null) {
                mMediaPlayerService.setmSavedData(mSavedData);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        boolean showDialog = getResources().getBoolean(R.bool.show_dialog);

        if(showDialog) {
            getDialog().getWindow().setLayout(600, 600);
            mPlaybackViewHolder.artistNameView.setPadding(0, 20, 0, 0);
        }
        if(mPlayIntent == null) {
            mPlayIntent = new Intent(getActivity(), MediaPlayerService.class);
            getActivity().startService(mPlayIntent);
            getActivity().bindService(mPlayIntent, mMusicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    Runnable run = new Runnable() {

        @Override
        public void run() {
            if(mMediaPlayerService != null) {
                mHandler.sendEmptyMessage(0);
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    private void loadPlaybackView() {
        mPlaybackViewHolder.artistNameView.setText(mSelectedTrack.getArtistName());
        mPlaybackViewHolder.albumNameView.setText(mSelectedTrack.getAlbumName());
        mPlaybackViewHolder.trackNameView.setText(mSelectedTrack.getTrackName());
        if(mSelectedTrack.getAlbumImageUrl() != null &&
                !mSelectedTrack.getAlbumImageUrl().isEmpty()) {
            Picasso.with(getActivity())
                    .load(mSelectedTrack.getAlbumImageUrl())
                    .into(mPlaybackViewHolder.trackThumbnailView);
        }
    }

    public void playNextTrack() {
        if(mCurrentTrackPosition == topTracksList.size()) {
            mCurrentTrackPosition = 1;
        } else {
            mCurrentTrackPosition++;
        }
        mSelectedTrack = mTopTracksMap.get(mCurrentTrackPosition);

        // Update the view with appropriate data
        loadPlaybackView();
        if(mMediaPlayerService != null) {
            mMediaPlayerService.setSongUrl(mSelectedTrack.getPreviewUrl());
            mMediaPlayerService.playSong();
        }
    }

    public void updateSeekTime(String currentPlayingTime) {
        mPlaybackViewHolder.playCurrentTimeView.
                setText(currentPlayingTime);
    }

    @Override
    public void onDestroy() {
        //mMediaPlayerService.stopSelf();
        //mMediaPlayerService.stopService(mPlayIntent);
        //getActivity().stopService(mPlayIntent);
        //UpdateSeekBarTask task = new UpdateSeekBarTask();
        //task.cancel(true);

        mHandler.removeCallbacksAndMessages(null);
        mTrackCompletionHandler.removeCallbacksAndMessages(null);
        mTimeUpdateHandler.removeCallbacksAndMessages(null);
        if(mMusicBound) {
            getActivity().unbindService(mMusicConnection);
        }
        mMediaPlayerService = null;
        super.onDestroy();

        /*boolean showDialog = getResources().getBoolean(R.bool.show_dialog);
        if(!showDialog) {
            getActivity().finish();
        }*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Bundle args = mMediaPlayerService.getDataForScreenRotation();
        outState.putParcelable("currentState", args);
        super.onSaveInstanceState(outState);
    }

    // Async Task to load the artist result set for the search text.
    class UpdateSeekBarTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if(mMediaPlayerService != null) {
                mMediaPlayerService.handleSeekBar();
            }
            return null;
        }
    }
}
