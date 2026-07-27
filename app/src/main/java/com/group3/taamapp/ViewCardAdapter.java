package com.group3.taamapp;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import com.bumptech.glide.Glide;

public class ViewCardAdapter extends RecyclerView.Adapter<ViewCardAdapter.MyViewHolder> {

    Context context;
    ArrayList<ExpandedArtifact> viewCards;

    public ViewCardAdapter(Context context, ArrayList<ExpandedArtifact> viewCards) {
        this.context = context;
        this.viewCards = viewCards;
    }

    @NonNull
    @Override
    public ViewCardAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.artifact_view_card, parent, false);
        return new ViewCardAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewCardAdapter.MyViewHolder holder, int position) {
        holder.tvName.setText(viewCards.get(position).getArtifactName());
        holder.tvDescription.setText(viewCards.get(position).getDescription());
        holder.tvPeriod.setText(viewCards.get(position).getDynastyPeriod());
        Glide.with(holder.itemView.getContext())
                .load(viewCards.get(position).getImageUrl())
                .into(holder.imageView);

//        holder.tvDetails.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent expandDetails = new Intent(ViewCardAdapter.this, )
//            }
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
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewCard);
            tvName = itemView.findViewById(R.id.textArtefactName);
            tvDescription = itemView.findViewById(R.id.textDescription);
            tvPeriod = itemView.findViewById(R.id.textPeriod);
            tvDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
