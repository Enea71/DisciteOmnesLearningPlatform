// GroupDetailViewModel.java
package com.example.disciteomneslearningplatform.ui.groupDetail;

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
import API.Task;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupDetailViewModel extends AndroidViewModel {
    private GroupRepository repo;
    private final MutableLiveData<List<Task>> tasks = new MutableLiveData<>();
    public LiveData<List<Task>> tasks() { return tasks; }
    private final MutableLiveData<String> groupId = new MutableLiveData<>();

    /** Exposed LiveData that fetches whenever groupId changes. */
    private final MutableLiveData<Group> _group = new MutableLiveData<>();
    public LiveData<Group> group() { return _group; }
    ApiService api;
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error() { return _error; }

    public GroupDetailViewModel(@NonNull Application app) {
        super(app);
        // replicate what your GroupsViewModel does:
        api = ApiClient.getApiClient().create(ApiService.class);
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
    public void loadTasks(String bearer, String groupId) {
        api.getTasksForGroup(bearer, groupId)
                .enqueue(new Callback<List<Task>>() {
                    @Override public void onResponse(Call<List<Task>> call, Response<List<Task>> res) {
                        if (res.isSuccessful()) tasks.postValue(res.body());
                    }
                    @Override public void onFailure(Call<List<Task>> call, Throwable t) {
                        // optionally post an error
                    }
                });
    }

    public void createTask(String bearer, String groupId, Task newTask) {
        api.createTask(bearer, groupId, newTask)
                .enqueue(new Callback<Task>() {
                    @Override public void onResponse(Call<Task> call, Response<Task> res) {
                        if (res.isSuccessful()) {
                            // reload to include the newly created task
                            loadTasks(bearer, groupId);
                        }
                    }
                    @Override public void onFailure(Call<Task> call, Throwable t) {
                        // handle failure
                    }
                });
    }
}
