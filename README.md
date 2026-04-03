# Online Voting App

An Android application for secure, real-time online voting powered by Firebase.

## Features

| Feature | Details |
|---|---|
| 🔐 Authentication | Email/password login & registration via Firebase Auth |
| 📋 Candidate List | Real-time list of candidates with photo, name, and manifesto |
| 🗳️ One-Time Vote | Each voter can cast exactly one vote (transaction-locked) |
| 📊 Live Results | Real-time vote counts with percentage progress bars |

## Tech Stack

- **Android (Java/XML)** — Activities, RecyclerView adapters, vector drawables
- **Firebase Authentication** — Email/password sign-in & registration
- **Firebase Realtime Database** — Voters, candidates, and vote counts
- **Glide** — Loading candidate profile images

## Project Structure

```
app/src/main/
├── java/com/example/votingapp/
│   ├── activities/
│   │   ├── LoginActivity.java          # Sign-in screen
│   │   ├── RegisterActivity.java       # New-user registration
│   │   ├── CandidateListActivity.java  # Browse all candidates
│   │   ├── VotingActivity.java         # Cast vote (one-time lock)
│   │   └── ResultsActivity.java        # Live result tallies
│   ├── adapters/
│   │   ├── CandidateAdapter.java       # RecyclerView adapter for candidates
│   │   └── ResultAdapter.java          # RecyclerView adapter for results
│   ├── models/
│   │   ├── User.java                   # Voter model
│   │   └── Candidate.java              # Candidate model
│   └── utils/
│       └── FirebaseUtils.java          # Centralised Firebase references
└── res/
    ├── layout/          # XML layouts for every screen + list items
    ├── values/          # colors.xml, strings.xml, themes.xml
    ├── drawable/        # Vector icons
    └── menu/            # Options menu for CandidateListActivity
```

## Firebase Database Structure

```
voting-app/
├── voters/
│   └── {uid}/
│       ├── name       : String
│       ├── email      : String
│       ├── hasVoted   : Boolean   ← prevents duplicate voting
│       └── votedFor   : String    ← candidateId
└── candidates/
    └── {candidateId}/
        ├── name       : String
        ├── photoUrl   : String
        ├── manifesto  : String
        └── voteCount  : Long      ← incremented via Firebase Transaction
```

## Setup

### 1. Firebase Project
1. Create a project at [Firebase Console](https://console.firebase.google.com/).
2. Enable **Email/Password** sign-in under *Authentication → Sign-in method*.
3. Create a **Realtime Database** and set rules (see below).
4. Download **`google-services.json`** and place it in the `app/` directory.  
   *(This file is git-ignored — never commit it.)*

### 2. Firebase Security Rules (Realtime Database)

```json
{
  "rules": {
    "voting-app": {
      "voters": {
        "$uid": {
          ".read":  "$uid === auth.uid",
          ".write": "$uid === auth.uid && !data.child('hasVoted').val()"
        }
      },
      "candidates": {
        ".read": "auth !== null",
        "$candidateId": {
          "voteCount": {
            ".write": "auth !== null"
          }
        }
      }
    }
  }
}
```

### 3. Seed Candidates
Add candidate records directly in the Firebase Console under  
`voting-app/candidates/{auto-id}` with the fields: `name`, `photoUrl`, `manifesto`, `voteCount: 0`.

### 4. Build & Run
```bash
# Open the project in Android Studio and sync Gradle, then run on a device/emulator.
./gradlew assembleDebug
```

## Running Unit Tests

```bash
./gradlew :app:test
```
