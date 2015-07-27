package com.example.palaniyappan.spotifystreamer;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Palaniyappan on 7/12/2015.
 */
public class SpotifyUtil {
    public void displayToast(Context context, int duration, CharSequence text) {
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
