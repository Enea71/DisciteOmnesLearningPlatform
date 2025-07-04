package com.example.disciteomneslearningplatform.ui.groupManagement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disciteomneslearningplatform.R;
import com.example.disciteomneslearningplatform.data.model.AuthRepository;
import com.example.disciteomneslearningplatform.data.model.GroupAdapter;
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

        api = ApiClient.getApiClient().create(ApiService.class);
        repo = AuthRepository.getInstance(api, this.requireContext());
        groupRepo =new GroupRepository(repo);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController nav = Navigation.findNavController(view);

        adapter = new GroupAdapter(R.layout.item_group_owner, group -> {
            Bundle args = new Bundle();
            args.putString("groupId", group.id);
            nav.navigate(R.id.groupDetailFragment, args);
        },group ->{
            String bearer = "Bearer " + repo.getIdToken();
            groupRepo.deleteGroup(bearer, group.id, new AuthRepository.ResultCallback<Void>() {
                @Override public void onSuccess(Void unused) {
                    Toast.makeText(getContext(),
                            "Deleted “" + group.name + "”", Toast.LENGTH_SHORT).show();
                    // remove from adapter:
                    adapter.removeGroup(group);
                }
                @Override public void onError(String msg) {
                    Toast.makeText(getContext(),
                            "Delete failed: " + msg, Toast.LENGTH_LONG).show();
                }
            });
        });

        RecyclerView rv = binding.rvGroups;
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

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