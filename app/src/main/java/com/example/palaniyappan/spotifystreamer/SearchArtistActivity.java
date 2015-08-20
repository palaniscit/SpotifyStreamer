package com.example.palaniyappan.spotifystreamer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SearchArtistActivity extends ActionBarActivity implements SearchArtistActivityFragment.Callback{

    private boolean mTwoPane;
    private final String TOP_TRACKS_FRAGMENT_TAG = "TTFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_artist);

        // Check if this is a two pane layout for tablets or the single pane layout for phones
        if(findViewById(R.id.top_tracks_container) != null) {
            mTwoPane = true;
            if(savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.top_tracks_container,
                                new ViewTopTracksActivityFragment(),
                                TOP_TRACKS_FRAGMENT_TAG)
                        .commit();
            }
        } else {
             mTwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_artist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(ArtistParcelable selectedArtist) {
        if(mTwoPane) {
            Bundle args = new Bundle();
            args.putString(SpotifyStreamerConstants.KEY_SELECTED_ARTIST_NAME,
                    selectedArtist.getArtistId());
            ViewTopTracksActivityFragment af = new ViewTopTracksActivityFragment();
            af.setArguments(args);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.top_tracks_container,
                            af,
                            TOP_TRACKS_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent topTracksIntent = new Intent(getApplicationContext(),
                    ViewTopTracksActivity.class);
            topTracksIntent.putExtra(SpotifyStreamerConstants.KEY_SELECTED_ARTIST_NAME,
                    selectedArtist.getArtistId());
            startActivity(topTracksIntent);
        }
    }
}
