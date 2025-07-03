package com.example.disciteomneslearningplatform.ui.groupManagement;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;

import com.example.disciteomneslearningplatform.data.model.AuthRepository;

import API.ApiClient;
import API.ApiService;

public class GroupsViewModel extends AndroidViewModel {
    private final AuthRepository repo;


    public GroupsViewModel(@NonNull Application app) {
        super(app);
        ApiService api = ApiClient.getApiClient().create(ApiService.class);
        repo = AuthRepository.getInstance(api, app);



    }
}