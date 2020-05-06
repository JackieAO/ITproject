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

import com.example.itprojects.bean.Artifact;
import com.example.itprojects.R;
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

public class ArtifactAdapter extends RecyclerView.Adapter<ArtifactAdapter.ImageViewHolder> {

    // The context needed this adapter
    private Context mContext;

    // List of artifacts
    private List<Artifact> mArtifacts;

    // Listener for when the users clicks one particular artifact
    private OnItemClickListener mListener;

    public ArtifactAdapter(Context context, List<Artifact> Blogs) {
        mContext = context;
        mArtifacts = Blogs;
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
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_artifact, parent,
                false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        // Get the artifact the user has clicked
        Artifact uploadCurrent = mArtifacts.get(position);

        // Gengerate the current time
        long milliseconds = mArtifacts.get(position).getUploadTime().getTime();
        String uploadTimeFormat = DateFormat.format("MM/dd/yyyy", new Date(milliseconds))
                .toString();

        // Assign the values
        holder.textViewTitle.setText(uploadCurrent.getTitle());
        holder.textViewDate.setText(uploadTimeFormat);
        holder.textViewDesc.setText(uploadCurrent.getDesc());
        Picasso.get()
                .load(uploadCurrent.getImage())
                .placeholder(R.mipmap.ic_loading)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mArtifacts.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

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
            textViewTitle = itemView.findViewById(R.id.post_title);
            textViewDate = itemView.findViewById(R.id.post_date);
            textViewDesc = itemView.findViewById((R.id.post_desc));
            imageView = itemView.findViewById(R.id.post_image);

            // Set the artifact clickable
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
