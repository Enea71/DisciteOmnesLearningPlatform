package com.example.disciteomneslearningplatform.ui.groupDetailEdit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.disciteomneslearningplatform.data.model.AuthRepository;
import com.example.disciteomneslearningplatform.data.model.GroupRepository;

import java.util.List;

import API.ApiClient;
import API.ApiService;
import API.Group;

public class GroupDetailViewModel extends AndroidViewModel {
    private final GroupRepository groupRepo;
    private final MutableLiveData<Group> _group = new MutableLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _updateResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _memberUpdateResult = new MutableLiveData<>();
    public LiveData<Boolean> memberUpdateResult() { return _memberUpdateResult; }


    public GroupDetailViewModel(@NonNull Application app) {
        super(app);
        ApiService api = ApiClient.getApiClient().create(ApiService.class);
        AuthRepository repo = AuthRepository.getInstance(api, app);
        groupRepo = new GroupRepository(repo);
    }

    public LiveData<Group> group() { return _group; }
    public LiveData<String> error() { return _error; }
    public LiveData<Boolean> updateResult() { return _updateResult; }

    /** Load full group */
    public void loadGroup(String bearer, String groupId) {
        groupRepo.getGroupById(bearer, groupId, new GroupRepository.ResultCallback<Group>() {
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
        current.name = newTitle;
        groupRepo.updateGroup(bearer, current, new GroupRepository.ResultCallback<Group>() {
            @Override public void onSuccess(Group data) {
                _group.postValue(data);
                _updateResult.postValue(true);
            }
            @Override public void onError(String msg) {
                _error.postValue(msg);
                _updateResult.postValue(false);
            }
        });
    }

    /** Update only the description */
    public void updateGroupDescription(String bearer, String newDescription) {
        Group current = _group.getValue();
        if (current == null) return;
        current.description = newDescription;
        groupRepo.updateGroup(bearer, current, new GroupRepository.ResultCallback<Group>() {
            @Override public void onSuccess(Group data) {
                _group.postValue(data);
                _updateResult.postValue(true);
            }
            @Override public void onError(String msg) {
                _error.postValue(msg);
                _updateResult.postValue(false);
            }
        });
    }
    public void updateGroupMembers(
            String bearer,
            String groupId,
            List<String> newMembers
    ) {
        Group g = _group.getValue();
        if (g == null) return;
        g.members = newMembers;
        groupRepo.updateGroup(bearer, g, new GroupRepository.ResultCallback<Group>() {
            @Override
            public void onSuccess(Group data) {
                // push the new group (with updated members)
                _group.postValue(data);
                _memberUpdateResult.postValue(true);
            }
            @Override
            public void onError(String msg) {
                _error.postValue(msg);
                _memberUpdateResult.postValue(false);
            }
        });
    }
}
