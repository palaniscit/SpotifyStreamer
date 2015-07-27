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
public class ArtistAdapter extends ArrayAdapter<ArtistParcelable> {
    private List<ArtistParcelable> artistsList;
    public ArtistAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ArtistAdapter(Context context, int resource, List<ArtistParcelable> artists) {
        super(context, resource, artists);
        this.artistsList = artists;
    }

    public List<ArtistParcelable> getItems() {
        return this.artistsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.artist_list_item_layout, null);
        }

        ArtistParcelable p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.artist_name);
            //TextView tt2 = (TextView) v.findViewById(R.id.categoryId);
            //TextView tt3 = (TextView) v.findViewById(R.id.description);
            ImageView tt2 = (ImageView) v.findViewById(R.id.artist_thumbnail);

            if (tt1 != null) {
                tt1.setText(p.getArtistName());
            }

            if(tt2 != null) {
                if(p.getArtistImageUrl() != null && !p.getArtistImageUrl().isEmpty()) {
                    Picasso.with(getContext())
                            .load(p.getArtistImageUrl())
                            .into(tt2);
                }
            }
        }

        return v;
    }
}
