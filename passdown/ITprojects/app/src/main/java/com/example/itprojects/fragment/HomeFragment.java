package com.example.itprojects.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itprojects.activity.ArtifactslistActivity;
import com.example.itprojects.activity.InfoPhotoActivity;
import com.example.itprojects.adapter.ScrollAdapter;
import com.example.itprojects.bean.Artifact;
import com.example.itprojects.R;
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

public class HomeFragment extends Fragment implements ScrollAdapter.OnItemClickListener{

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

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
//            Log.d("mTEST", getArguments().getString("UserID"));
            userID = getArguments().getString("UserID");
            editable = false;
        }
        else {
            userID = FirebaseAuth.getInstance().getUid();
            editable = true;
        }

        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("传家主页");


        // Get each component's ID
        mNewestIV = view.findViewById(R.id.fragment_home_newest_image);
        mNewestTV = view.findViewById(R.id.fragment_home_newest_title);
        mShowAllRL = view.findViewById(R.id.fragment_home_view_all);
        mEditDescRL = view.findViewById(R.id.fragment_home_edit);
        mPassDownDescET = view.findViewById(R.id.fragment_home_desc);
        mPhotosRV = view.findViewById(R.id.fragment_home_images);
        mNoArtifact = view.findViewById(R.id.no_data_description);

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
                        mArtifacts.add(upload_Blog);
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
                            Intent photoInfoIntent = new Intent(getActivity(), InfoPhotoActivity.class);

                            // Put the artifact's info
                            photoInfoIntent.putExtra(CLICKED_KEY, clickedBlog.getKey());
                            photoInfoIntent.putExtra(CLICKED_IMAGE, clickedBlog.getImage());
                            photoInfoIntent.putExtra(CLICKED_TITLE, clickedBlog.getTitle());
                            photoInfoIntent.putExtra(CLICKED_DESC, clickedBlog.getDesc());
                            photoInfoIntent.putExtra(CLICKED_STATUS, clickedBlog.isStatus());
                            if (editable) {
                                photoInfoIntent.putExtra(CLICKED_EDITABLE, "Yes");
                            }
                            else {
                                photoInfoIntent.putExtra(CLICKED_EDITABLE, "No");
                            }

                            startActivity(photoInfoIntent);
                        }
                    });

                    mArtifacts.remove(0);
                }
                else if (mArtifacts.size()<1) {
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
                            Intent photoInfoIntent = new Intent(getActivity(), InfoPhotoActivity.class);

                            // Put the artifact's info
                            photoInfoIntent.putExtra(CLICKED_KEY, clickedBlog.getKey());
                            photoInfoIntent.putExtra(CLICKED_IMAGE, clickedBlog.getImage());
                            photoInfoIntent.putExtra(CLICKED_TITLE, clickedBlog.getTitle());
                            photoInfoIntent.putExtra(CLICKED_DESC, clickedBlog.getDesc());
                            photoInfoIntent.putExtra(CLICKED_STATUS, clickedBlog.isStatus());
                            if (editable) {
                                photoInfoIntent.putExtra(CLICKED_EDITABLE, "Yes");
                            }
                            else {
                                photoInfoIntent.putExtra(CLICKED_EDITABLE, "No");
                            }

                            startActivity(photoInfoIntent);
                        }
                    });

                    // Remove the top one as it already shown
                    mArtifacts.remove(0);
                }

                // Show the horizontal recycle view of partial photos at the middle
                mPhotosRV.setHasFixedSize(false);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                        LinearLayoutManager.HORIZONTAL, false);
                mPhotosRV.setLayoutManager(layoutManager);
                mAdapter = new ScrollAdapter(getActivity(), mArtifacts);
                mPhotosRV.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(HomeFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Show all photos in one page
        mShowAllRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ArtifactsIntent = new Intent(getActivity(), ArtifactslistActivity.class);
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
        final TextView mEditDescTV = view.findViewById(R.id.fragment_home_desc_edit_text);
        final ImageView mEditDescIV = view.findViewById(R.id.fragment_home_edit_pic);

        if (!uid.equals(userID)) {
            mEditDescTV.setVisibility(View.GONE);
            mEditDescIV.setVisibility(View.GONE);
            mEditDescTV.setEnabled(false);
            mEditDescIV.setEnabled(false);
        }
        else {
            // Track the status of the button
            mEditDescRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // The user has clicked "完成" to finish his modification
                    if (edit_active) {
                        edit_active = false;

                        // Change to image icon instead of text -- Yat Yeung
                        mEditDescIV.setImageResource(R.drawable.signin_normal_n);
                        mEditDescTV.setText("编辑");
                        mPassDownDescET.setKeyListener(null);

                        // Update the text the user has edited
                        startPosting();
                    }
                    // The user has clicked "编辑" to edit mode
                    else {
                        edit_active = true;

                        // Change to image icon instead of text -- Yat Yeung
                        mEditDescIV.setImageResource(R.drawable.signin_select);
                        mEditDescTV.setText("完成");

                        // The user can now edit the description
                        mPassDownDescET.setKeyListener(mEditDescKL);
                    }
                }
            });
        }

        // Inflate the layout for this fragment
        return view;
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

        Toasty.success(getActivity(), "已完成修改", Toast.LENGTH_SHORT).show();
    }

    /**
     * Listener for each artifact clicked by the user.
     *
     * @param position           The position that the user click on.
     *
     */

    @Override
    public void onItemClick(int position) {
        Intent photoInfoIntent = new Intent(getActivity(), InfoPhotoActivity.class);
        Artifact clickedBlog = mArtifacts.get(position);

        // Put the artifact's info
        photoInfoIntent.putExtra(CLICKED_KEY, clickedBlog.getKey());
        photoInfoIntent.putExtra(CLICKED_IMAGE, clickedBlog.getImage());
        photoInfoIntent.putExtra(CLICKED_TITLE, clickedBlog.getTitle());
        photoInfoIntent.putExtra(CLICKED_DESC, clickedBlog.getDesc());
        photoInfoIntent.putExtra(CLICKED_STATUS, clickedBlog.isStatus());
        if (editable) {
            photoInfoIntent.putExtra(CLICKED_EDITABLE, "Yes");
        }
        else {
            photoInfoIntent.putExtra(CLICKED_EDITABLE, "No");
        }

        startActivity(photoInfoIntent);
    }
}