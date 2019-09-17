package com.radityarin.myfavouritemovie;

import android.database.Cursor;

interface LoadMoviesCallback {
    void postExecute(Cursor movies);

}
