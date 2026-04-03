package com.example.votingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingapp.R;
import com.example.votingapp.models.Candidate;

import java.util.List;

/**
 * RecyclerView adapter for the results screen.
 *
 * Each row displays the candidate name, vote count, percentage, and a
 * horizontal progress bar proportional to the total votes cast.
 */
public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private final List<Candidate> candidates;
    private long totalVotes;

    public ResultAdapter(List<Candidate> candidates) {
        this.candidates  = candidates;
        this.totalVotes  = 0;
    }

    public void setTotalVotes(long totalVotes) {
        this.totalVotes = totalVotes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Candidate candidate = candidates.get(position);
        long voteCount = candidate.getVoteCount();

        holder.tvName.setText(candidate.getName());
        holder.tvVoteCount.setText(String.valueOf(voteCount));

        int percentage = (totalVotes > 0) ? (int) ((voteCount * 100) / totalVotes) : 0;
        holder.tvPercentage.setText(holder.tvPercentage.getContext()
                .getString(R.string.percentage_format, percentage));
        holder.progressBar.setMax(100);
        holder.progressBar.setProgress(percentage);
    }

    @Override
    public int getItemCount() {
        return candidates.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView    tvName, tvVoteCount, tvPercentage;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            tvName       = itemView.findViewById(R.id.tv_result_candidate_name);
            tvVoteCount  = itemView.findViewById(R.id.tv_vote_count);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
            progressBar  = itemView.findViewById(R.id.progress_result);
        }
    }
}
