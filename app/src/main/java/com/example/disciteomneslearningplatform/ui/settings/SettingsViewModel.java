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
        repo = new AuthRepository(api, app);
    }

    public void changePassword(String newPw, AuthRepository.ResultCallback<Void> cb) {
        String bearer = "Bearer " + repo.getIdToken();
        String uid    = repo.getUid();
        repo.changePassword(bearer, uid, new UserAPI.ChangePasswordRequest(newPw), cb);
    }
}
