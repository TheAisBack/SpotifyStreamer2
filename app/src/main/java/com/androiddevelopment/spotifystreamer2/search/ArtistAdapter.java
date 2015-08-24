package com.androiddevelopment.spotifystreamer2.search;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androiddevelopment.spotifystreamer2.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

public class ArtistAdapter extends BaseAdapter implements Parcelable {
    Context context;
    List<Artist> artists;
    Picasso mPicasso;
    LayoutInflater mInflater;

    public ArtistAdapter(Parcel in) {
        in.readArray(ArtistAdapter.class.getClassLoader());
    }

    ArtistAdapter(Context context, List<Artist> artists) {
        this.context = context;
        this.artists = artists;
        this.mPicasso = Picasso.with(context);
        this.mInflater = LayoutInflater.from(context);
    }

    public static final Parcelable.Creator<ArtistAdapter> CREATOR
            = new Parcelable.Creator<ArtistAdapter>() {
        public ArtistAdapter createFromParcel(Parcel in) {
            return new ArtistAdapter(in);
        }

        public ArtistAdapter[] newArray(int size) {
            return new ArtistAdapter[size];
        }
    };
    @Override
    public Object getItem(int position) {
        return artists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

    @Override
    public int getCount() {
        return artists.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if(view == null) {
            view = mInflater.inflate(R.layout.artist, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.artistImage);
            holder.textView = (TextView) view.findViewById(R.id.artistText);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }
        Artist artist = artists.get(position);
        String image_url = "";

        if(artist.images.size() > 0) {
           image_url = artist.images.get(0).url;
        }

        if(!image_url.isEmpty()) {
            mPicasso.load(image_url)
                    .fit()
                    .into(holder.imageView);
        }
        holder.textView.setText(artist.name);
        return view;
    }
    private class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}