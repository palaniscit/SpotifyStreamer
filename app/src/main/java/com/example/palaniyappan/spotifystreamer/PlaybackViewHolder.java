package com.example.palaniyappan.spotifystreamer;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Pal on 8/22/15.
 */
public class PlaybackViewHolder {
    // Using the view holder can reduce the number of times the findViewById method is called
    public final TextView artistNameView;
    public final TextView albumNameView;
    public final ImageView trackThumbnailView;
    public final TextView trackNameView;
    public final ImageView previousTrackImageView;
    public final ImageView nextTrackImageView;
    public final ImageView playTrackImageView;
    public final SeekBar playProgressBar;

    public PlaybackViewHolder(View view) {
        artistNameView = (TextView)view.findViewById(R.id.artist_name);
        albumNameView = (TextView)view.findViewById(R.id.album_name);
        trackThumbnailView = (ImageView)view.findViewById(R.id.track_thumbnail);
        trackNameView = (TextView)view.findViewById(R.id.track_name);
        previousTrackImageView = (ImageView)view.findViewById(R.id.playback_previous);
        nextTrackImageView = (ImageView)view.findViewById(R.id.playback_next);
        playTrackImageView = (ImageView)view.findViewById(R.id.playback_play);
        playProgressBar = (SeekBar)view.findViewById(R.id.playback_seekbar);
    }
}
