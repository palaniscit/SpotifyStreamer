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

}
