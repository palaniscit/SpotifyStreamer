package com.example.palaniyappan.spotifystreamer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class ViewTopTracksActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_top_tracks);
        if(savedInstanceState == null) {
            Bundle args = new Bundle();
            Intent intent = getIntent();
            if(intent != null && intent.
                    hasExtra(SpotifyStreamerConstants.KEY_SELECTED_ARTIST_NAME)) {
                args.putString(
                        SpotifyStreamerConstants.KEY_SELECTED_ARTIST_NAME,
                        intent.getStringExtra(SpotifyStreamerConstants.KEY_SELECTED_ARTIST_NAME)
                );
            }
            ViewTopTracksActivityFragment af = new ViewTopTracksActivityFragment();
            af.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.top_tracks_container, af)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_top_tracks, menu);
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
}
