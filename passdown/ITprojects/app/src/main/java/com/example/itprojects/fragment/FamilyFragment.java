package com.example.itprojects.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itprojects.activity.HomeActivity;
import com.example.itprojects.adapter.PassDownAdapter;
import com.example.itprojects.bean.PassDown;
import com.example.itprojects.interfaces.MyButtonClickListener;
import com.example.itprojects.util.MySwipeHelper;
import com.example.itprojects.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-09-07
 */

public class FamilyFragment extends Fragment implements PassDownAdapter.OnItemClickListener {

    // Used for retrieve one recycle view's info that the user clicks to
    public static final String CLICKED_UserId = "UserId";

    // Recycle view of the PassDowns for the users whom are the family members
    private RecyclerView mFamilyRV;
    private RelativeLayout mNoArtifactRL;
    private RelativeLayout mBottomRL;

    // And it's adapter
    private PassDownAdapter mFamilyRVA;
    private List<PassDown> mPassDowns;

    /**
     * Firebase variable
     */

    // Database reference
    private DatabaseReference mDatebaseRef;

    // This user's UID
    private String uid;

    /**
     * Required empty public constructor.
     */

    public FamilyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_family, container, false);

        // To change the subtitle
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("家人列表");

        // Initialize the recycle view
        mFamilyRV = view.findViewById(R.id.family_view);
        mFamilyRV.setHasFixedSize(false);
        mFamilyRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        // For empty artifact shown layout - Yat Yeung
        mNoArtifactRL = view.findViewById(R.id.family_empty);
        mBottomRL = view.findViewById(R.id.family_out);

        mPassDowns = new ArrayList<>();

        uid = FirebaseAuth.getInstance().getUid();
        mDatebaseRef = FirebaseDatabase.getInstance().getReference("Users/" + uid +
                "/friends/");

        mDatebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPassDowns.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PassDown upload_passdown = postSnapshot.getValue(PassDown.class);

                    mPassDowns.add(upload_passdown);
                }

                if (mPassDowns.size() < 1) {
                    mNoArtifactRL.setVisibility(View.VISIBLE);
                    mBottomRL.setVisibility((View.GONE));
                }
                else {
                    mNoArtifactRL.setVisibility(View.GONE);
                    mBottomRL.setVisibility((View.VISIBLE));
                }

                Collections.reverse(mPassDowns);
                // Set up the recycle view
                mFamilyRVA = new PassDownAdapter(getActivity(), mPassDowns);
                mFamilyRV.setAdapter(mFamilyRVA);
                mFamilyRVA.setOnItemClickListener(FamilyFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Add swiper funcionality
        MySwipeHelper swipeHelper = new MySwipeHelper(getActivity(), mFamilyRV,
                200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder,
                                            List<MySwipeHelper.MyButton> buffer) {
                buffer.add(new MyButton(getActivity(),
                        "删除",
                        30,
                        0,
                        Color.parseColor("#FF3C30"),
                        new MyButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                PassDown deletePassdown = mPassDowns.get(pos);
                                DatabaseReference deleteDBRef = FirebaseDatabase.
                                        getInstance().
                                        getReference("Users/" + uid).
                                        child("friends").
                                        child(deletePassdown.getUserId());
                                DatabaseReference deleteOtherDBRef = FirebaseDatabase.
                                        getInstance().
                                        getReference("Users/" + deletePassdown.getUserId()).
                                        child("friends").
                                        child(uid);

                                // Detele it from the database
                                deleteDBRef.removeValue();
                                deleteOtherDBRef.removeValue();
                                // Initialize the blogs shown on the recycle view
                                mPassDowns = new ArrayList<>();
                            }
                        }));
                // Can add more buffer here
                // ......
            }
        };

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Listener for each PassDown clicked by the user.
     *
     * @param position           The position that the user click on.
     *
     */

    @Override
    public void onItemClick(int position) {
        Intent HomeIntent = new Intent(getActivity(), HomeActivity.class);
        PassDown clickedPassDown = mPassDowns.get(position);
        HomeIntent.putExtra(CLICKED_UserId, clickedPassDown.getUserId());
        startActivity(HomeIntent);
    }
}