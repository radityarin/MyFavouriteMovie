package com.radityarin.myfavouritemovie;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.radityarin.myfavouritemovie.adapter.MovieAdapter;
import com.radityarin.myfavouritemovie.pojo.Movie;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

import static com.radityarin.myfavouritemovie.db.DatabaseContract.FavoriteColumns.CONTENT_URI;
import static com.radityarin.myfavouritemovie.helper.MappingHelper.mapCursorToArrayList;


public class MoviesFragment extends Fragment implements LoadMoviesCallback{

    private MovieAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvInfo;

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_movies, container, false);

        tvInfo = view.findViewById(R.id.tv_info);
        progressBar = view.findViewById(R.id.progressBar);

        showLoading(true);
        adapter = new MovieAdapter();
        adapter.notifyDataSetChanged();
        RecyclerView recyclerView = view.findViewById(R.id.rv_movies);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        DataObserver myObserver = new DataObserver(handler, Objects.requireNonNull(getActivity()).getApplicationContext());
        getActivity().getApplicationContext().getContentResolver().registerContentObserver(CONTENT_URI, true, myObserver);
        new getData(getActivity().getApplicationContext(), this).execute();
        return view;
    }

    private void showLoading(Boolean state) {
        if (state) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void postExecute(Cursor movies) {
        String type = "movie";
        ArrayList<Movie> listMovies = mapCursorToArrayList(movies, type);
        if (listMovies.size() > 0) {
            adapter.setListMovie(listMovies);
        } else {
            adapter.setListMovie(new ArrayList<Movie>());
            tvInfo.setVisibility(View.VISIBLE);
        }
        showLoading(false);
    }

    private static class getData extends AsyncTask<Void, Void, Cursor> {
        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadMoviesCallback> weakCallback;
        private getData(Context context, LoadMoviesCallback callback) {
            weakContext = new WeakReference<>(context);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            return weakContext.get().getContentResolver().query(CONTENT_URI, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor data) {
            super.onPostExecute(data);
            weakCallback.get().postExecute(data);
        }
    }
    static class DataObserver extends ContentObserver {
        final Context context;
        DataObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new getData(context, (LoadMoviesCallback) context).execute();
        }
    }
}
