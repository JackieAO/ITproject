package com.example.itprojects.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itprojects.adapter.ArtifactAdapter;
import com.example.itprojects.bean.Artifact;


import com.example.itprojects.R;
import com.example.itprojects.interfaces.MyButtonClickListener;
import com.example.itprojects.util.MySwipeHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.itprojects.fragment.FamilyFragment.CLICKED_UserId;

/**
 * This activity only contains a recycle view which is used to display all the artifacts of a
 * particular user.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-08-30
 */

public class ArtifactslistActivity extends AppCompatActivity implements
        ArtifactAdapter.OnItemClickListener{

    // Used for retrieve one recycle view's info that the user clicks to
    public static final String CLICKED_IMAGE = "imageUrl";
    public static final String CLICKED_TITLE = "titleName";
    public static final String CLICKED_DESC = "descContent";
    public static final String CLICKED_KEY = "KeyContent";

    public static final String CLICKED_STATUS = "status";
    private static final String CLICKED_UserId = "UserId";
    public String uid;

    /**
     * Layout component variable
     */

    // Store all the artifacts'info
    private RecyclerView mAllArtisRV;
    private RelativeLayout mNoArtifactRL;

    // It's adapter
    private ArtifactAdapter mAllArtisRVAA;

    // List of all artifacts
    private List<Artifact> mArtis;

    /**
     * Show the images and display the view.
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artifactslist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("全部传家");

        // Get RV's ID
        mAllArtisRV = findViewById(R.id.artifacts_view);

        // Assign properties
        mAllArtisRV.setHasFixedSize(false);
        mAllArtisRV.setLayoutManager(new LinearLayoutManager(ArtifactslistActivity.this));

        // For empty artifact shown layout - Yat Yeung
        mNoArtifactRL = findViewById(R.id.artifacts_view_no_artifact);

        // Initialize the list
        mArtis = new ArrayList<>();

        /*
         Read the artifacts from the particular user's database
         Retrieve the user's Firebase ID
        */
        final Intent thisIntent = getIntent();
        uid = thisIntent.getStringExtra(CLICKED_UserId);
        if (!uid.equals(FirebaseAuth.getInstance().getUid())) {
            // Firebase variable. Database reference to his "Artifact" child
            DatabaseReference mDatebaseBlogRef =
                    FirebaseDatabase.getInstance().getReference("Artifact/" + uid);

            // Access the database
            mDatebaseBlogRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Artifact upload_Blog = postSnapshot.getValue(Artifact.class);
                        if(!upload_Blog.isStatus()) {
                            mArtis.add(upload_Blog);
                        }
                    }
                    // Reorder the list
                    Collections.reverse(mArtis);

                    // Show empty -- Yat Yeung
                    if(mArtis.size() < 1){
                        mNoArtifactRL.setVisibility(View.VISIBLE);
                    }else{
                        mNoArtifactRL.setVisibility(View.GONE);
                    }

                    // Set up the recycle view
                    mAllArtisRVAA = new ArtifactAdapter(ArtifactslistActivity.this, mArtis);
                    mAllArtisRV.setAdapter(mAllArtisRVAA);
                    mAllArtisRVAA.setOnItemClickListener(ArtifactslistActivity.this);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        else {
            // Firebase variable. Database reference to his "Artifact" child
            DatabaseReference mDatebaseBlogRef =
                    FirebaseDatabase.getInstance().getReference("Artifact/" + uid);

            // Access the database
            mDatebaseBlogRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Artifact upload_Blog = postSnapshot.getValue(Artifact.class);
                        mArtis.add(upload_Blog);
                    }
                    // Reorder the list
                    Collections.reverse(mArtis);

                    // Show empty -- Yat Yeung
                    if (mArtis.size() < 1) {
                        mNoArtifactRL.setVisibility(View.VISIBLE);
                    }
                    else {
                        mNoArtifactRL.setVisibility(View.GONE);
                    }

                    // Set up the recycle view
                    mAllArtisRVAA = new ArtifactAdapter(ArtifactslistActivity.this, mArtis);
                    mAllArtisRV.setAdapter(mAllArtisRVAA);
                    mAllArtisRVAA.setOnItemClickListener(ArtifactslistActivity.this);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            // Add swiper funcionality
            MySwipeHelper swipeHelper = new MySwipeHelper(ArtifactslistActivity.this, mAllArtisRV,
                    200) {
                @Override
                public void instantiateMyButton(RecyclerView.ViewHolder viewHolder,
                                                List<MySwipeHelper.MyButton> buffer) {

                    // Add one option inside the swipe
                    buffer.add(new MyButton(ArtifactslistActivity.this,
                            "删除",
                            30,
                            0,
                            Color.parseColor("#FF3C30"),
                            new MyButtonClickListener() {
                                @Override
                                public void onClick(int pos) {
                                    Artifact deleteBlog = mArtis.get(pos);
                                    DatabaseReference deleteDBRef = FirebaseDatabase.getInstance().
                                            getReference("Artifact/" + uid)
                                            .child(deleteBlog.getKey());
                                    // Detele it from the database
                                    deleteDBRef.removeValue();

                                    // Initialize the blogs shown on the recycle view
                                    mArtis = new ArrayList<>();
                                }
                            }));
                    // Can add more buffer here
                    // ......
                }
            };
        }
    }

    // Listener for each artifact clicked by the user
    @Override
    public void onItemClick(int position) {
        Intent photoInfoIntent = new Intent(this, InfoPhotoActivity.class);
        Artifact clickedBlog = mArtis.get(position);

        // Put the artifact's info
        photoInfoIntent.putExtra(CLICKED_KEY, clickedBlog.getKey());
        photoInfoIntent.putExtra(CLICKED_IMAGE, clickedBlog.getImage());
        photoInfoIntent.putExtra(CLICKED_TITLE, clickedBlog.getTitle());
        photoInfoIntent.putExtra(CLICKED_DESC, clickedBlog.getDesc());
        photoInfoIntent.putExtra(CLICKED_STATUS, clickedBlog.isStatus());
        photoInfoIntent.putExtra(CLICKED_UserId, uid);

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