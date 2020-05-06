package com.example.itprojects.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itprojects.R;
import com.example.itprojects.bean.Update;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Adapter for the bean "Artifact" class recycle view.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-09-04
 */

public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.ImageViewHolder> {

    // The context needed this adapter
    private Context mContext;

    // List of artifacts
    private List<Update> mUpdates;

    // Listener for when the users clicks one particular artifact
    private OnItemClickListener mListener;

    public UpdateAdapter(Context context, List<Update> Blogs) {
        mContext = context;
        mUpdates = Blogs;
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
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_update, parent,
                false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        // Get the artifact the user has clicked
        Update uploadCurrent = mUpdates.get(position);

        // Gengerate the current time
        long milliseconds = mUpdates.get(position).getUpload().getTime();
        String uploadTimeFormat = DateFormat.format("MM/dd/yyyy", new Date(milliseconds))
                .toString();

        // Assign the values
        holder.userName.setText(uploadCurrent.getUsername());
        holder.textViewTitle.setText(uploadCurrent.getTitle());
        holder.textViewDate.setText(uploadTimeFormat);
        holder.textViewDesc.setText(uploadCurrent.getDesc());
        Picasso.get()
                .load(uploadCurrent.getUserImgae())
                .placeholder(R.drawable.content_default_bg)
                .fit()
                .centerCrop()
                .into(holder.userImage);
        Picasso.get()
                .load(uploadCurrent.getArtifactImage())
                .placeholder(R.mipmap.ic_loading)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUpdates.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItem2Click(int postion);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        // Avatar
        public ImageView userImage;

        // Username
        private TextView userName;

        // Artifact's title
        public TextView textViewTitle;

        // Artifact's upload date
        public TextView textViewDate;

        // Artifact's description
        public TextView textViewDesc;

        // Artifact's image
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            // Get each component's ID
            userImage=itemView.findViewById(R.id.row_update_avatar);
            userName=itemView.findViewById(R.id.row_update_username);
            textViewTitle = itemView.findViewById(R.id.row_update_title);
            textViewDate = itemView.findViewById(R.id.row_update_date);
            textViewDesc = itemView.findViewById(R.id.row_update_desc);
            imageView = itemView.findViewById(R.id.row_update_photo);

            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItem2Click(position);
                        }
                    }
                }
            });
            // Set the artifact clickable
            imageView.setOnClickListener(new View.OnClickListener() {
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
