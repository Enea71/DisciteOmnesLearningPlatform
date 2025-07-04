package com.example.disciteomneslearningplatform.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disciteomneslearningplatform.R;
import com.example.disciteomneslearningplatform.data.model.AuthRepository;
import com.example.disciteomneslearningplatform.data.model.GroupRepository;
import com.example.disciteomneslearningplatform.databinding.FragmentHomeBinding;
import com.example.disciteomneslearningplatform.data.model.GroupAdapter;

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
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        api = ApiClient.getApiClient().create(ApiService.class);
        repo = AuthRepository.getInstance(api, this.requireContext());
        groupRepo = new GroupRepository(repo);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController nav = Navigation.findNavController(view);

        adapter = new GroupAdapter(R.layout.item_group_home, group -> {
            Bundle args = new Bundle();
            args.putString("groupId", group.id);
            nav.navigate(R.id.groupDetailFragment, args);
        });

        RecyclerView rv = binding.rvGroups;
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rv.setAdapter(adapter);
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