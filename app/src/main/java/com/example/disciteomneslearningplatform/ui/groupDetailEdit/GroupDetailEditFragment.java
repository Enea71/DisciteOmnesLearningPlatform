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

import com.example.disciteomneslearningplatform.R;
import com.example.disciteomneslearningplatform.data.model.AuthRepository;
import com.example.disciteomneslearningplatform.data.model.NameAdapter;
import com.example.disciteomneslearningplatform.databinding.FragmentGroupEditBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import API.ApiClient;
import API.ApiService;
import API.Group;

public class GroupDetailEditFragment extends Fragment {

    private FragmentGroupEditBinding binding;
    private GroupDetailViewModel vm;
    private AuthRepository repo;
    private NameAdapter adapter;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1) Setup RecyclerView with adapter
        adapter = new NameAdapter(R.layout.item_member_edit);
        binding.rvMembers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvMembers.setAdapter(adapter);

        // 2) Initialize AuthRepository and ViewModel
        ApiService api = ApiClient.getApiClient().create(ApiService.class);
        repo = AuthRepository.getInstance(api, requireContext());
        bearer = "Bearer " + repo.getIdToken();
        vm = new ViewModelProvider(this).get(GroupDetailViewModel.class);
        groupId = requireArguments().getString("groupId");

        // 3) Observe LiveData before loading
        vm.group().observe(getViewLifecycleOwner(), group -> {
            // Populate fields
            binding.newGroupTitle.setText(group.name);
            binding.newGroupDescription.setText(group.description);
            // Refresh member list
            loadMemberNames(group.members);
        });

        vm.error().observe(getViewLifecycleOwner(), err ->
                Toast.makeText(requireContext(), "Error: " + err, Toast.LENGTH_LONG).show()
        );

        vm.updateResult().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Group updated", Toast.LENGTH_SHORT).show();
                // reload full group
                vm.loadGroup(bearer, groupId);
            }
        });

        vm.memberUpdateResult().observe(getViewLifecycleOwner(), added -> {
            if (Boolean.TRUE.equals(added)) {
                Toast.makeText(requireContext(), "Member added!", Toast.LENGTH_SHORT).show();
                // reload members via group reload
                vm.loadGroup(bearer, groupId);
            } else {
                Toast.makeText(requireContext(), "Failed to add member", Toast.LENGTH_LONG).show();
            }
        });

        // 4) Load initial group data
        vm.loadGroup(bearer, groupId);

        // 5) Wire Save buttons
        binding.btnSaveTitle.setOnClickListener(v -> {
            String newTitle = binding.newGroupTitle.getText().toString().trim();
            vm.updateGroupTitle(bearer, newTitle);
        });
        binding.btnSaveDescription.setOnClickListener(v -> {
            String newDesc = binding.newGroupDescription.getText().toString().trim();
            vm.updateGroupDescription(bearer, newDesc);
        });

        // 6) FloatingActionButton for adding members
        FloatingActionButton addFab = requireActivity().findViewById(R.id.leftButton);
        addFab.show();
        addFab.setOnClickListener(v -> showAddMemberDialog());
    }

    private void loadMemberNames(List<String> memberIds) {
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
                @Override public void onSuccess(String username) {
                    names.set(idx, username);
                    if (remaining.decrementAndGet() == 0) {
                        adapter.setItems(names);
                    }
                }
                @Override public void onError(String errorMsg) {
                    names.set(idx, "Error");
                    if (remaining.decrementAndGet() == 0) {
                        adapter.setItems(names);
                    }
                }
            });
        }
    }

    private void showAddMemberDialog() {
        TextInputEditText input = new TextInputEditText(requireContext());
        input.setHint("Enter username to add");
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Member")
                .setView(input)
                .setPositiveButton("Add", (dlg, which) -> {
                    String username = input.getText().toString().trim();
                    if (!username.isEmpty()) lookupAndAddMember(username);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void lookupAndAddMember(String username) {
        repo.getUidByUsername(username, new AuthRepository.ResultCallback<String>() {
            @Override public void onSuccess(String uid) {
                addMemberToGroup(uid);
            }
            @Override public void onError(String errorMsg) {
                Toast.makeText(requireContext(),
                        "Couldn’t find user \"" + username + "\": " + errorMsg,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addMemberToGroup(String newUid) {
        Group current = vm.group().getValue();
        if (current == null) return;
        if (current.members.contains(newUid)) {
            Toast.makeText(requireContext(), "Already a member", Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> updated = new ArrayList<>(current.members);
        updated.add(newUid);
        vm.updateGroupMembers(bearer, current.id, updated);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // cleanup
        FloatingActionButton addFab = requireActivity().findViewById(R.id.leftButton);
        addFab.setOnClickListener(null);
        binding = null;
    }

}
