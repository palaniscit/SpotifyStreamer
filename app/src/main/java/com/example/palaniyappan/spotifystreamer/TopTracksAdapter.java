package com.example.palaniyappan.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Palaniyappan on 7/10/2015.
 */
public class TopTracksAdapter extends ArrayAdapter<TopTrackParcelable>{
    private List<TopTrackParcelable> topTrackList;
    public TopTracksAdapter(Context context, int resourceId,List<TopTrackParcelable> topTrackList) {
        super(context, resourceId, topTrackList);
        this.topTrackList = topTrackList;
    }

    public List<TopTrackParcelable> getItems() {
        return this.topTrackList;
    }

    @Override
    public android.view.View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.top_tracks_layout, null);
        }

        TopTrackParcelable p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.track_description);
            ImageView tt3 = (ImageView) v.findViewById(R.id.top_track_thumbnail);

            if (tt1 != null) {
                String albumName = SpotifyStreamerConstants.TEXT_UNKNOWN;
                String trackName = SpotifyStreamerConstants.TEXT_UNKNOWN;
                if(p.getAlbumName() != null && p.getAlbumName() != null) {
                    albumName = p.getAlbumName();
                }

                if(p.getTrackName() != null) {
                    trackName = p.getTrackName();
                }

                tt1.setText(trackName + SpotifyStreamerConstants.LINE_BREAK + albumName);
            }

            if(tt3 != null) {
                if(p.getAlbumImageUrl() != null && !p.getAlbumImageUrl().isEmpty()) {
                    Picasso.with(getContext())
                            .load(p.getAlbumImageUrl())
                            .into(tt3);
                }
            }
        }

        return v;
    }
}
