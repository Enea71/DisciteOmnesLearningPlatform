// LoginActivity.java
package com.example.disciteomneslearningplatform;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.example.disciteomneslearningplatform.ui.DialogUtil;
import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailInput, passwordInput;
    private Button loginButton;
    private ProgressBar loadingBar;
    private Group formGroup;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Create this layout
        FirebaseApp.initializeApp(this);

        // Install the Debug App Check provider

        // init Firebase
        mAuth = FirebaseAuth.getInstance();
        // check if instance is correctly configured
        try {
            FirebaseApp app = FirebaseApp.getInstance();
            Log.d("FirebaseConfig", "App options: " + app.getOptions());
        } catch (IllegalStateException e) {
            Log.e("FirebaseConfig", "FirebaseApp not initialized", e);
        }
        emailInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingBar = findViewById(R.id.loading);
        formGroup = findViewById(R.id.form_group);
        loginButton.setOnClickListener(v -> checkCredentials());

    }
    private void checkCredentials() {
        String email = emailInput.getText().toString().trim();
        String pass  = passwordInput.getText().toString();

        // basic validation
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email");
            return;
        }
        if (pass.isEmpty() || pass.length() < 6) {
            passwordInput.setError("Password must be ≥ 6 chars");
            return;
        }
        attemptLoginOrRegister(email,pass);
    }
    private void attemptLoginOrRegister(String email, String pass) {
        formGroup.setVisibility(View.GONE);
        loadingBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    //loadingBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        // ✅ Signed in — now check Firestore profile…
                        checkUserProfile();
                        return;
                    }

                    // 🚫 Sign-in failed — figure out why
                    Exception e = task.getException();
                    String code = "";
                    if (e instanceof FirebaseAuthException) {
                        code = ((FirebaseAuthException) e).getErrorCode();
                    }
                    Log.d("AuthError",
                            "Class: " + e.getClass().getSimpleName() +
                                    "   Code: " + code);

                    // 1) No such user → register
                    if (e instanceof FirebaseAuthInvalidUserException
                            || "ERROR_USER_NOT_FOUND".equals(code)
                            || "auth/user-not-found".equals(code)
                            // fallback when App Check still blocks you
                            || "ERROR_INVALID_CREDENTIAL".equals(code)
                            || "auth/invalid-credential".equals(code)
                    ) {
                        promptRegister(email, pass);

                        // 2) Bad password → show error
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException
                            || "ERROR_WRONG_PASSWORD".equals(code)
                            || "auth/wrong-password".equals(code)
                    ) {
                        showPasswordError();

                        // 3) Malformed email → show email error
                    } else if ("ERROR_INVALID_EMAIL".equals(code)
                            || "auth/invalid-email".equals(code)
                    ) {
                        emailInput.setError("That email address is malformed");
                        emailInput.requestFocus();

                        // 4) Anything else
                    } else {
                        Toast.makeText(this,
                                "Login error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkUserProfile() {
        String uid = mAuth.getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    loadingBar.setVisibility(View.GONE);
                    if (doc.exists()) {
                        onLoginSuccess(mAuth.getCurrentUser());
                    } else {
                        goToCompleteProfile();
                    }
                })
                .addOnFailureListener(err -> {
                    loadingBar.setVisibility(View.GONE);
                    Toast.makeText(this,
                            "DB read failed: " + err.getMessage(),
                            Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                    formGroup.setVisibility(View.VISIBLE);
                });
    }
    private void showPasswordError() {
        // Bring the form back into view
        formGroup.setVisibility(View.VISIBLE);

        // If you instead have a plain EditText:
        EditText passwordEditText = findViewById(R.id.password);
        passwordEditText.setError("Wrong password");
        passwordEditText.requestFocus();
    }

    private void goToCompleteProfile() {
        // Launch your “complete profile” screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        // If you don’t want the user to come back to the login screen:
        finish();
    }
    private void promptRegister(String email, String pass) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_register, null);
        EditText etUsername = dialogView.findViewById(R.id.etUsername);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("No account found")
                .setView(dialogView)  // <-- your custom layout here
                .setPositiveButton("Register", (d, which) -> {
                    String username = etUsername.getText().toString().trim();
                    doRegister(email,pass,username);

                })
                .setNegativeButton("Cancel", (d, which) -> formGroup.setVisibility(View.VISIBLE))
                .setCancelable(false)
                .create();
        // prevent taps outside the dialog from dismissing
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(d -> {
            DialogUtil.applyButtonColor(dialog);
        });

        dialog.show();
    }

    private void doRegister(String email, String pass,String username) {
        loadingBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    loadingBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Map<String,Object> data = new HashMap<>();
                        data.put("username", username);
                        db.collection("Username")
                                .document(mAuth.getCurrentUser().getUid())
                                .set(data)
                                .addOnSuccessListener(aVoid -> {
                                    onLoginSuccess(Objects.requireNonNull(mAuth.getCurrentUser()));
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this,
                                            "Save failed: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                    } else {
                        formGroup.setVisibility(View.VISIBLE);
                        Toast.makeText(this,
                                "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                });
    }
    private void onLoginSuccess(FirebaseUser user) {
        Toast.makeText(this,
                "Welcome, " + (user.getDisplayName() != null
                        ? user.getDisplayName()
                        : user.getEmail()),
                Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
