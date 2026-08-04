package com.group3.taamapp.SavedArtifactPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group3.taamapp.ExpandedArtifact;
import com.group3.taamapp.R;

import java.util.List;

/**
 * Displays saved artifacts inside the RecyclerView.
 */
public class SavedArtifactAdapter extends RecyclerView.Adapter<SavedArtifactAdapter.ArtifactViewHolder> {

    /**
     * Handles actions performed on each artifact card.
     */
    public interface OnArtifactActionListener {
        void onUnsave(ExpandedArtifact artifact);
        void onOpenArtifact(ExpandedArtifact artifact);
    }

    // List of saved artifacts and callback listener
    private final List<ExpandedArtifact> artifacts;
    private final OnArtifactActionListener listener;

    public SavedArtifactAdapter(List<ExpandedArtifact> artifacts,
                                OnArtifactActionListener listener) {
        this.artifacts = artifacts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArtifactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate a single artifact card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artifacts_display_cards, parent, false);

        return new ArtifactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtifactViewHolder holder, int position) {
        ExpandedArtifact artifact = artifacts.get(position);

        // Display artifact information
        holder.tvName.setText(artifact.getArtifactName());
        holder.tvCategory.setText(
                artifact.getCategory() + " · " + artifact.getDynastyPeriod()
        );

        // Load the artifact image, or show a placeholder if not available
        if (artifact.getImageUrl() != null && !artifact.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView)
                    .load(artifact.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Open the expanded artifact page
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOpenArtifact(artifact);
            }
        });

        // Remove the artifact from the saved collection
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUnsave(artifact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artifacts.size();
    }

    /**
     * Holds references to the views in a single artifact card.
     */
    static class ArtifactViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        TextView tvName;
        TextView tvCategory;
        ImageButton btnRemove;

        ArtifactViewHolder(@NonNull View itemView) {
            super(itemView);

            // Connect the card's views to their XML
            ivImage = itemView.findViewById(R.id.iv_artifact_image);
            tvName = itemView.findViewById(R.id.tv_artifact_name);
            tvCategory = itemView.findViewById(R.id.tv_artifact_category);
            btnRemove = itemView.findViewById(R.id.btn_remove_saved);
        }
    }
}