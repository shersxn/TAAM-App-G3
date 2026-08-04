package com.group3.taamapp;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import com.bumptech.glide.Glide;
import com.group3.taamapp.Bases.BundleInitializer;

public class ViewCardAdapter extends RecyclerView.Adapter<ViewCardAdapter.MyViewHolder> {
    public interface OnArtifactActionListener {
        void onOpenArtifact(ExpandedArtifact artifact);
    }
    Context context;
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
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.artifact_view_card, parent, false);
        return new ViewCardAdapter.MyViewHolder(view);
    }

    public String shortend(String s, int n) {
        if (s.length() > n) {
            s = s.substring(0, n) + "...";
        }
        return s;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewCardAdapter.MyViewHolder holder, int position) {
        ExpandedArtifact artifact = viewCards.get(position);

        holder.tvName.setText(shortend(viewCards.get(position).getArtifactName(), 15));
        holder.tvDescription.setText(shortend(viewCards.get(position).getDescription(), 40));
        holder.tvPeriod.setText(shortend(viewCards.get(position).getDynastyPeriod(), 25));
        Glide.with(holder.itemView.getContext())
                .load(viewCards.get(position).getImageUrl())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOpenArtifact(artifact);
        });

//        holder.tvCard.setOnClickListener(item -> {
//            loadFragment(new HomeFragment(), new BundleInitializer() {
//                public void initBundle(Bundle bundle) {
//                    bundle.putString("email", email);
//                }
//            });
//        });

    }

    @Override
    public int getItemCount() {
        return viewCards.size();
    }

    public void filterList(ArrayList<ExpandedArtifact> filteredList) {
        viewCards = filteredList;
        notifyDataSetChanged();
    }

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
