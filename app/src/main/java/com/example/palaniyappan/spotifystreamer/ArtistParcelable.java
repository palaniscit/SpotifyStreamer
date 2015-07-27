package com.example.palaniyappan.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Palaniyappan on 7/12/2015.
 */
public class ArtistParcelable implements Parcelable {
    private String artistName;

    private String artistId;

    private String artistImageUrl;

    public ArtistParcelable(String id, String name, String url) {
        this.artistId = id;
        this.artistName = name;
        this.artistImageUrl = url;
    }

    // Getter methods
    public String getArtistName() {
        return artistName;
    }

    public String getArtistId() {
        return artistId;
    }

    public String getArtistImageUrl() {
        return artistImageUrl;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeStringArray(new String[]{artistName, artistId, artistImageUrl});
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

    public ArtistParcelable(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);
        artistId = data[0];
        artistName = data[1];
        artistImageUrl = data[2];
    }
}
