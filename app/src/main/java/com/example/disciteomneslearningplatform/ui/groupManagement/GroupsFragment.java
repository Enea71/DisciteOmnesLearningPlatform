package com.example.disciteomneslearningplatform.ui.groupManagement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disciteomneslearningplatform.data.model.AuthRepository;
import com.example.disciteomneslearningplatform.data.model.GroupRepository;
import com.example.disciteomneslearningplatform.databinding.FragmentGroupsBinding;
import com.example.disciteomneslearningplatform.ui.settings.SettingsViewModel;

import java.util.ArrayList;

import API.ApiClient;
import API.ApiService;
import API.GroupAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupsFragment extends Fragment {
    private FragmentGroupsBinding binding;
    private AuthRepository repo;
    private GroupRepository groupRepo;
    private GroupAdapter adapter;
    ApiService api;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGroupsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new GroupAdapter(group -> {
            // handle click
        });
        RecyclerView rv = binding.rvGroups;
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rv.setAdapter(adapter);

        api = ApiClient.getApiClient().create(ApiService.class);
        repo = AuthRepository.getInstance(api, this.requireContext());
        groupRepo =new GroupRepository(repo);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("GroupsFragment", "✅ onViewCreated – fragment is visible!");
        Toast.makeText(getContext(), "GroupsFragment loaded", Toast.LENGTH_SHORT).show();
        loadGroups();
    }


    private void loadGroups() {
        groupRepo.getMemberGroups(new GroupRepository.ResultCallback<GroupAPI.GroupsResponse>() {
            @Override
            public void onSuccess(GroupAPI.GroupsResponse data) {
                Log.d("GroupsFetch", "Fetched " + data.groups.size() + " groups");
                adapter.setGroups(data.groups);
            }
            @Override
            public void onError(String message) {
                Log.d("ERR", "Error" + message);
                Toast.makeText(getContext(),
                        "Failed to load groups: " + message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
