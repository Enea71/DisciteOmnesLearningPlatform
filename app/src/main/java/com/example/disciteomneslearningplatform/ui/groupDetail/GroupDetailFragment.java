package com.example.disciteomneslearningplatform.ui.groupDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.disciteomneslearningplatform.data.model.AuthRepository;
//import com.example.disciteomneslearningplatform.data.model.NameAdapter;
import com.example.disciteomneslearningplatform.databinding.FragmentGroupDetailBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import API.ApiClient;
import API.ApiService;
import API.Group;

public class GroupDetailFragment extends Fragment {

    private FragmentGroupDetailBinding binding;
    private GroupDetailViewModel vm;
    private AuthRepository authRepo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentGroupDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
/*
        // RecyclerView + adapter
        NameAdapter adapter = new NameAdapter();
        binding.rvMembers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvMembers.setAdapter(adapter);
*/
        // Prepare AuthRepository (to get the bearer token)
        ApiService api = ApiClient.getApiClient().create(ApiService.class);
        authRepo = AuthRepository.getInstance(api, requireContext());

        // Obtain AndroidViewModel
        vm = new ViewModelProvider(this)
                .get(GroupDetailViewModel.class);

        // Observe LiveData from ViewModel
        vm.group().observe(getViewLifecycleOwner(), group -> {
            // Populate static fields
            String groupName = "Group Name: " + group.name;
            String groupDescription ="Description:\n " + group.description;

            binding.tvGroupName.setText(groupName);
            binding.tvGroupDescription.setText(groupDescription);

            // Get all ids
            List<String> ids = group.membersID;
            if (ids == null || ids.isEmpty()) {
                //adapter.setItems(Collections.emptyList());
                return;
            }
            List<String> names = new ArrayList<>(Collections.nCopies(ids.size(), null));
            AtomicInteger remaining = new AtomicInteger(ids.size());

           // adapter.setItems(group.membersID);
        });
        vm.error().observe(getViewLifecycleOwner(), errMsg -> {
            // Error path
            Toast.makeText(requireContext(),
                            "Failed to load group: " + errMsg,
                            Toast.LENGTH_LONG)
                    .show();
        });

        // Kick off load with the UID from bundle
        String groupId = requireArguments().getString("groupId");
        String bearer = "Bearer " + authRepo.getIdToken();
        vm.loadGroup(bearer, groupId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}