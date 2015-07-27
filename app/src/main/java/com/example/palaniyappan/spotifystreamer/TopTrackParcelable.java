package com.example.palaniyappan.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Palaniyappan on 7/13/2015.
 */
public class TopTrackParcelable implements Parcelable {
    private String trackName;
    private String albumName;
    private String albumImageUrl;

    public TopTrackParcelable(String trackName, String albumName, String albumImageUrl) {
        this.trackName = trackName;
        this.albumName = albumName;
        this.albumImageUrl = albumImageUrl;
    }

    // Getter methods
    public String getTrackName() {
        return trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumImageUrl() {
        return albumImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{trackName, albumName, albumImageUrl});
    }

    public static final Parcelable.Creator<ArtistParcelable> CREATOR
            = new Parcelable.Creator<ArtistParcelable>() {
        public ArtistParcelable createFromParcel(Parcel in) {
            return new ArtistParcelable(in);
        }

        public ArtistParcelable[] newArray(int size) {
            return new ArtistParcelable[size];
        }
    };

    public TopTrackParcelable(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);
        trackName = data[0];
        albumName = data[1];
        albumImageUrl = data[2];
    }
}
