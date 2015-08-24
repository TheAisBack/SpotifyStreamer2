package com.androiddevelopment.spotifystreamer2.search;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.androiddevelopment.spotifystreamer2.R;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;

public class SearchActivity extends AppCompatActivity  {

    public static ArrayList<Track> trackList = new ArrayList<>();
    public static final String SELECTED_ARTIST_ID = ".SELECTED_ARTIST_ID";
    public static final String SELECTED_ARTIST_NAME = ".SELECTED_ARTIST_NAME";
    public static final String TRACK_INDEX = ".TRACK_INDEX";
    public static boolean mDualPane = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_artists, menu);
        return true;
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_artists);
        if(findViewById(R.id.track_list_container) != null) {
            mDualPane = true;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}