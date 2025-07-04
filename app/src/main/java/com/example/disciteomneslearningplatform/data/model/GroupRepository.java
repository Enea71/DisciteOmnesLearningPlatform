package com.example.disciteomneslearningplatform.data.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import API.ApiClient;
import API.ApiService;
import API.Group;
import API.GroupAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupRepository {
    private final ApiService api = ApiClient
            .getApiClient()
            .create(ApiService.class);
    private final AuthRepository auth;

    public GroupRepository(AuthRepository auth) {
        this.auth = auth;
    }
    // In-memory caches
    private final List<GroupAPI.GroupResponse> memberCache = new ArrayList<>();
    private final List<GroupAPI.GroupResponse> ownerCache  = new ArrayList<>();
    // LiveData UI can observe
    private final MutableLiveData<List<GroupAPI.GroupResponse>> memberGroupsLive = new MutableLiveData<>();
    private final MutableLiveData<List<GroupAPI.GroupResponse>> ownerGroupsLive  = new MutableLiveData<>();

    public LiveData<List<GroupAPI.GroupResponse>> memberGroups() {
        return memberGroupsLive;
    }
    public LiveData<List<GroupAPI.GroupResponse>> ownerGroups() {
        return ownerGroupsLive;
    }
    public void fetchMemberGroups() {
        String token = "Bearer " + auth.getIdToken();
        api.getMemberGroups(token).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<GroupAPI.GroupsResponse> c, Response<GroupAPI.GroupsResponse> r) {
                if (r.isSuccessful() && r.body()!=null) {
                    memberCache.clear();
                    memberCache.addAll(r.body().groups);
                    memberGroupsLive.setValue(new ArrayList<>(memberCache));
                    Log.d("memberCache", "Pushed new membercache");
                }
            }
            @Override
            public void onFailure(Call<GroupAPI.GroupsResponse> c, Throwable t) {
                Log.d("memberCache", "Pushing cache failed: " + t);
            }
        });
    }
    public void fetchOwnerGroups() {
        String token = "Bearer " + auth.getIdToken();
        api.getOwnerGroups(token).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<GroupAPI.GroupsResponse> c, Response<GroupAPI.GroupsResponse> r) {
                if (r.isSuccessful() && r.body()!=null) {
                    ownerCache.clear();
                    ownerCache.addAll(r.body().groups);
                    ownerGroupsLive.setValue(new ArrayList<>(ownerCache));
                    Log.d("ownerCache", "Pushed new ownercache");
                }
            }
            @Override
            public void onFailure(Call<GroupAPI.GroupsResponse> c, Throwable t) {
                Log.d("ownerCache", "Pushing cache failed: " + t);
            }
        });
    }

    public interface ResultCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    public void createGroup(String name, String description, List<String> members, ResultCallback<GroupAPI.GroupResponse> cb) {
        String token = "Bearer " + auth.getIdToken();

        api.createGroup(token, new GroupAPI.CreateGroupRequest(name, description, members))
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<GroupAPI.GroupResponse> call,
                                           Response<GroupAPI.GroupResponse> res) {
                        if (res.isSuccessful() && res.body() != null) {
                            cb.onSuccess(res.body());
                        } else {
                            cb.onError("Create failed: HTTP " + res.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<GroupAPI.GroupResponse> call, Throwable t) {
                        cb.onError("Network error: " + t.getMessage());
                    }
                });
    }
    public void deleteGroup(String bearer, String gid, AuthRepository.ResultCallback cb){
        api.deleteGroup(bearer,gid).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> resp) {
                if (resp.isSuccessful()) {
                    cb.onSuccess(null);  // no data to pass
                } else {
                    cb.onError("Failed to delete: HTTP " + resp.code());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                cb.onError("Network error: " + t.getMessage());
            }
        });
    }
    public void getGroupById(String bearer, String groupId, ResultCallback<Group> callback) {
        api.getGroup(bearer, groupId).enqueue(new retrofit2.Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    callback.onSuccess(resp.body());
                } else {
                    callback.onError("HTTP " + resp.code() + ": " + resp.message());
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
