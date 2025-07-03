package com.example.disciteomneslearningplatform.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.disciteomneslearningplatform.data.model.AuthRepository;

import API.ApiClient;
import API.ApiService;
import API.UserAPI;

public class SettingsViewModel extends AndroidViewModel {
    private final AuthRepository repo;

    public SettingsViewModel(@NonNull Application app) {
        super(app);
        ApiService api = ApiClient.getApiClient().create(ApiService.class);
        repo = AuthRepository.getInstance(api, app);
    }

    public void changePassword(String newPw, AuthRepository.ResultCallback<Void> cb) {
        String bearer = "Bearer " + repo.getIdToken();
        String uid    = repo.getUid();
        repo.changePassword(bearer, uid, new UserAPI.ChangePasswordRequest(newPw), cb);
    }
    public void changeUsername(String newUn, AuthRepository.ResultCallback<Void> cb) {
        String bearer = "Bearer " + repo.getIdToken();
        String uid    = repo.getUid();
        repo.changeUsername(bearer, uid, new UserAPI.ChangeUsernameRequest(newUn), cb);
    }
    public void deleteUser(AuthRepository.ResultCallback<Void> cb) {
        String bearer = "Bearer " + repo.getIdToken();
        String uid    = repo.getUid();
        repo.deleteUser(bearer, uid, cb);
    }
}
