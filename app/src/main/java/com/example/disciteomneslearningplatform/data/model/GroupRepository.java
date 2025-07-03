package com.example.disciteomneslearningplatform.data.model;

import java.util.Collections;
import java.util.List;

import API.ApiClient;
import API.ApiService;
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

    public interface ResultCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    /**
     * Creates a new group with the current user as its only initial member.
     * Calls back with the newly-created GroupResponse.
     */
    public void createGroup(String name, String description, ResultCallback<GroupAPI.GroupResponse> cb) {
        String token = "Bearer " + auth.getIdToken();
        List<String> members = Collections.singletonList(auth.getUid());

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
    public void getMemberGroups(ResultCallback<GroupAPI.GroupsResponse> cb) {
        String token = "Bearer " + auth.getIdToken();
        api.getMemberGroups(token).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<GroupAPI.GroupsResponse> call,
                                   Response<GroupAPI.GroupsResponse> res) {
                if (res.isSuccessful() && res.body() != null) {
                    cb.onSuccess(res.body());
                } else {
                    cb.onError("Fetch failed: HTTP " + res.code());
                }
            }
            @Override
            public void onFailure(Call<GroupAPI.GroupsResponse> call, Throwable t) {
                cb.onError("Network error: " + t.getMessage());
            }
        });
    }
    public void getOwnerGroups(ResultCallback<GroupAPI.GroupsResponse> cb) {
        String token = "Bearer " + auth.getIdToken();
        api.getOwnerGroups(token)
                .enqueue(new Callback<>() {
                    @Override public void onResponse(Call<GroupAPI.GroupsResponse> c, Response<GroupAPI.GroupsResponse> r) {
                        if (r.isSuccessful() && r.body()!=null) cb.onSuccess(r.body());
                        else cb.onError("HTTP "+r.code());
                    }
                    @Override public void onFailure(Call<GroupAPI.GroupsResponse> c, Throwable t) {
                        cb.onError(t.getMessage());
                    }
                });
    }
}
