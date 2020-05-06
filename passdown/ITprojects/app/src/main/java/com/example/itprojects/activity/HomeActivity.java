package com.example.itprojects.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itprojects.R;
import com.example.itprojects.adapter.ScrollAdapter;
import com.example.itprojects.bean.Artifact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-09-07
 */

public class HomeActivity extends AppCompatActivity implements ScrollAdapter.OnItemClickListener{

    // Used for retrieve one recycle view's info that the user clicks to
    public static final String CLICKED_IMAGE = "imageUrl";
    public static final String CLICKED_TITLE = "titleName";
    public static final String CLICKED_DESC = "descContent";
    public static final String CLICKED_KEY = "KeyContent";
    public static final String CLICKED_STATUS = "status";
    public static final String CLICKED_EDITABLE = "editable";

    // Used for retrieve one recycle view's info that the user clicks to
    public static final String CLICKED_UserId = "UserId";
    private static List<Artifact> mArtifacts;

    /**
     * Layout component variable
     */

    private ImageView mNewestIV;
    private TextView mNewestTV;
    private RelativeLayout mShowAllRL;
    private RelativeLayout mEditDescRL;
    private EditText mPassDownDescET;
    private RecyclerView mPhotosRV;
    private ScrollAdapter mAdapter;
    private RelativeLayout mNoArtifact;

    // Trigger whether the user can edit the passdown description or not
    private KeyListener mEditDescKL;

    // In default is in "Display" mode
    private boolean edit_active = false;

    // Firebase variable
    private String uid;
    private DatabaseReference mDatebaseRef;

    private String userID;
    private boolean editable;
    /**
     * Required empty public constructor.
     */

    public HomeActivity() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent thisIntent = getIntent();
        userID = thisIntent.getStringExtra(CLICKED_UserId);




        // Get each component's ID
        mNewestIV = findViewById(R.id.fragment_home_newest_image);
        mNewestTV = findViewById(R.id.fragment_home_newest_title);
        mShowAllRL = findViewById(R.id.fragment_home_view_all);
        mEditDescRL = findViewById(R.id.fragment_home_edit);
        mPassDownDescET = findViewById(R.id.fragment_home_desc);
        mPhotosRV = findViewById(R.id.fragment_home_images);
        mNoArtifact = findViewById(R.id.no_data_description);

        /*
        Show the newest uploaded photo at the top
        Default if the user has no blogs
        */

        mNewestTV.setText(" ");
        Picasso.get().load(R.drawable.bg_exihibition).
                placeholder(R.mipmap.ic_loading).fit().centerInside().
                into(mNewestIV);

        // Access the user's posted blogs
        mArtifacts = new ArrayList<>();
        uid = FirebaseAuth.getInstance().getUid();
        mDatebaseRef = FirebaseDatabase.getInstance().getReference("Artifact/" + userID);
        mDatebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mArtifacts.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (dataSnapshot.exists()) {
                        Artifact upload_Blog = postSnapshot.getValue(Artifact.class);
                        if (!upload_Blog.isStatus()) {
                            mArtifacts.add(upload_Blog);
                        }
                    }
                }

                // Check whether there is not Blogs currently -- Yat Yeung
                if (mArtifacts.size() ==1){
                    mNoArtifact.setVisibility(View.VISIBLE);

                    Collections.reverse(mArtifacts);
                    mNewestTV.setText(mArtifacts.get(0).getTitle());
                    Picasso.get().load(mArtifacts.get(0).getImage()).
                            placeholder(R.mipmap.ic_loading).fit().centerInside().
                            into(mNewestIV);

                    // Set it clickable
                    final Artifact clickedBlog = mArtifacts.get(0);
                    mNewestIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent photoInfoIntent = new Intent(HomeActivity.this, InfoPhotoActivity.class);

                            // Put the artifact's info
                            photoInfoIntent.putExtra(CLICKED_KEY, clickedBlog.getKey());
                            photoInfoIntent.putExtra(CLICKED_IMAGE, clickedBlog.getImage());
                            photoInfoIntent.putExtra(CLICKED_TITLE, clickedBlog.getTitle());
                            photoInfoIntent.putExtra(CLICKED_DESC, clickedBlog.getDesc());
                            photoInfoIntent.putExtra(CLICKED_STATUS, clickedBlog.isStatus());
                            photoInfoIntent.putExtra(CLICKED_EDITABLE, "No");

                            startActivity(photoInfoIntent);
                        }
                    });

                    mArtifacts.remove(0);
                }
                else if(mArtifacts.size()<1){
                    mNoArtifact.setVisibility(View.VISIBLE);

                }
                else {
                    // Show the newest artifact at top
                    Collections.reverse(mArtifacts);
                    mNewestTV.setText(mArtifacts.get(0).getTitle());
                    Picasso.get().load(mArtifacts.get(0).getImage()).
                            placeholder(R.mipmap.ic_loading).fit().centerInside().
                            into(mNewestIV);

                    mNoArtifact.setVisibility(View.GONE);

                    // Set it clickable
                    final Artifact clickedBlog = mArtifacts.get(0);
                    mNewestIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent photoInfoIntent = new Intent(HomeActivity.this, InfoPhotoActivity.class);

                            // Put the artifact's info
                            photoInfoIntent.putExtra(CLICKED_KEY, clickedBlog.getKey());
                            photoInfoIntent.putExtra(CLICKED_IMAGE, clickedBlog.getImage());
                            photoInfoIntent.putExtra(CLICKED_TITLE, clickedBlog.getTitle());
                            photoInfoIntent.putExtra(CLICKED_DESC, clickedBlog.getDesc());
                            photoInfoIntent.putExtra(CLICKED_STATUS, clickedBlog.isStatus());
                            photoInfoIntent.putExtra(CLICKED_EDITABLE, "No");

                            startActivity(photoInfoIntent);
                        }
                    });

                    // Remove the top one as it already shown
                    mArtifacts.remove(0);
                }

                // Show the horizontal recycle view of partial photos at the middle
                mPhotosRV.setHasFixedSize(false);
                LinearLayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this,
                        LinearLayoutManager.HORIZONTAL, false);
                mPhotosRV.setLayoutManager(layoutManager);
                mAdapter = new ScrollAdapter(HomeActivity.this, mArtifacts);
                mPhotosRV.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(HomeActivity.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Show all photos in one page
        mShowAllRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ArtifactsIntent = new Intent(HomeActivity.this, ArtifactslistActivity.class);
                ArtifactsIntent.putExtra(CLICKED_UserId, userID);
                startActivity(ArtifactsIntent);
            }
        });

        // Retrieve the text from database first
        mPassDownDescET.setText("暂无简介");
        FirebaseDatabase.
                getInstance().
                getReference().
                child("Users").
                child(userID).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (data.getKey().equals("passdown_Desc")) {
                                mPassDownDescET.setText(data.getValue().toString());
                            }
                        }

                        // Show whose PassDown is it
                        getSupportActionBar().setTitle(
                                dataSnapshot.child("surName").getValue().toString() +
                                        dataSnapshot.child("firstName").getValue().toString() +
                                        "的传家库");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        // Edit the passdown description or just display the content
        mEditDescKL = mPassDownDescET.getKeyListener();

        // First it is on "Display" mode
        mPassDownDescET.setKeyListener(null);

        // Get each component's ID
        final TextView mEditDescTV = findViewById(R.id.fragment_home_desc_edit_text);
        final ImageView mEditDescIV = findViewById(R.id.fragment_home_edit_pic);

        mEditDescTV.setVisibility(View.GONE);
        mEditDescIV.setVisibility(View.GONE);



    }




    /**
     * Update the description to the database.
     */

    private void startPosting() {
        String desc = mPassDownDescET.getText().toString().trim();

        // Update the database
        FirebaseDatabase.
                getInstance().
                getReference().
                child("Users").
                child(FirebaseAuth.getInstance().getUid()).
                child("passdown_Desc").
                setValue(desc);

        Toasty.success(HomeActivity.this, "已完成修改", Toast.LENGTH_SHORT).show();
    }

    /**
     * Listener for each artifact clicked by the user.
     *
     * @param position           The position that the user click on.
     *
     */

    @Override
    public void onItemClick(int position) {
        Intent photoInfoIntent = new Intent(HomeActivity.this, InfoPhotoActivity.class);
        Artifact clickedBlog = mArtifacts.get(position);

        // Put the artifact's info
        photoInfoIntent.putExtra(CLICKED_KEY, clickedBlog.getKey());
        photoInfoIntent.putExtra(CLICKED_IMAGE, clickedBlog.getImage());
        photoInfoIntent.putExtra(CLICKED_TITLE, clickedBlog.getTitle());
        photoInfoIntent.putExtra(CLICKED_DESC, clickedBlog.getDesc());
        photoInfoIntent.putExtra(CLICKED_STATUS, clickedBlog.isStatus());
        photoInfoIntent.putExtra(CLICKED_EDITABLE, "No");


        startActivity(photoInfoIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
