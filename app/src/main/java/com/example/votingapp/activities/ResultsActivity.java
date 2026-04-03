package com.example.votingapp.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.votingapp.R;
import com.example.votingapp.adapters.ResultAdapter;
import com.example.votingapp.models.Candidate;
import com.example.votingapp.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays real-time election results.
 *
 * Each row shows the candidate name, their vote count, and a percentage bar
 * relative to the total votes cast.  Results update live as votes arrive.
 */
public class ResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ResultAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvTotalVotes, tvEmpty;

    private final List<Candidate> candidateList = new ArrayList<>();

    private ValueEventListener resultsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_results);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView  = findViewById(R.id.recycler_results);
        progressBar   = findViewById(R.id.progress_bar);
        tvTotalVotes  = findViewById(R.id.tv_total_votes);
        tvEmpty       = findViewById(R.id.tv_empty);

        adapter = new ResultAdapter(candidateList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadResults();
    }

    private void loadResults() {
        progressBar.setVisibility(View.VISIBLE);

        resultsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                candidateList.clear();
                long totalVotes = 0;

                for (DataSnapshot child : snapshot.getChildren()) {
                    Candidate candidate = child.getValue(Candidate.class);
                    if (candidate != null) {
                        candidate.setCandidateId(child.getKey());
                        candidateList.add(candidate);
                        totalVotes += candidate.getVoteCount();
                    }
                }

                progressBar.setVisibility(View.GONE);
                tvEmpty.setVisibility(candidateList.isEmpty() ? View.VISIBLE : View.GONE);
                tvTotalVotes.setText(getString(R.string.total_votes, totalVotes));

                adapter.setTotalVotes(totalVotes);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ResultsActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        FirebaseUtils.getCandidatesRef().addValueEventListener(resultsListener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resultsListener != null) {
            FirebaseUtils.getCandidatesRef().removeEventListener(resultsListener);
        }
    }
}
