package com.example.itprojects.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itprojects.bean.Artifact;
import com.example.itprojects.bean.PassDown;
import com.example.itprojects.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapter for the bean "PassDown" class recycle view.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-09-04
 */

public class PassDownAdapter extends RecyclerView.Adapter<PassDownAdapter.PassDownHolder> {

    // The context needed this adapter
    private Context mContext;

    // List of PassDowns
    private List<PassDown> mPassDowns;
    private List<Artifact> mArtifacts;

    // Listener for when the users clicks one particular Passdown
    private OnItemClickListener mListener;

    public PassDownAdapter(Context context, List<PassDown> PassDowns) {
        mArtifacts=new ArrayList<>();
        mContext = context;
        mPassDowns = PassDowns;
    }

    /**
     * Assign the listener.
     *
     * @param listener
     */

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public PassDownAdapter.PassDownHolder onCreateViewHolder
            (@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.passdown_row, parent,
                false);
        return new PassDownHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PassDownHolder holder, int position) {

        // Get the PassDown the user has clicked
        final PassDown uploadCurrent = mPassDowns.get(position);
        // Get the user ID
        String uid = uploadCurrent.getUserId();

        // Retrieve and assign the user avata
        FirebaseDatabase.getInstance().getReference("Users/" + uid).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("image").getValue() != null) {
                    String avatar = dataSnapshot.child("image").getValue().toString();
                    // Assign the values
                    Picasso.get()
                            .load(avatar)
                            .placeholder(R.drawable.content_default_bg)
                            .fit()
                            .centerCrop()
                            .into(holder.UserAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Assign
        holder.PassDownName.setText(uploadCurrent.getPassdown_Name());
        holder.UserName.setText(uploadCurrent.getPassdown_Username());
        Picasso.get()
                .load(R.drawable.no_artifact)
                .placeholder(R.mipmap.ic_loading)
                .fit()
                .centerCrop()
                .into(holder.NewestPhoto);

        // Assign the user newest photo
        FirebaseDatabase.getInstance().getReference("Artifact/" + uid).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mArtifacts.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Artifact upload_Artifact= postSnapshot.getValue(Artifact.class);
                            System.out.println(upload_Artifact.getDesc());
                            if(upload_Artifact.isStatus()==false){
                                mArtifacts.add(upload_Artifact);
                            }
                        }
                        if (!mArtifacts.isEmpty()) {
                            Collections.reverse(mArtifacts);
                            String newestPhoto;

                            newestPhoto = mArtifacts.get(0).getImage();
                            Picasso.get()
                                    .load(newestPhoto)
                                    .placeholder(R.mipmap.ic_loading)
                                    .fit()
                                    .centerCrop()
                                    .into(holder.NewestPhoto);

                        }
                        else {
                            Picasso.get()
                                    .load(R.drawable.no_artifact)
                                    .placeholder(R.mipmap.ic_loading)
                                    .fit()
                                    .centerCrop()
                                    .into(holder.NewestPhoto);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public int getItemCount() {
        // show the total number of uploaded Artifacts
        return mPassDowns.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class PassDownHolder extends RecyclerView.ViewHolder {

        // Avatar
        public ImageView UserAvatar;

        // PassDown's name
        public TextView PassDownName;

        // PassDown's username
        public TextView UserName;

        // Newest photo
        public ImageView NewestPhoto;

        public PassDownHolder(@NonNull View itemView) {
            super(itemView);

            // Get each component's ID
            UserAvatar = itemView.findViewById(R.id.PassDown_avatar);
            PassDownName = itemView.findViewById(R.id.PassDown_name);
            UserName = itemView.findViewById(R.id.PassDown_User_Name);
            NewestPhoto = itemView.findViewById(R.id.PassDown_firstPhoto);

            // Set the PassDown clickable
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}



