package com.example.palaniyappan.spotifystreamer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pal on 8/22/15.
 */
public class Utility {

    public static Map<Integer, TopTrackParcelable> getTopTracksMap(List<TopTrackParcelable> topTrackList) {
        Map<Integer, TopTrackParcelable> topTracksMap = new HashMap<>();
        Integer position = 1;
        if(topTrackList != null) {
            for(TopTrackParcelable topTrack: topTrackList) {
                topTracksMap.put(position, topTrack);
                position++;
            }
        }
        return topTracksMap;
    }

    public static String convertMilliSecToDesiredFormat(int milliseconds) {
        String desiredFormat = "";

        int inputInSeconds = milliseconds/1000;
        int mins = (int) Math.floor(inputInSeconds/60);
        int seconds = inputInSeconds%60;

        desiredFormat = String.valueOf(mins) + ":" + String.valueOf(seconds);
        return desiredFormat;
    }

    public static void main(String [] args) {
        convertMilliSecToDesiredFormat(28606);
    }
}
