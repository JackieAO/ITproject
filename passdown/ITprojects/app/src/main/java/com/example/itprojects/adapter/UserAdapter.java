package com.example.itprojects.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itprojects.bean.User;
import com.example.itprojects.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter for the bean "User" class recycle view.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-09-04
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    // The context needed this adapter
    private Context mContext;

    // List of users
    private ArrayList<User> mUsers;

    // Listener for when the users clicks one particular user
    private OnItemClickListener mListener;

    public UserAdapter(Context context, ArrayList<User> Users) {
        mContext = context;
        mUsers = Users;
    }

    /**
     * Assign the listener.
     *
     * @param listener
     */
    public void setOnItemClickListener(UserAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.friend_row, parent,
                false);
        return new UserHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {

        // Get the users list the user has clicked
        User uploadCurrent = mUsers.get(position);

        // Assign the values
        holder.emailView.setText(uploadCurrent.getEmail());
        holder.nameView.setText(uploadCurrent.getSurName() + " " + uploadCurrent.getFirstName());
        Picasso.get()
                .load(uploadCurrent.getImage())
                .placeholder(R.mipmap.ic_unkown_user)
                .fit()
                .centerCrop()
                .into(holder.UserView);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class UserHolder extends RecyclerView.ViewHolder {

        // User's email
        public TextView emailView;

        // User's name
        public TextView nameView;

        // User's avatar
        public ImageView UserView;

        public UserHolder(@NonNull View itemView) {
            super(itemView);

            // Get each component's ID
            emailView = itemView.findViewById(R.id.friend_row_email);
            nameView = itemView.findViewById(R.id.friend_row_name);
            UserView = itemView.findViewById(R.id.friend_row_image);

            // Set the user clickable
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
