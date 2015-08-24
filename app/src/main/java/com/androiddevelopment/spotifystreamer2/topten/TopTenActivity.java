package com.androiddevelopment.spotifystreamer2.topten;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androiddevelopment.spotifystreamer2.R;
import com.androiddevelopment.spotifystreamer2.search.SearchActivity;

public class TopTenActivity extends AppCompatActivity {

    String artistName, artistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_ten_activity);
        Intent intent = getIntent();
        artistName = intent.getStringExtra(SearchActivity.SELECTED_ARTIST_NAME);
        artistId = intent.getStringExtra(SearchActivity.SELECTED_ARTIST_ID);

        actionBarSetup(artistName);

        Bundle bundle = new Bundle();
        bundle.putString(SearchActivity.SELECTED_ARTIST_NAME, artistName);
        bundle.putString(SearchActivity.SELECTED_ARTIST_ID, artistId);

        TopTenFragment frag = new TopTenFragment();
        frag.setArguments(bundle);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.track_list_container, frag).addToBackStack("topten").commit();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void actionBarSetup(String sub) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSupportActionBar().setSubtitle(sub);
        }
    }
}
