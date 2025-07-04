package com.example.disciteomneslearningplatform.ui.home;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disciteomneslearningplatform.data.model.AuthRepository;
import com.example.disciteomneslearningplatform.data.model.GroupRepository;
import com.example.disciteomneslearningplatform.databinding.FragmentHomeBinding;
import com.example.disciteomneslearningplatform.ui.groupManagement.GroupAdapter;

import API.ApiClient;
import API.ApiService;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private AuthRepository repo;
    private GroupAdapter adapter;
    private HomeViewModel homeViewModel;
    private GroupRepository groupRepo;
    ApiService api;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        adapter = new GroupAdapter(group -> {
            /* click */
        });

        RecyclerView rv = binding.rvGroups;
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rv.setAdapter(adapter);

        api = ApiClient.getApiClient().create(ApiService.class);
        repo = AuthRepository.getInstance(api, this.requireContext());
        groupRepo = new GroupRepository(repo);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel.memberGroups.observe(getViewLifecycleOwner(), groups -> {
            Log.d("Observer", "Observed new group size " + groups.size());
            adapter.setGroups(groups);
        });
        homeViewModel.refreshMembers();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}