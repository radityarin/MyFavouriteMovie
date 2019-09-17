package com.radityarin.myfavouritemovie;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.radityarin.myfavouritemovie.pojo.Movie;

import static com.radityarin.myfavouritemovie.db.DatabaseContract.FavoriteColumns.CATEGORY;
import static com.radityarin.myfavouritemovie.db.DatabaseContract.FavoriteColumns.CONTENT_URI;
import static com.radityarin.myfavouritemovie.db.DatabaseContract.FavoriteColumns.DESCRIPTION;
import static com.radityarin.myfavouritemovie.db.DatabaseContract.FavoriteColumns.PHOTO;
import static com.radityarin.myfavouritemovie.db.DatabaseContract.FavoriteColumns.RATING;
import static com.radityarin.myfavouritemovie.db.DatabaseContract.FavoriteColumns.TITLE;
import static com.radityarin.myfavouritemovie.db.DatabaseContract.FavoriteColumns.YEAR;

public class DetailActivity extends AppCompatActivity {

    private Movie movie = null;
    private ImageView movie_photo;
    private TextView movie_title, movie_year, movie_description, movie_rating;
    private ConstraintLayout constraintLayout;
    private Menu menu;
    private final static String URL_IMAGE = "http://image.tmdb.org/t/p/w185";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        constraintLayout = findViewById(R.id.constraintlayout);

        Uri uri = getIntent().getData();
        if (uri != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) movie = new Movie(cursor);
                cursor.close();
            }
        }
        prepare();
        show();
    }

    private void prepare() {
        movie_photo = findViewById(R.id.movie_photo);
        movie_title = findViewById(R.id.movie_title);
        movie_year = findViewById(R.id.movie_year);
        movie_description = findViewById(R.id.movie_description);
        movie_rating = findViewById(R.id.movie_rating);
    }

    private void show() {
        Glide.with(this).load(URL_IMAGE + movie.getPhoto()).into(movie_photo);
        movie_title.setText(movie.getTitle());
        movie_year.setText(movie.getYear());
        movie_description.setText(movie.getDescription());
        movie_rating.setText(movie.getRating());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_menu, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favorite) {
            showAlertDialog();
        }
        return super.onOptionsItemSelected(item);
    }


    private void showAlertDialog() {
        String dialogTitle, dialogMessage;

        dialogMessage = String.format(getResources().getString(R.string.delete_question), movie.getTitle());
        dialogTitle = getResources().getString(R.string.delete);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(true)
                .setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            ContentValues values = new ContentValues();
                            values.put(TITLE, movie.getTitle());
                            values.put(YEAR, movie.getYear());
                            values.put(DESCRIPTION, movie.getDescription());
                            values.put(RATING, movie.getRating());
                            values.put(PHOTO, movie.getPhoto());
                            values.put(CATEGORY, movie.getType());
                            Uri uri = Uri.parse(CONTENT_URI + "/" + movie.getId());
                            getContentResolver().delete(uri, null, null);
                            showSnackbarMessage(String.format(getResources().getString(R.string.delete_successful), movie.getTitle()));
                            MenuItem settingsItem = menu.findItem(R.id.action_favorite);
                            settingsItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_off));

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showSnackbarMessage(String message) {
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
