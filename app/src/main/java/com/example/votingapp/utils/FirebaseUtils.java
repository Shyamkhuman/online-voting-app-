package com.example.votingapp.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Central access point for Firebase services.
 *
 * Database structure:
 * <pre>
 * voting-app/
 * ├── voters/
 * │   └── {uid}/
 * │       ├── name       : String
 * │       ├── email      : String
 * │       ├── hasVoted   : boolean
 * │       └── votedFor   : String (candidateId)
 * └── candidates/
 *     └── {candidateId}/
 *         ├── name       : String
 *         ├── photoUrl   : String
 *         ├── manifesto  : String
 *         └── voteCount  : long
 * </pre>
 */
public final class FirebaseUtils {

    private static final String DB_ROOT       = "voting-app";
    private static final String NODE_VOTERS    = "voters";
    private static final String NODE_CANDIDATES = "candidates";

    private FirebaseUtils() {}

    /** Returns the shared FirebaseAuth instance. */
    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    /** Returns the root DatabaseReference for this app. */
    public static DatabaseReference getDbRoot() {
        return FirebaseDatabase.getInstance().getReference(DB_ROOT);
    }

    /** Returns the {@code voters} node reference. */
    public static DatabaseReference getVotersRef() {
        return getDbRoot().child(NODE_VOTERS);
    }

    /**
     * Returns the reference to a specific voter's record.
     *
     * @param uid Firebase Auth UID
     */
    public static DatabaseReference getVoterRef(String uid) {
        return getVotersRef().child(uid);
    }

    /** Returns the {@code candidates} node reference. */
    public static DatabaseReference getCandidatesRef() {
        return getDbRoot().child(NODE_CANDIDATES);
    }

    /**
     * Returns the reference to a specific candidate's record.
     *
     * @param candidateId unique candidate identifier
     */
    public static DatabaseReference getCandidateRef(String candidateId) {
        return getCandidatesRef().child(candidateId);
    }

    /**
     * Convenience: returns the {@code voteCount} field reference for a candidate.
     *
     * @param candidateId unique candidate identifier
     */
    public static DatabaseReference getVoteCountRef(String candidateId) {
        return getCandidateRef(candidateId).child("voteCount");
    }
}
