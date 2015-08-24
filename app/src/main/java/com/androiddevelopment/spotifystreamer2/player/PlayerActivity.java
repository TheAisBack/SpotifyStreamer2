package com.androiddevelopment.spotifystreamer2.player;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androiddevelopment.spotifystreamer2.R;
import com.androiddevelopment.spotifystreamer2.search.SearchActivity;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity);

        if( savedInstanceState == null ) {
            Intent intent = getIntent();

            Bundle bundle = new Bundle();
            bundle.putInt(SearchActivity.TRACK_INDEX, intent.getIntExtra(SearchActivity.TRACK_INDEX, 0));
            bundle.putString(SearchActivity.SELECTED_ARTIST_NAME, intent.getStringExtra(SearchActivity.SELECTED_ARTIST_NAME));

            PlayerFragment frag = new PlayerFragment();
            frag.setArguments(bundle);

            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.player_container, frag).addToBackStack("player").commit();
        }
    }

}
