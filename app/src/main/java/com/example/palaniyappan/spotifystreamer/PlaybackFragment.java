package com.example.palaniyappan.spotifystreamer;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.palaniyappan.spotifystreamer.service.MediaPlayerService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * Created by Pal on 8/19/15.
 */
public class PlaybackFragment extends DialogFragment {
    private Map<Integer, TopTrackParcelable> mTopTracksMap;
    private Integer mCurrentTrackPosition;
    private TopTrackParcelable mSelectedTrack;
    private PlaybackViewHolder mPlaybackViewHolder;

    private MediaPlayerService mMediaPlayerService;
    private Intent mPlayIntent;
    private boolean mMusicBound=false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UpdateSeekBarTask task = new UpdateSeekBarTask();
            String temp = "";
            task.execute(temp);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View rootView = inflater.inflate(R.layout.playback_layout, container, false);

        Bundle args = getArguments();
        if(args != null) {
            mSelectedTrack = args.getParcelable(
                    SpotifyStreamerConstants.SELECTED_TRACK_DETAILS);

            final List<TopTrackParcelable> topTracksList = args.getParcelableArrayList(
                    SpotifyStreamerConstants.KEY_TOP_TRACK_RESULT);
            mCurrentTrackPosition = args.getInt(
                    SpotifyStreamerConstants.KEY_CURRENT_SELECTED_TRACK_POSITION, 1);

            if(topTracksList != null) {
                mTopTracksMap = Utility.getTopTracksMap(topTracksList);
            }

            //mHandler = new Handler();
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
                            if(mMediaPlayerService != null) {
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
            //pass list
            mMediaPlayerService.setSongUrl(mSelectedTrack.getPreviewUrl());
            mMusicBound = true;
            mMediaPlayerService.playSong();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if(mPlayIntent == null) {
            mPlayIntent = new Intent(getActivity(), MediaPlayerService.class);
            getActivity().bindService(mPlayIntent, mMusicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(mPlayIntent);
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

    @Override
    public void onDestroy() {
        getActivity().stopService(mPlayIntent);
        mMediaPlayerService = null;
        super.onDestroy();
    }

    // Async Task to load the artist result set for the search text.
    class UpdateSeekBarTask extends AsyncTask<String, Void, Void> {
        private String TAG = UpdateSeekBarTask.class.getSimpleName();
        @Override
        protected Void doInBackground(String... params) {
            if(params == null || params.length == 0) {
                //return null;
            }

            if(mMediaPlayerService != null) {
                mMediaPlayerService.handleSeekBar();
            }

            return null;
        }
    }
}
