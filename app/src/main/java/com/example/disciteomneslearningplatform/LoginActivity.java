// LoginActivity.java
package com.example.disciteomneslearningplatform;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.example.disciteomneslearningplatform.data.model.AuthRepository;
import com.example.disciteomneslearningplatform.ui.AlertDialogButtons;

import API.ApiClient;
import API.ApiService;
import API.UserAPI;


public class LoginActivity extends AppCompatActivity {
        private EditText emailInput, passwordInput;
        private Button loginButton;
        private ProgressBar loadingBar;
        private Group formGroup;
        private AuthRepository repo;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            // UI references from XML
            emailInput  = findViewById(R.id.username);
            passwordInput = findViewById(R.id.password);
            loginButton = findViewById(R.id.login);
            loadingBar  = findViewById(R.id.loading);
            formGroup   = findViewById(R.id.form_group);

            // REPO initialisation
            ApiService api = ApiClient.getApiClient().create(ApiService.class);
            repo = new AuthRepository(api, LoginActivity.this);

            // Button Functionality
            loginButton.setOnClickListener(v -> checkCredentials());
        }

        private void checkCredentials() {
            String email = emailInput.getText().toString().trim();
            String pass  = passwordInput.getText().toString();

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Enter a valid email");
                return;
            }
            if (pass.isEmpty() || pass.length() < 6) {
                passwordInput.setError("Password must be ≥ 6 chars");
                return;
            }
            attemptLoginOrRegister(email, pass);
        }

        private void attemptLoginOrRegister(String email, String pass) {
            formGroup.setVisibility(View.GONE);
            loadingBar.setVisibility(View.VISIBLE);

            repo.login(email, pass, new AuthRepository.ResultCallback<UserAPI.LoginResponse>() {
                @Override
                public void onSuccess(UserAPI.LoginResponse resp) {
                    // hide loading, proceed to next screen
                    loadingBar.setVisibility(View.GONE);
                    onLoginSuccess(resp.getUsername());
                }

                @Override
                public void onError(String msg) {
                    loadingBar.setVisibility(View.GONE);
                    if (msg.contains("Invalid credentials")) {
                        promptRegister(email, pass);
                    } else {
                        formGroup.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        private void promptRegister(String email, String pass) {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_register, null);
            EditText etUsername = dialogView.findViewById(R.id.etUsername);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("No account found")
                    .setView(dialogView)
                    .setPositiveButton("Register", (d, w) -> {
                        String username = etUsername.getText().toString().trim();
                        doRegister(email, pass, username);
                    })
                    .setNegativeButton("Cancel", (d, w) -> formGroup.setVisibility(View.VISIBLE))
                    .setCancelable(false)
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            AlertDialogButtons.formatButtons(this,dialog, R.attr.alert_options_color);
        }

        private void doRegister(String email, String pass, String username) {
            loadingBar.setVisibility(View.VISIBLE);
            repo.register(email, pass, username, new AuthRepository.ResultCallback<>() {
                @Override
                public void onSuccess(UserAPI.RegisterResponse resp) {
                    onLoginSuccess(username);
                }
                @Override
                public void onError(String msg) {
                    loadingBar.setVisibility(View.GONE);
                    formGroup.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            });
        }

        private void onLoginSuccess(String username) {
            Toast.makeText(this, "Welcome, " + username, Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
}


