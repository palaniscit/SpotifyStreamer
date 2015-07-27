package com.example.palaniyappan.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchArtistActivityFragment extends Fragment {

    ArtistAdapter artistListAdapter;
    public SearchArtistActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_artist, container, false);

        // Array adapter for the artist list view
        artistListAdapter = new ArtistAdapter(getActivity(), R.layout.artist_list_item_layout, new ArrayList<ArtistParcelable>());
        artistListAdapter.notifyDataSetChanged();

        ListView artistListView = (ListView)view.findViewById(R.id.artist_list);
        artistListView.setAdapter(artistListAdapter);

        artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the artist details of the item selected by the user.
                ArtistParcelable clickedArtist = artistListAdapter.getItem(position);
                // Create an intent to open the activity to view top tracks of the selected artist
                Intent viewTopTrackIntent = new Intent(getActivity(), ViewTopTracksActivity.class);
                // Pass on the artist id to the Top tracks activity
                viewTopTrackIntent.putExtra(SpotifyStreamerConstants.KEY_SELECTED_ARTIST_NAME, clickedArtist.getArtistId());
                startActivity(viewTopTrackIntent);
            }
        });

        // Add text watcher for the search artist edit text to load the search result for the text
        // entered by the user
        final EditText searchArtistEditText = (EditText)view.findViewById(R.id.search_artist_text);
       TextWatcher searchArtistTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s == null || s.length() == 0) {
                    artistListAdapter.clear();
                }
                if(s != null && s.length() > 0) {
                    if(savedInstanceState != null && savedInstanceState.containsKey
                            (SpotifyStreamerConstants.KEY_ARTIST_SEARCH_RESULT)) {
                        savedInstanceState.remove(SpotifyStreamerConstants.KEY_ARTIST_SEARCH_RESULT);
                    } else {
                        getSearchResult(s.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        searchArtistEditText.addTextChangedListener(searchArtistTextWatcher);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // To handle screen rotation and while coming back from child activities.
        if(savedInstanceState != null && savedInstanceState.containsKey
                (SpotifyStreamerConstants.KEY_ARTIST_SEARCH_RESULT)){
            List<ArtistParcelable> artistsList = savedInstanceState.getParcelableArrayList
                    (SpotifyStreamerConstants.KEY_ARTIST_SEARCH_RESULT);
            artistListAdapter.clear();
            if(artistsList != null && !artistsList.isEmpty()) {
                for(ArtistParcelable artist: artistsList) {
                    artistListAdapter.add(artist);
                }
            }
        }
    }

    // Method to call the async task to fetch the search result for the search text.
    public void getSearchResult(String artistName) {
        // Call the async task that will get the search result and load it to the UI
        FetchArtistTask task = new FetchArtistTask();
        task.execute(artistName);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save state information with a collection of key-value pairs
        savedInstanceState.putParcelableArrayList(SpotifyStreamerConstants.KEY_ARTIST_SEARCH_RESULT, (ArrayList)artistListAdapter.getItems());
        super.onSaveInstanceState(savedInstanceState);
    }

    // Async Task to load the artist result set for the search text.
    class FetchArtistTask extends AsyncTask<String, Void, ArtistsPager> {
        private String TAG = FetchArtistTask.class.getSimpleName();
        @Override
        protected ArtistsPager doInBackground(String... params) {
            if(params == null || params.length == 0) {
                return null;
            }

            ArtistsPager artistsPager = new ArtistsPager();

            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService service = api.getService();

                artistsPager = service.searchArtists(params[0]);
            } catch(RetrofitError e) {
                Log.e(TAG, "Internet connection unavailable.");
            }

            return artistsPager;
        }

        @Override
        protected void onPostExecute(ArtistsPager artistsPager) {
            Pager<Artist> artists = artistsPager.artists;
            if(artists != null) {
                List<Artist> artistList = artists.items;
                artistListAdapter.clear();
                ArtistParcelable artistParcelable = null;
                String url = null;
                if(artistList != null && !artistList.isEmpty()) {
                    for(Artist artist: artistList) {
                        if(artist.images != null && !artist.images.isEmpty()) {
                            url = artist.images.get(0).url;
                        }
                        artistParcelable = new ArtistParcelable(artist.id, artist.name,url);
                        artistListAdapter.add(artistParcelable);
                    }
                } else {
                    SpotifyUtil util = new SpotifyUtil();
                    util.displayToast(getActivity(), Toast.LENGTH_SHORT, getString(R.string.artist_not_found));
                }
            }
        }
    }
}
