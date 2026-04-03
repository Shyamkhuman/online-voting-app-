package com.example.votingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.votingapp.R;
import com.example.votingapp.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows an authenticated user to cast a vote for the selected candidate.
 *
 * Key behaviours:
 *  - On open, the voter's record is checked: if {@code hasVoted == true} the
 *    vote button is disabled and the user is informed they have already voted.
 *  - Voting uses a multi-path update (atomic write) to:
 *      1. Set {@code voters/{uid}/hasVoted = true}
 *      2. Set {@code voters/{uid}/votedFor = candidateId}
 *      3. Increment {@code candidates/{candidateId}/voteCount} via a Transaction.
 *  - After a successful vote, the user is navigated to ResultsActivity.
 */
public class VotingActivity extends AppCompatActivity {

    public static final String EXTRA_CANDIDATE_ID   = "extra_candidate_id";
    public static final String EXTRA_CANDIDATE_NAME = "extra_candidate_name";

    private TextView tvCandidateName, tvVoteStatus;
    private Button btnVote;
    private ProgressBar progressBar;

    private String candidateId;
    private String candidateName;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_voting);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvCandidateName = findViewById(R.id.tv_candidate_name);
        tvVoteStatus    = findViewById(R.id.tv_vote_status);
        btnVote         = findViewById(R.id.btn_vote);
        progressBar     = findViewById(R.id.progress_bar);

        candidateId   = getIntent().getStringExtra(EXTRA_CANDIDATE_ID);
        candidateName = getIntent().getStringExtra(EXTRA_CANDIDATE_NAME);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // Should not happen – guarded by LoginActivity – but handle defensively.
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        uid = user.getUid();

        tvCandidateName.setText(candidateName);
        btnVote.setEnabled(false); // disabled until vote status is fetched

        checkVoteStatus();

        btnVote.setOnClickListener(v -> confirmVote());
    }

    /**
     * Reads the voter's record to determine whether the user has already voted.
     */
    private void checkVoteStatus() {
        setLoading(true);

        FirebaseUtils.getVoterRef(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setLoading(false);
                Boolean hasVoted = snapshot.child("hasVoted").getValue(Boolean.class);
                if (Boolean.TRUE.equals(hasVoted)) {
                    String votedFor = snapshot.child("votedFor").getValue(String.class);
                    tvVoteStatus.setText(getString(R.string.already_voted, votedFor));
                    tvVoteStatus.setVisibility(View.VISIBLE);
                    btnVote.setEnabled(false);
                } else {
                    btnVote.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setLoading(false);
                Toast.makeText(VotingActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmVote() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_confirm_title)
                .setMessage(getString(R.string.dialog_confirm_message, candidateName))
                .setPositiveButton(R.string.btn_yes, (dialog, which) -> submitVote())
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    /**
     * Submits a vote atomically:
     *  Step 1 – multi-path update to lock the voter record.
     *  Step 2 – Firebase transaction to safely increment the vote count.
     */
    private void submitVote() {
        setLoading(true);
        btnVote.setEnabled(false);

        // Step 1: lock voter record
        Map<String, Object> voterUpdate = new HashMap<>();
        voterUpdate.put("hasVoted", true);
        voterUpdate.put("votedFor", candidateId);

        FirebaseUtils.getVoterRef(uid).updateChildren(voterUpdate)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        incrementVoteCount();
                    } else {
                        setLoading(false);
                        btnVote.setEnabled(true);
                        Toast.makeText(VotingActivity.this,
                                R.string.error_vote_failed, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Safely increments the candidate's vote count using a Firebase transaction,
     * preventing race conditions when multiple users vote simultaneously.
     */
    private void incrementVoteCount() {
        FirebaseUtils.getVoteCountRef(candidateId).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Long count = currentData.getValue(Long.class);
                currentData.setValue(count == null ? 1L : count + 1L);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed,
                                   DataSnapshot currentData) {
                setLoading(false);
                if (committed) {
                    Toast.makeText(VotingActivity.this,
                            R.string.vote_success, Toast.LENGTH_SHORT).show();
                    navigateToResults();
                } else {
                    // Roll back the voter record if the transaction failed
                    Map<String, Object> rollback = new HashMap<>();
                    rollback.put("hasVoted", false);
                    rollback.put("votedFor", "");
                    FirebaseUtils.getVoterRef(uid).updateChildren(rollback);

                    btnVote.setEnabled(true);
                    String msg = error != null ? error.getMessage()
                            : getString(R.string.error_vote_failed);
                    Toast.makeText(VotingActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void navigateToResults() {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
