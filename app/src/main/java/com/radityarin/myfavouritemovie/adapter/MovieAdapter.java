package com.radityarin.myfavouritemovie.adapter;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.radityarin.myfavouritemovie.DetailActivity;
import com.radityarin.myfavouritemovie.R;
import com.radityarin.myfavouritemovie.pojo.Movie;

import java.util.ArrayList;

import static com.radityarin.myfavouritemovie.db.DatabaseContract.FavoriteColumns.CONTENT_URI;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final static String URL_IMAGE = "http://image.tmdb.org/t/p/w185";
    private final ArrayList<Movie> list_movie = new ArrayList<>();

    public void setData(ArrayList<Movie> items) {
        list_movie.clear();
        list_movie.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_movie, parent, false);
        final ViewHolder holder = new ViewHolder(v);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), DetailActivity.class);
                Uri uri = Uri.parse(CONTENT_URI + "/" + getListMovie().get(holder.getAdapterPosition()).getId());
                intent.setData(uri);
                parent.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.bind(list_movie.get(position));
    }

    @Override
    public int getItemCount() {
        return list_movie.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView moviePhoto;
        private final TextView movieTitle;
        private final TextView movieDescription;

        ViewHolder(View itemView) {
            super(itemView);
            moviePhoto = itemView.findViewById(R.id.movie_photo);
            movieTitle = itemView.findViewById(R.id.movie_title);
            movieDescription = itemView.findViewById(R.id.movie_description);
        }

        void bind(Movie movie) {
            movieTitle.setText(movie.getTitle());
            movieDescription.setText(movie.getDescription());
            Glide.with(itemView).load(URL_IMAGE + movie.getPhoto()).into(moviePhoto);
        }
    }

    private ArrayList<Movie> getListMovie() {
        return list_movie;
    }

    public void setListMovie(ArrayList<Movie> list_movie) {
        if (list_movie.size() > 0) {
            this.list_movie.clear();
        }
        this.list_movie.addAll(list_movie);
        notifyDataSetChanged();
    }
}