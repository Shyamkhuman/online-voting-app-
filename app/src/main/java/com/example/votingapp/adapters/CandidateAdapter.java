package com.example.votingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.votingapp.R;
import com.example.votingapp.models.Candidate;

import java.util.List;

/**
 * RecyclerView adapter for the candidate list screen.
 */
public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.ViewHolder> {

    public interface OnCandidateClickListener {
        void onCandidateClick(Candidate candidate);
    }

    private final List<Candidate> candidates;
    private final OnCandidateClickListener listener;

    public CandidateAdapter(List<Candidate> candidates, OnCandidateClickListener listener) {
        this.candidates = candidates;
        this.listener   = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_candidate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Candidate candidate = candidates.get(position);
        holder.tvName.setText(candidate.getName());
        holder.tvManifesto.setText(candidate.getManifesto());

        Glide.with(holder.ivPhoto.getContext())
                .load(candidate.getPhotoUrl())
                .placeholder(R.drawable.ic_person_placeholder)
                .error(R.drawable.ic_person_placeholder)
                .circleCrop()
                .into(holder.ivPhoto);

        holder.itemView.setOnClickListener(v -> listener.onCandidateClick(candidate));
    }

    @Override
    public int getItemCount() {
        return candidates.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView  tvName, tvManifesto;

        ViewHolder(View itemView) {
            super(itemView);
            ivPhoto     = itemView.findViewById(R.id.iv_candidate_photo);
            tvName      = itemView.findViewById(R.id.tv_candidate_name);
            tvManifesto = itemView.findViewById(R.id.tv_candidate_manifesto);
        }
    }
}
