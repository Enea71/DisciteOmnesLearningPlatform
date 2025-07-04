package com.example.disciteomneslearningplatform.ui.home;


import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.example.disciteomneslearningplatform.data.model.AuthRepository;
import com.example.disciteomneslearningplatform.data.model.GroupRepository;


import java.util.List;

import API.ApiClient;
import API.ApiService;
import API.GroupAPI;

public class HomeViewModel extends AndroidViewModel {
    private final AuthRepository repo;
    private final GroupRepository groupRepo;
    public final LiveData<List<GroupAPI.GroupResponse>> memberGroups;

    //force refresh on demand
    public void refreshMembers()  { groupRepo.fetchMemberGroups();  }
    public HomeViewModel(@NonNull Application app) {
        super(app);
        ApiService api = ApiClient.getApiClient().create(ApiService.class);
        repo = AuthRepository.getInstance(api, app);
        groupRepo = new GroupRepository(repo);
        memberGroups  = groupRepo.memberGroups();
        groupRepo.fetchMemberGroups();
    }
}