package com.example.itprojects.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itprojects.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import static com.example.itprojects.activity.InfoPhotoActivity.DISPLAYED_IMAGE;

/**
 * This activity contains a single ImageView used to display the image that the user has clicked
 * from "InfoPhotoActivity".
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-09-02
 */

public class FullImageActivity extends AppCompatActivity {

    // Back to photo page btn
    ImageView mCloseIV;

    /**
     * Show the images and display the view.
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        /**
         * Layout component variable
         * assign it's ID
         */

        // Hide action bar
        getSupportActionBar().hide();

        // ImageView in this xml
        PhotoView mFullImageIV = findViewById(R.id.activity_full_image_image);

        // Close btn in this xml
        mCloseIV = findViewById(R.id.activity_full_image_close);

        // The image url needed to pass into the ImageView
        final Intent thisIntent = getIntent();
        String imageUrl = thisIntent.getStringExtra(DISPLAYED_IMAGE);

        // Load the image
        Picasso.get().load(imageUrl).placeholder(R.mipmap.ic_loading).into(mFullImageIV);

        // Return back
        mCloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullImageActivity.this.finish();
            }
        });
    }
}
