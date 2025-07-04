// GroupDetailViewModel.java
package com.example.disciteomneslearningplatform.ui.groupDetail;

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
    private GroupRepository repo;
    private final MutableLiveData<String> groupId = new MutableLiveData<>();

    /** Exposed LiveData that fetches whenever groupId changes. */
    private final MutableLiveData<Group> _group = new MutableLiveData<>();
    public LiveData<Group> group() { return _group; }

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error() { return _error; }

    public GroupDetailViewModel(@NonNull Application app) {
        super(app);
        // replicate what your GroupsViewModel does:
        ApiService api = ApiClient.getApiClient().create(ApiService.class);
        AuthRepository auth = AuthRepository.getInstance(api, app);
        this.repo = new GroupRepository(auth);
    }
    public void loadGroup(String bearer, String groupId) {
        repo.getGroupById(bearer, groupId, new GroupRepository.ResultCallback<Group>() {
            @Override
            public void onSuccess(Group data) {
                _group.postValue(data);
            }
            @Override
            public void onError(String message) {
                _error.postValue(message);
            }
        });
    }

    public void setGroupId(String gid) {
        if (gid != null && !gid.equals(groupId.getValue())) {
            groupId.setValue(gid);
        }
    }
}
