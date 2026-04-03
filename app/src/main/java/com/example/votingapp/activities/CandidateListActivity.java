package com.example.votingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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
import com.example.votingapp.adapters.CandidateAdapter;
import com.example.votingapp.models.Candidate;
import com.example.votingapp.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays the list of candidates fetched in real-time from Firebase.
 *
 * Tapping a candidate card opens {@link VotingActivity} for that candidate.
 * The toolbar contains a "Results" option and a sign-out option.
 */
public class CandidateListActivity extends AppCompatActivity
        implements CandidateAdapter.OnCandidateClickListener {

    private RecyclerView recyclerView;
    private CandidateAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private final List<Candidate> candidateList = new ArrayList<>();

    private ValueEventListener candidatesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_candidates);
        }

        recyclerView = findViewById(R.id.recycler_candidates);
        progressBar  = findViewById(R.id.progress_bar);
        tvEmpty      = findViewById(R.id.tv_empty);

        adapter = new CandidateAdapter(candidateList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadCandidates();
    }

    private void loadCandidates() {
        progressBar.setVisibility(View.VISIBLE);

        candidatesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                candidateList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Candidate candidate = child.getValue(Candidate.class);
                    if (candidate != null) {
                        candidate.setCandidateId(child.getKey());
                        candidateList.add(candidate);
                    }
                }
                progressBar.setVisibility(View.GONE);
                tvEmpty.setVisibility(candidateList.isEmpty() ? View.VISIBLE : View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CandidateListActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        FirebaseUtils.getCandidatesRef().addValueEventListener(candidatesListener);
    }

    @Override
    public void onCandidateClick(Candidate candidate) {
        Intent intent = new Intent(this, VotingActivity.class);
        intent.putExtra(VotingActivity.EXTRA_CANDIDATE_ID,   candidate.getCandidateId());
        intent.putExtra(VotingActivity.EXTRA_CANDIDATE_NAME, candidate.getName());
        startActivity(intent);
    }

    // ── Options menu ─────────────────────────────────────────────────────────

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_candidate_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_results) {
            startActivity(new Intent(this, ResultsActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (candidatesListener != null) {
            FirebaseUtils.getCandidatesRef().removeEventListener(candidatesListener);
        }
    }
}
