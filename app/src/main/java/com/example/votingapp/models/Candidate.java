package com.example.votingapp.models;

/**
 * Represents a candidate in the election.
 *
 * Firebase Realtime Database path:
 *   candidates/{candidateId}/
 *       name       – candidate's full name
 *       photoUrl   – URL of candidate's profile photo
 *       manifesto  – brief manifesto / party description
 *       voteCount  – running total of votes (updated via Firebase transaction)
 */
public class Candidate {

    private String candidateId;
    private String name;
    private String photoUrl;
    private String manifesto;
    private long voteCount;

    /** Required no-arg constructor for Firebase deserialization. */
    public Candidate() {}

    public Candidate(String candidateId, String name, String photoUrl, String manifesto) {
        this.candidateId = candidateId;
        this.name = name;
        this.photoUrl = photoUrl;
        this.manifesto = manifesto;
        this.voteCount = 0;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getCandidateId() { return candidateId; }

    public String getName() { return name; }

    public String getPhotoUrl() { return photoUrl; }

    public String getManifesto() { return manifesto; }

    public long getVoteCount() { return voteCount; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }

    public void setName(String name) { this.name = name; }

    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public void setManifesto(String manifesto) { this.manifesto = manifesto; }

    public void setVoteCount(long voteCount) { this.voteCount = voteCount; }
}
