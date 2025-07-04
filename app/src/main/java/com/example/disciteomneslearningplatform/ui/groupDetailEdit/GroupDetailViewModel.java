package com.example.disciteomneslearningplatform.ui.groupDetailEdit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.disciteomneslearningplatform.data.model.AuthRepository;
import com.example.disciteomneslearningplatform.data.model.GroupRepository;

import API.ApiClient;
import API.ApiService;
import API.Group;

public class GroupDetailViewModel extends AndroidViewModel {
    private final GroupRepository repo;
    private final MutableLiveData<Group> _group = new MutableLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _updateResult = new MutableLiveData<>();

    public GroupDetailViewModel(@NonNull Application app) {
        super(app);
        ApiService api = ApiClient.getApiClient().create(ApiService.class);
        AuthRepository auth = AuthRepository.getInstance(api, app);
        repo = new GroupRepository(auth);
    }

    public LiveData<Group> group() { return _group; }
    public LiveData<String> error() { return _error; }
    public LiveData<Boolean> updateResult() { return _updateResult; }

    /** Load full group */
    public void loadGroup(String bearer, String groupId) {
        repo.getGroupById(bearer, groupId, new GroupRepository.ResultCallback<Group>() {
            @Override public void onSuccess(Group data) {
                _group.postValue(data);
            }
            @Override public void onError(String message) {
                _error.postValue(message);
            }
        });
    }

    /** Update only the title */
    public void updateGroupTitle(String bearer, String newTitle) {
        Group current = _group.getValue();
        if (current == null) return;
        current.name = newTitle;/*
        repo.updateGroup(bearer, current, new GroupRepository.ResultCallback<Group>() {
            @Override public void onSuccess(Group data) {
                _group.postValue(data);
                _updateResult.postValue(true);
            }
            @Override public void onError(String msg) {
                _error.postValue(msg);
                _updateResult.postValue(false);
            }
        });*/
    }

    /** Update only the description */
    public void updateGroupDescription(String bearer, String newDescription) {
        Group current = _group.getValue();
        if (current == null) return;
        current.description = newDescription;/*
        repo.updateGroup(bearer, current, new GroupRepository.ResultCallback<Group>() {
            @Override public void onSuccess(Group data) {
                _group.postValue(data);
                _updateResult.postValue(true);
            }
            @Override public void onError(String msg) {
                _error.postValue(msg);
                _updateResult.postValue(false);
            }
        });*/
    }
}
