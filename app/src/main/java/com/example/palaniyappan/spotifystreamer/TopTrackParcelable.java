package com.example.palaniyappan.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Palaniyappan on 7/13/2015.
 */
public class TopTrackParcelable implements Parcelable {
    private String trackName;
    private String albumName;
    private String albumImageUrl;
    private String artistName;
    private String previewUrl;

    public TopTrackParcelable(Track track) {
        String url = null;
        String albumName = null;
        String artistName = null;
        if(track.album != null && track.album.images != null
                && !track.album.images.isEmpty()) {
            url = track.album.images.get(0).url;
        }
        if(track.album != null) {
            albumName = track.album.name;
        }
        if(track.artists != null && !track.artists.isEmpty()) {
            ArtistSimple artist = track.artists.get(0);
            artistName = artist.name;
        }

        this.artistName = artistName;
        this.trackName = track.name;
        this.albumName = albumName;
        this.albumImageUrl = url;
        this.previewUrl = track.preview_url;
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

    public String getArtistName() {
        return artistName;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                trackName,
                albumName,
                albumImageUrl,
                artistName,
                previewUrl
        });
    }

    public static final Parcelable.Creator<TopTrackParcelable> CREATOR
            = new Parcelable.Creator<TopTrackParcelable>() {
        public TopTrackParcelable createFromParcel(Parcel in) {
            return new TopTrackParcelable(in);
        }

        public TopTrackParcelable[] newArray(int size) {
            return new TopTrackParcelable[size];
        }
    };

    public TopTrackParcelable(Parcel in) {
        String[] data = new String[5];
        in.readStringArray(data);
        trackName = data[0];
        albumName = data[1];
        albumImageUrl = data[2];
        artistName = data[3];
        previewUrl = data[4];
    }
}
