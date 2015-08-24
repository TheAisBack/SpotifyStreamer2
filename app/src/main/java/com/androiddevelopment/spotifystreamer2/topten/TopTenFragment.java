package com.androiddevelopment.spotifystreamer2.topten;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androiddevelopment.spotifystreamer2.player.PlayerActivity;
import com.androiddevelopment.spotifystreamer2.player.PlayerFragment;
import com.androiddevelopment.spotifystreamer2.R;
import com.androiddevelopment.spotifystreamer2.search.SearchActivity;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TopTenFragment extends Fragment {

    TrackAdapter adapter;
    ListView trackListView;

    String artistName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_ten_fragment, container, false);

        if (savedInstanceState != null) {
            adapter = savedInstanceState.getParcelable("tracks");
        }
        else {
            SearchActivity.trackList.clear();
            adapter = new TrackAdapter(getActivity(), SearchActivity.trackList);
        }
        Bundle args = getArguments();
        artistName = args.getString(SearchActivity.SELECTED_ARTIST_NAME);
        String artistId = args.getString(SearchActivity.SELECTED_ARTIST_ID);
        trackListView = (ListView) view.findViewById(R.id.track_list);
        trackListView.setAdapter(adapter);
        trackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(SearchActivity.mDualPane) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(SearchActivity.TRACK_INDEX, i);
                    bundle.putString(SearchActivity.SELECTED_ARTIST_NAME, artistName);
                    PlayerFragment player = new PlayerFragment();
                    player.setArguments(bundle);
                    FragmentManager fm = getFragmentManager();
                    player.show(fm, "player");
                }
                else {
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra(SearchActivity.TRACK_INDEX, i);
                    intent.putExtra(SearchActivity.SELECTED_ARTIST_NAME, artistName);
                    startActivity(intent);
                }
            }
        });
        if(savedInstanceState == null) getTracks(artistId);
        return view;
    }

    public void getTracks(String artist) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("country","ca");

        spotify.getArtistTopTrack(artist, parameters, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                SearchActivity.trackList.clear();
                SearchActivity.trackList.addAll(tracks.tracks);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            @Override
            public void failure(RetrofitError error) {
                Log.d("Fail ", error.getMessage());
            }
        });
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("tracks", adapter);
    }
}