package com.group3.taamapp;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import com.bumptech.glide.Glide;

public class ViewCardAdapter extends RecyclerView.Adapter<ViewCardAdapter.MyViewHolder> {
    // Handles actions performed on each artifact card
    public interface OnArtifactActionListener {
        void onOpenArtifact(ExpandedArtifact artifact);
    }
    Context context;
    // List of view artifacts
    ArrayList<ExpandedArtifact> viewCards;
    OnArtifactActionListener listener;

    public ViewCardAdapter(Context context, ArrayList<ExpandedArtifact> viewCards, OnArtifactActionListener listener) {
        this.context = context;
        this.viewCards = viewCards;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewCardAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate a single view artifact card and create a view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.artifact_view_card, parent, false);
        return new ViewCardAdapter.MyViewHolder(view);
    }

    // Shorten the string that exceeds the given maximum length
    public String shorten(String s, int n) {
        if (s.length() > n) {
            s = s.substring(0, n) + "...";
        }
        else {
            // Pad the string with spaces to maintain the same card height
            s = s + " ".repeat(n - s.length());
        }
        return s;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewCardAdapter.MyViewHolder holder, int position) {
        ExpandedArtifact artifact = viewCards.get(position);

        // Display artifact information
        holder.tvName.setText(shorten(artifact.getArtifactName(), 18));
        holder.tvDescription.setText(shorten(artifact.getDescription(), 50));
        holder.tvPeriod.setText(shorten(artifact.getDynastyPeriod(), 25));

        // Load and display the artifact image, or placeholder if not available
        Glide.with(holder.itemView.getContext())
                .load(artifact.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.imageView);

        // Open the expanded artifact page
        holder.tvCard.setOnClickListener(item -> {
            if (listener != null) listener.onOpenArtifact(artifact);
        });
    }

    // Return the number of displayed artifacts
    @Override
    public int getItemCount() {
        return viewCards.size();
    }

    // Replace the current list of artifacts with filtered artifacts
    public void filterList(ArrayList<ExpandedArtifact> filteredList) {
        viewCards = filteredList;
        notifyDataSetChanged();
    }

    // Holds references to the views in a single artifact card.
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvName, tvDescription, tvPeriod, tvDetails;
        CardView tvCard;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewCard);
            tvName = itemView.findViewById(R.id.textArtefactName);
            tvDescription = itemView.findViewById(R.id.textDescription);
            tvPeriod = itemView.findViewById(R.id.textPeriod);
            tvCard = itemView.findViewById(R.id.artifactCard);
        }
    }
}
