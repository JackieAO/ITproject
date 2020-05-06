package com.example.itprojects.fragment;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-09-07
 */

import android.content.Intent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itprojects.R;
import com.example.itprojects.activity.HomeActivity;
import com.example.itprojects.activity.InfoPhotoActivity;
import com.example.itprojects.adapter.UpdateAdapter;
import com.example.itprojects.base.MainActivity;
import com.example.itprojects.bean.Artifact;
import com.example.itprojects.bean.PassDown;
import com.example.itprojects.bean.SortByDate;

import com.example.itprojects.bean.Update;
import com.example.itprojects.bean.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.Empty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public class ArtifactsFragment extends Fragment implements UpdateAdapter.OnItemClickListener {
    // Used for retrieve one recycle view's info that the user clicks to
    public static final String CLICKED_IMAGE = "imageUrl";
    public static final String CLICKED_TITLE = "titleName";
    public static final String CLICKED_DESC = "descContent";
    public static final String CLICKED_KEY = "KeyContent";
    public static final String CLICKED_STATUS = "status";
    public static final String CLICKED_UserId = "UserId";

    // Recycle view for the artifacts
    private RecyclerView mArtifactsRV;
    private UpdateAdapter mArtifactsRVA;

    // List of artifacts
    private List<Update> mUpdates;
    private String Username;
    private String UserImage;

    // Reference to the storage
    private DatabaseReference mDatebaseRef;
    private String uid;

    // For empty artifact shown layout - Yat Yeung
    private RelativeLayout mNoArtifactRL;
    private  boolean isUserExist;
    /**
     * Required empty public constructor.
     */

    public ArtifactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("传家动态");

        // Initialize the recycle view
        mArtifactsRV = view.findViewById(R.id.recycler_view);
        mArtifactsRV.setHasFixedSize(false);
        mArtifactsRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        // For empty artifact shown layout - Yat Yeung
        mNoArtifactRL = view.findViewById(R.id.no_artifact);

        // Initialize the list

        mUpdates=new ArrayList<>();



        // Retrieve the Firebase UID
        uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference("Users/" + uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mUpdates.clear();
                        if(dataSnapshot.child("friends").exists()){
                            isUserExist=true;
                        }
                        else {
                            isUserExist=false;
                        }

                        User mUser= dataSnapshot.getValue(User.class);
                        Username =mUser.getSurName()+ " "+mUser.getFirstName();
                        UserImage=mUser.getImage();
                        //get my artifacts
                        FirebaseDatabase.getInstance().getReference("Artifact/" + uid)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {

                                            Artifact upload_Blog = Snapshot.getValue(Artifact.class);

                                            Update UserUpdate=new Update(Username,upload_Blog.getUploadTime(),UserImage
                                                    ,upload_Blog.getTitle(),upload_Blog.getDesc(),upload_Blog.getImage()
                                                    ,upload_Blog.isStatus(),upload_Blog.getUserId(),upload_Blog.getKey());

                                            mUpdates.add(UserUpdate);
                                        }
                                        if (isUserExist==false) {
                                            //order by Date
                                            Collections.sort(mUpdates, new SortByDate());
                                            Collections.reverse(mUpdates);
                                            //added to recyclerview
                                            mArtifactsRVA = new UpdateAdapter(getActivity(), mUpdates);
                                            mArtifactsRV.setAdapter(mArtifactsRVA);
                                            mArtifactsRVA.setOnItemClickListener(ArtifactsFragment.this);


                                            if (mUpdates.size() < 1) {
                                                mNoArtifactRL.setVisibility(View.VISIBLE);
                                            }
                                            else {
                                                mNoArtifactRL.setVisibility(View.GONE);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        //get friends' artifacts
        mDatebaseRef=FirebaseDatabase.getInstance().getReference("Users/" + uid +
                "/friends/");
        mDatebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for ( DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    final List<User> user=new ArrayList<>() ;
                    FirebaseDatabase.getInstance().getReference("Users/" + postSnapshot.getKey())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot Snapshot1) {
                                    User mUser=Snapshot1.getValue(User.class);
                                    user.add(mUser);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                    FirebaseDatabase.getInstance().getReference("Artifact/" +postSnapshot.getKey() )
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                                    User new_user=user.get(0);
                                    Username=new_user.getSurName()+' '+new_user.getFirstName();
                                    UserImage=new_user.getImage();
                                    for (DataSnapshot Snapshot2 : dataSnapshots.getChildren()) {
                                        Artifact upload_Blog = Snapshot2.getValue(Artifact.class);
                                        Update UserUpdate=new Update(Username,upload_Blog.getUploadTime(),UserImage
                                                ,upload_Blog.getTitle(),upload_Blog.getDesc(),upload_Blog.getImage()
                                                ,upload_Blog.isStatus(),upload_Blog.getUserId(),upload_Blog.getKey());

                                        if (!UserUpdate.isStatus()) {
                                            mUpdates.add(UserUpdate);
                                        }
                                    }
                                    user.remove(0);


                                    if (mUpdates.size() < 1) {
                                        mNoArtifactRL.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        mNoArtifactRL.setVisibility(View.GONE);
                                    }

                                    //order by Date
                                    Collections.sort(mUpdates, new SortByDate());
                                    Collections.reverse(mUpdates);
                                    //added to recyclerview
                                    mArtifactsRVA = new UpdateAdapter(getActivity(), mUpdates);
                                    mArtifactsRV.setAdapter(mArtifactsRVA);
                                    mArtifactsRVA.setOnItemClickListener(ArtifactsFragment.this);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Inflate the layout for this fragment
        return view;
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
        Update clickedBlog = mUpdates.get(position);

        // Put the artifact's info
        photoInfoIntent.putExtra(CLICKED_KEY, clickedBlog.getKey());
        photoInfoIntent.putExtra(CLICKED_IMAGE, clickedBlog.getArtifactImage());
        photoInfoIntent.putExtra(CLICKED_TITLE, clickedBlog.getTitle());
        photoInfoIntent.putExtra(CLICKED_DESC, clickedBlog.getDesc());
        photoInfoIntent.putExtra(CLICKED_STATUS, clickedBlog.isStatus());
        photoInfoIntent.putExtra(CLICKED_UserId, clickedBlog.getUserId());

        startActivity(photoInfoIntent);
    }

    // When click the user avatar
    @Override
    public void onItem2Click(int position) {
        Intent HomeIntent = new Intent(getActivity(), HomeActivity.class);
        Update clickedPassDown = mUpdates.get(position);

        // Click is the user itself, go straight to his home fragment
        if (clickedPassDown.getUserId().equals(uid)) {
            Intent MainIntent = new Intent(getActivity(), MainActivity.class);
            MainIntent.putExtra("clicked_id", 1);
            startActivity(MainIntent);
        }
        else {
            HomeIntent.putExtra(CLICKED_UserId, clickedPassDown.getUserId());
            startActivity(HomeIntent);
        }
    }
}