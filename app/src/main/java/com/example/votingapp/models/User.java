package com.example.votingapp.models;

/**
 * Represents a registered voter in the application.
 *
 * Firebase Realtime Database path:
 *   voters/{uid}/
 *       name      – display name
 *       email     – email address
 *       hasVoted  – true once the user has cast a vote
 *       votedFor  – candidateId the user voted for (set atomically with hasVoted)
 */
public class User {

    private String uid;
    private String name;
    private String email;
    private boolean hasVoted;
    private String votedFor;

    /** Required no-arg constructor for Firebase deserialization. */
    public User() {}

    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.hasVoted = false;
        this.votedFor = "";
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getUid() { return uid; }

    public String getName() { return name; }

    public String getEmail() { return email; }

    public boolean isHasVoted() { return hasVoted; }

    public String getVotedFor() { return votedFor; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setUid(String uid) { this.uid = uid; }

    public void setName(String name) { this.name = name; }

    public void setEmail(String email) { this.email = email; }

    public void setHasVoted(boolean hasVoted) { this.hasVoted = hasVoted; }

    public void setVotedFor(String votedFor) { this.votedFor = votedFor; }
}
