package com.example.votingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.votingapp.R;
import com.example.votingapp.models.User;
import com.example.votingapp.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Handles new-user registration via Firebase Authentication (email/password).
 *
 * On successful registration:
 *  1. A new Firebase Auth account is created.
 *  2. A voter record is written to {@code voters/{uid}} in the Realtime Database
 *     with {@code hasVoted = false}.
 *  3. The user is redirected to CandidateListActivity.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseUtils.getAuth();

        etName            = findViewById(R.id.et_name);
        etEmail           = findViewById(R.id.et_email);
        etPassword        = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister       = findViewById(R.id.btn_register);
        tvLogin           = findViewById(R.id.tv_login);
        progressBar       = findViewById(R.id.progress_bar);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> finish()); // back to LoginActivity
    }

    private void attemptRegister() {
        String name            = etName.getText().toString().trim();
        String email           = etEmail.getText().toString().trim();
        String password        = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError(getString(R.string.error_name_required));
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_email_required));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_password_required));
            return;
        }
        if (password.length() < 6) {
            etPassword.setError(getString(R.string.error_password_length));
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.error_passwords_no_match));
            return;
        }

        setLoading(true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            saveVoterRecord(firebaseUser.getUid(), name, email);
                        }
                    } else {
                        setLoading(false);
                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : getString(R.string.error_registration_failed);
                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Persists the new voter record in Firebase Realtime Database.
     * {@code hasVoted} is initialised to {@code false} so the user can vote exactly once.
     */
    private void saveVoterRecord(String uid, String name, String email) {
        User user = new User(uid, name, email);

        FirebaseUtils.getVoterRef(uid).setValue(user)
                .addOnCompleteListener(task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,
                                R.string.registration_success, Toast.LENGTH_SHORT).show();
                        navigateToCandidateList();
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                R.string.error_saving_user, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToCandidateList() {
        Intent intent = new Intent(RegisterActivity.this, CandidateListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
    }
}
