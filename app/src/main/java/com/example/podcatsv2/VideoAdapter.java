package com.example.podcatsv2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.podcatsv2.R;
import com.example.podcatsv2.Video;
import com.example.podcatsv2.VideoPlayerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context context;
    private List<Video> videos;


    private OnItemClickListener listener;
    private FirebaseAuth mAuth;
    private DatabaseReference likesRef;

    // Interface for handling click events
    public interface OnItemClickListener {
        void onItemClick(Video video);
    }

    // Constructor
    public VideoAdapter(Context context, List<Video> videos, OnItemClickListener listener) {
        this.context = context;
        this.videos = videos;
        this.listener = listener;
        this.mAuth = FirebaseAuth.getInstance();
        this.likesRef = FirebaseDatabase.getInstance().getReference("Likes");
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoViewHolder holder, int position) {
        final Video video = videos.get(position);
        holder.textViewTitle.setText(video.getTitle());
        holder.textViewPublicationTime.setText(video.getPublicationTime());

        // Load thumbnail using Glide
        Glide.with(context)
                .load(video.getThumbnailUrl())
                .into(holder.imageViewThumbnail);

        // Set like count
        holder.textViewLikeCount.setText(video.getLikeCount() + " " + context.getString(R.string.likes));

        // Check if current user has liked this video
        if (mAuth.getCurrentUser() != null) {
            likesRef.child(video.getVideoId()).child(mAuth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                holder.buttonLike.setEnabled(false);
                                holder.buttonLike.setText(context.getString(R.string.liked));
                            } else {
                                holder.buttonLike.setEnabled(true);
                                holder.buttonLike.setText(context.getString(R.string.like));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                            Log.e("VideoAdapter", "Error checking like status: " + error.getMessage());
                        }
                    });
        }

        // Like button functionality
        holder.buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null) {
                    String userId = mAuth.getCurrentUser().getUid();
                    likesRef.child(video.getVideoId()).child(userId).setValue(true)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Increment like count
                                    FirebaseDatabase.getInstance().getReference("Videos")
                                            .child(video.getVideoId()).child("likeCount")
                                            .setValue(video.getLikeCount() + 1);
                                    holder.buttonLike.setEnabled(false);
                                    holder.buttonLike.setText(context.getString(R.string.liked));
                                    Toast.makeText(context, context.getString(R.string.like_success), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, context.getString(R.string.like_failed), Toast.LENGTH_SHORT).show();
                                    Log.e("VideoAdapter", "Like failed: " + task.getException().getMessage());
                                }
                            });
                } else {
                    Toast.makeText(context, context.getString(R.string.login_required), Toast.LENGTH_SHORT).show();
                    Log.e("VideoAdapter", "User not authenticated.");
                }
            }
        });

        // Click on thumbnail to play video
        holder.imageViewThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(video);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    // ViewHolder class
    class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewThumbnail;
        TextView textViewTitle, textViewPublicationTime, textViewLikeCount;
        Button buttonLike;

        VideoViewHolder(View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewPublicationTime = itemView.findViewById(R.id.textViewPublicationTime);
            textViewLikeCount = itemView.findViewById(R.id.textViewLikeCount);
            buttonLike = itemView.findViewById(R.id.buttonLike);
        }
    }
}
