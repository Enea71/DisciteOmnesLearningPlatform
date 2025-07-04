package com.example.disciteomneslearningplatform.ui.groupManagement;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.example.disciteomneslearningplatform.data.model.AuthRepository;
import com.example.disciteomneslearningplatform.data.model.GroupRepository;

import java.util.List;

import API.ApiClient;
import API.ApiService;
import API.GroupAPI;

public class GroupsViewModel extends AndroidViewModel {
    private final AuthRepository repo;
    private final GroupRepository groupRepo;
    public final LiveData<List<GroupAPI.GroupResponse>> ownerGroups;

    //force refresh on demand
    public void refreshOwner()  { groupRepo.fetchOwnerGroups();  }
    public GroupsViewModel(@NonNull Application app) {
        super(app);
        ApiService api = ApiClient.getApiClient().create(ApiService.class);
        repo = AuthRepository.getInstance(api, app);
        groupRepo = new GroupRepository(repo);
        ownerGroups  = groupRepo.ownerGroups();
        groupRepo.fetchOwnerGroups();
    }
}