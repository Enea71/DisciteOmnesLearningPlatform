package com.example.disciteomneslearningplatform.data.model;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import API.ApiService;
import API.UserAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private static final String PREFS_NAME  = "auth_prefs";
    private static final String KEY_TOKEN   = "key_token";
    private static final String KEY_UID     = "key_uid";
    private static final String KEY_EMAIL   = "key_email";
    private static final String KEY_USERNAME= "key_username";
    private final ApiService api;

    private final SharedPreferences prefs;

    private String idToken, uid, email, username;


    public AuthRepository(ApiService api, Context ctx) {
        this.prefs = ctx.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.api   = api;
        // On startup, load whatever was persisted:
        this.idToken  = prefs.getString(KEY_TOKEN,   null);
        this.uid      = prefs.getString(KEY_UID,     null);
        this.email    = prefs.getString(KEY_EMAIL,   null);
        this.username = prefs.getString(KEY_USERNAME,null);
    }
    private void saveAuth(String token, String uid, String email, String username) {
        this.idToken  = token;
        this.uid      = uid;
        this.email    = email;
        this.username = username;
        prefs.edit()
                .putString(KEY_TOKEN,    token)
                .putString(KEY_UID,      uid)
                .putString(KEY_EMAIL,    email)
                .putString(KEY_USERNAME, username)
                .apply();
    }
    private void onLoginOrRegisterSuccess(UserAPI.LoginResponse resp) {
        saveAuth(
                resp.getIdToken(),
                resp.getUid(),
                resp.getEmail(),
                resp.getUsername()
        );
    }


    public interface ResultCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }
    public void logout() {
        // 1) Clear SharedPreferences (removes token, uid, email, username)
        prefs.edit().clear().apply();
        // 2) Clear in-memory values
        idToken   = null;
        uid       = null;
        email     = null;
        username  = null;
    }
    public void login(String email, String pass, ResultCallback<UserAPI.LoginResponse> cb) {
        api.login(new UserAPI.LoginRequest(email, pass))
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<UserAPI.LoginResponse> c, Response<UserAPI.LoginResponse> r) {
                        if (r.isSuccessful() && r.body()!=null) {
                            onLoginOrRegisterSuccess(r.body());
                            cb.onSuccess(r.body());
                        } else if (r.code()==401) {
                            cb.onError("Invalid credentials");
                        } else {
                            cb.onError("Login failed: "+r.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<UserAPI.LoginResponse> c, Throwable t) {
                        cb.onError("Network error: "+t.getMessage());
                    }
                });
    }
    public void register(String email, String pass, String username, ResultCallback<UserAPI.RegisterResponse> cb) {
        api.register(new UserAPI.RegisterRequest(email, pass, username))
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<UserAPI.RegisterResponse> c, Response<UserAPI.RegisterResponse> r) {
                        if (r.isSuccessful() && r.body()!=null) {
                            UserAPI.LoginResponse tempLoginResponse =
                                    new UserAPI.LoginResponse(
                                            r.body().getIdToken(),
                                            r.body().getUid(),
                                            r.body().getUsername(),
                                            r.body().getEmail());
                            onLoginOrRegisterSuccess(tempLoginResponse);
                            cb.onSuccess(r.body());
                        } else {
                            cb.onError("Email Already has an Account, Error: "+r.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<UserAPI.RegisterResponse> c, Throwable t) {
                        cb.onError("Network error: "+t.getMessage());
                    }
                });
    }
    public void changePassword(String bearer, String uid, UserAPI.ChangePasswordRequest req, ResultCallback cb) {
        api.changePassword(bearer, uid, req)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> resp) {
                        if (resp.isSuccessful()) {
                            cb.onSuccess(resp.body());
                        } else {
                            cb.onError("Failed: " + resp.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        cb.onError("Network error: " + t.getMessage());
                    }
                });
    }

    public String getIdToken()  { return idToken;  }
    public String getUid()      { return uid;      }
    public String getEmail()    { return email;    }
    public String getUsername() { return username; }
}
