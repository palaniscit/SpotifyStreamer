package com.example.palaniyappan.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class PlaybackActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        if(savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            PlaybackFragment newFragment = new PlaybackFragment();

            // Get playback artist details
            Intent intent = getIntent();
            if(intent != null) {
                TopTrackParcelable selectedTrack = intent.getExtras().
                        getParcelable(SpotifyStreamerConstants.SELECTED_TRACK_DETAILS);
                List<TopTrackParcelable> topTracksList = intent.getParcelableArrayListExtra(
                        SpotifyStreamerConstants.KEY_TOP_TRACK_RESULT);
                int currentTrackPosition = intent.getIntExtra(
                        SpotifyStreamerConstants.KEY_CURRENT_SELECTED_TRACK_POSITION, 1);
                // Set the value into the arguments passed to the fragment
                Bundle args = new Bundle();
                args.putParcelable(
                        SpotifyStreamerConstants.SELECTED_TRACK_DETAILS,
                        selectedTrack);
                args.putParcelableArrayList(
                        SpotifyStreamerConstants.KEY_TOP_TRACK_RESULT,
                        (ArrayList) topTracksList);
                args.putInt(
                        SpotifyStreamerConstants.KEY_CURRENT_SELECTED_TRACK_POSITION,
                        currentTrackPosition);
                newFragment.setArguments(args);
            }

            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.add(R.id.playback_container, newFragment)
                    .addToBackStack(null).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playback, menu);
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
