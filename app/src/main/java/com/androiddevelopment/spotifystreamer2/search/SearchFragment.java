package com.androiddevelopment.spotifystreamer2.search;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.androiddevelopment.spotifystreamer2.R;
import com.androiddevelopment.spotifystreamer2.topten.TopTenActivity;
import com.androiddevelopment.spotifystreamer2.topten.TopTenFragment;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchFragment extends Fragment {

    ArtistAdapter adapter;
    List<Artist> artistList = new ArrayList<>();
    ListView artistListView;
    SearchView searchView;
    SpotifyApi api;
    SpotifyService spotify;

    private static int lastItem = -1;

    final Runnable postExecute = new Runnable() {
        @Override
        public void run() {
            adapter.notifyDataSetChanged();
            if (artistList.size() == 0) {
                Toast.makeText(getActivity(), "No artists found. Search for something else.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        api = new SpotifyApi();
        spotify = api.getService();

        if (savedInstanceState != null) {
            adapter = savedInstanceState.getParcelable("artists");
        }
        else {
            adapter = new ArtistAdapter(getActivity(), artistList);
        }
        artistListView = (ListView) view.findViewById(R.id.artistList);
        artistListView.setAdapter(adapter);
        artistListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = (Artist) artistListView.getItemAtPosition(position);

                Log.d("tag", "Dual? " + SearchActivity.mDualPane);
                if(SearchActivity.mDualPane) {

                    view.setBackgroundColor(Color.parseColor("@color/color_primary_dark"));

                    if(lastItem != -1 && lastItem != position) {
                        parent.getChildAt(lastItem).setBackgroundColor(Color.parseColor("@color/notification_color"));
                    }

                    lastItem = position;

                    Bundle bundle = new Bundle();
                    bundle.putString(SearchActivity.SELECTED_ARTIST_NAME, artist.name);
                    bundle.putString(SearchActivity.SELECTED_ARTIST_ID, artist.id);

                    TopTenFragment frag = new TopTenFragment();
                    frag.setArguments(bundle);

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.track_list_container, frag).addToBackStack("topten").commit();
                }
                else {
                    Intent intent = new Intent(getActivity(), TopTenActivity.class);
                    intent.putExtra(SearchActivity.SELECTED_ARTIST_NAME, artist.name);
                    intent.putExtra(SearchActivity.SELECTED_ARTIST_ID, artist.id);
                    startActivity(intent);
                }
            }
        });
        searchView = (SearchView) view.findViewById(R.id.search_artist);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        artistList.clear();
                        adapter.notifyDataSetChanged();
                        getArtists(query);
                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
        return view;
    }
    public void getArtists(String artist) {
        spotify.searchArtists(artist, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                List<Artist> artists = artistsPager.artists.items;
                artistList.clear();
                artistList.addAll(artists);
                Log.d("success. List: ", artistList.toString());
                getActivity().runOnUiThread(postExecute);
            }
            @Override
            public void failure(RetrofitError error) {
                artistList.clear();

                Log.d("No Artist here","");

                getActivity().runOnUiThread(postExecute);
            }
        });
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelable("artists", adapter);
    }
}
