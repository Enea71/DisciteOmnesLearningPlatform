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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disciteomneslearningplatform.R;
import com.example.disciteomneslearningplatform.data.model.AuthRepository;
import com.example.disciteomneslearningplatform.data.model.GroupRepository;
import com.example.disciteomneslearningplatform.databinding.FragmentGroupsBinding;

import API.ApiClient;
import API.ApiService;


public class GroupsFragment extends Fragment {
    private FragmentGroupsBinding binding;
    private AuthRepository repo;
    private GroupAdapter adapter;
    private GroupsViewModel groupsViewModel;
    private GroupRepository groupRepo;
    ApiService api;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGroupsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        groupsViewModel = new ViewModelProvider(this).get(GroupsViewModel.class);

        adapter = new GroupAdapter(R.layout.item_group_owner, group -> {
            //click
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

        groupsViewModel.ownerGroups.observe(getViewLifecycleOwner(), groups -> {
            Log.d("Observer", "Observed new group size " + groups.size());
            adapter.setGroups(groups);
        });
        groupsViewModel.refreshOwner();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}