// LoginActivity.java
package com.example.disciteomneslearningplatform;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.example.disciteomneslearningplatform.ui.DialogUtil;

import java.io.IOException;

import API.ApiClient;
import API.ApiService;
import API.UserAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginButton;
    private ProgressBar loadingBar;
    private Group formGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Create this layout

        //Get all views by id to change them, get text and change visibility
        emailInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingBar = findViewById(R.id.loading);
        formGroup = findViewById(R.id.form_group);
        loginButton.setOnClickListener(v -> checkCredentials());
        TextView textView = findViewById(R.id.tv);
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
        ApiService api = ApiClient
                .getApiClient()
                .create(ApiService.class);
        UserAPI.LoginRequest loginReq = new UserAPI.LoginRequest(email, pass);
        api.login(loginReq).enqueue(new Callback<UserAPI.LoginResponse>() {
            @Override
            public void onResponse(Call<UserAPI.LoginResponse> call,
                                   Response<UserAPI.LoginResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    // 2) Login succeeded: get token & uid
                    String token = resp.body().getIdToken();
                    String uid   = resp.body().getUid();
                    String bearer = "Bearer " + token;
                    onLoginSuccess();
                    // 3) Fetch their profile
                }
                else if (resp.code() == 401) {
                    // 4) No account / bad password → prompt registration
                    loadingBar.setVisibility(View.GONE);
                    //promptRegister(email, pass);
                }
                else {
                    // other server error
                    loadingBar.setVisibility(View.GONE);
                    formGroup.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this,
                            "Login error: " + resp.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserAPI.LoginResponse> call, Throwable t) {
                // network or parsing failure
                loadingBar.setVisibility(View.GONE);
                formGroup.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

/*
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
                    //doRegister(email,pass,username);

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
/*
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
    }*/
    private void onLoginSuccess() {
//        Toast.makeText(this,
//                "Welcome, " + (user.getDisplayName() != null
//                        ? user.getDisplayName()
//                        : user.getEmail()),
//                Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
