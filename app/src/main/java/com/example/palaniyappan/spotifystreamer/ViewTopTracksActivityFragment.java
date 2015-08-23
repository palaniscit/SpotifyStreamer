package com.example.palaniyappan.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
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
 * A placeholder fragment containing a simple view.
 */
public class ViewTopTracksActivityFragment extends Fragment {

    private TopTracksAdapter topTracksAdapter;
    public ViewTopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_top_tracks, container, false);

        // Array adapter for top tracks list for the selected artist.
        topTracksAdapter = new TopTracksAdapter(getActivity(),
                R.layout.top_tracks_layout, new ArrayList<TopTrackParcelable>());

        // Set the array adapter to the list view
        ListView topTracksListView = (ListView)rootView.findViewById(R.id.top_traacks_list);
        topTracksListView.setAdapter(topTracksAdapter);

        topTracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TopTrackParcelable selectedTrack = topTracksAdapter.getItem(i);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                PlaybackFragment newFragment = new PlaybackFragment();

                boolean showDialog = getResources().getBoolean(R.bool.show_dialog);

                if(showDialog) {
                    Bundle args = new Bundle();
                    args.putParcelable(
                            SpotifyStreamerConstants.SELECTED_TRACK_DETAILS,
                            selectedTrack);
                    args.putParcelableArrayList(
                            SpotifyStreamerConstants.KEY_TOP_TRACK_RESULT,
                            (ArrayList) topTracksAdapter.getItems());
                    args.putInt(
                            SpotifyStreamerConstants.KEY_CURRENT_SELECTED_TRACK_POSITION,
                            i+1);
                    newFragment.setArguments(args);
                    newFragment.show(fragmentManager, "dialog");
                } else {
                    Intent playbackIntent = new Intent(getActivity(), PlaybackActivity.class);
                    playbackIntent.putExtra(SpotifyStreamerConstants.SELECTED_TRACK_DETAILS,
                            selectedTrack);
                    playbackIntent.putParcelableArrayListExtra(
                            SpotifyStreamerConstants.KEY_TOP_TRACK_RESULT,
                            (ArrayList) topTracksAdapter.getItems());
                    playbackIntent.putExtra(
                            SpotifyStreamerConstants.KEY_CURRENT_SELECTED_TRACK_POSITION,
                            i+1);
                    startActivity(playbackIntent);
                }
            }
        });

        // Call the async task to load the top tracks for the artist.
        Bundle args = getArguments();
        if(savedInstanceState != null && savedInstanceState.
                containsKey(SpotifyStreamerConstants.KEY_TOP_TRACK_RESULT)) {
            List<TopTrackParcelable> topTrackList = savedInstanceState.
                    getParcelableArrayList(SpotifyStreamerConstants.KEY_TOP_TRACK_RESULT);
            topTracksAdapter.addAll(topTrackList);
        } else if(args != null) {
            getTopTracks(args.getString(SpotifyStreamerConstants.KEY_SELECTED_ARTIST_NAME));
        }

        return rootView;
    }

    public void getTopTracks(String artistName) {
        FetchTopTracksTask task = new FetchTopTracksTask();
        task.execute(artistName);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save state information with a collection of key-value pairs
        savedInstanceState.putParcelableArrayList(SpotifyStreamerConstants.KEY_TOP_TRACK_RESULT,
                (ArrayList)topTracksAdapter.getItems());
        super.onSaveInstanceState(savedInstanceState);
    }

    // Async Task to load the artist result set for the search text.
    class FetchTopTracksTask extends AsyncTask<String, Void, Tracks> {
        private String TAG = FetchTopTracksTask.class.getSimpleName();
        @Override
        protected Tracks doInBackground(String... params) {
            if(params == null || params.length == 0) {
                return null;
            }

            Tracks topTracks = new Tracks();
            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService service = api.getService();
                Map<String, Object> options = new HashMap<>();
                options.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());
                topTracks = service.getArtistTopTrack(params[0], options);
            } catch (RetrofitError e) {
                Log.e(TAG, "Internet connection unavailable.");
            }

            return topTracks;
        }

        @Override
        protected void onPostExecute(Tracks topTracks) {
            List<Track> trackList = topTracks.tracks;
            topTracksAdapter.clear();

            if(trackList != null && !trackList.isEmpty()) {
                for(Track track: trackList) {
                    topTracksAdapter.add(new TopTrackParcelable(track));
                }
            } else {
                SpotifyUtil util = new SpotifyUtil();
                util.displayToast(getActivity(), Toast.LENGTH_SHORT, getString(R.string.no_tracks_found));
            }
        }
    }
}
