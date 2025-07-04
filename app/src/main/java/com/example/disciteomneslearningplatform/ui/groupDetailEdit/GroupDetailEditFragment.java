package com.example.disciteomneslearningplatform.ui.groupDetailEdit;

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
import com.example.disciteomneslearningplatform.data.model.NameAdapter;
import com.example.disciteomneslearningplatform.databinding.FragmentGroupEditBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import API.ApiClient;
import API.ApiService;

public class GroupDetailEditFragment extends Fragment {

    private FragmentGroupEditBinding binding;
    private GroupDetailViewModel vm;
    private String bearer;
    private String groupId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentGroupEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NameAdapter adapter = new NameAdapter();
        binding.rvMembers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvMembers.setAdapter(adapter);

        // Prepare AuthRepository & bearer token
        ApiService api = ApiClient.getApiClient().create(ApiService.class);
        AuthRepository repo = AuthRepository.getInstance(api, requireContext());
        bearer = "Bearer " + repo.getIdToken();

        // ViewModel
        vm = new ViewModelProvider(this).get(GroupDetailViewModel.class);

        // Read groupId from args
        groupId = requireArguments().getString("groupId");
        vm.loadGroup(bearer, groupId);

        // Populate fields when data arrives
        vm.group().observe(getViewLifecycleOwner(), group -> {
            binding.newGroupTitle.setText(group.name);
            binding.newGroupDescription.setText(group.description);
            loadMemberNames(group.members, adapter, repo);

        });

        // Listen for update results
        vm.updateResult().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Group updated successfully", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
        vm.error().observe(getViewLifecycleOwner(), err ->
                Toast.makeText(requireContext(), "Error: " + err, Toast.LENGTH_LONG).show()
        );

        // Save title
        binding.btnSaveTitle.setOnClickListener(v -> {
            String newTitle = binding.newGroupTitle.getText().toString().trim();
            vm.updateGroupTitle(bearer, newTitle);
        });

        // Save description
        binding.btnSaveDescription.setOnClickListener(v -> {
            String newDesc = binding.newGroupDescription.getText().toString().trim();
            vm.updateGroupDescription(bearer, newDesc);
        });
    }
    private void loadMemberNames(
            List<String> memberIds,
            NameAdapter adapter,
            AuthRepository repo
    ) {
        if (memberIds == null || memberIds.isEmpty()) {
            adapter.setItems(Collections.emptyList());
            return;
        }

        List<String> names = new ArrayList<>(Collections.nCopies(memberIds.size(), null));
        AtomicInteger remaining = new AtomicInteger(memberIds.size());

        for (int i = 0; i < memberIds.size(); i++) {
            final int idx = i;
            String uid = memberIds.get(i);

            repo.getUsernameByUid(uid, new AuthRepository.ResultCallback<String>() {
                @Override
                public void onSuccess(String username) {
                    names.set(idx, username);
                    if (remaining.decrementAndGet() == 0) {
                        adapter.setItems(names);
                    }
                }

                @Override
                public void onError(String errorMsg) {
                    names.set(idx, "Error");
                    if (remaining.decrementAndGet() == 0) {
                        adapter.setItems(names);
                    }
                }
            });
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}