package com.example.disciteomneslearningplatform.ui.groupDetail;

import android.os.Bundle;
import android.util.Log;
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
import com.example.disciteomneslearningplatform.data.model.TaskAdapter;
import com.example.disciteomneslearningplatform.databinding.FragmentGroupDetailBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import API.ApiClient;
import API.ApiService;
import API.Task;

public class GroupDetailFragment extends Fragment {

    private FragmentGroupDetailBinding binding;
    private GroupDetailViewModel vm;
    private AuthRepository repo;

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

        // RecyclerView + adapter
        NameAdapter adapter = new NameAdapter(R.layout.item_member);
        binding.rvMembers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvMembers.setAdapter(adapter);

        // Prepare AuthRepository
        ApiService api = ApiClient.getApiClient().create(ApiService.class);
        repo = AuthRepository.getInstance(api, requireContext());

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
            binding.tvGroupName.setText(group.name);
            binding.tvGroupDescription.setText(group.description);

            // Call Members
            loadMemberNames(group.members, adapter, repo);

        });
        vm.error().observe(getViewLifecycleOwner(), errMsg -> {
            // Error path
            Toast.makeText(requireContext(),
                            "Failed to load group: " + errMsg,
                            Toast.LENGTH_LONG)
                    .show();
        });
// after your members‐adapter setup:
        TaskAdapter taskAdapter = new TaskAdapter(R.layout.item_task);
        binding.rvTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTasks.setAdapter(taskAdapter);

// observe tasks LiveData
        vm.tasks().observe(getViewLifecycleOwner(), list -> {
            taskAdapter.setItems(list);
        });

// once the group loads, fetch its tasks
        vm.group().observe(getViewLifecycleOwner(), grp -> {
            String bearer = "Bearer " + repo.getIdToken();
            vm.loadTasks(bearer, grp.id);
        });

// FAB to add a new task

        binding.fabAddTask.setOnClickListener(v -> showAddTaskDialog());

// … elsewhere in the Fragment:

        // Kick off load with the UID from bundle
        String groupId = requireArguments().getString("groupId");
        String bearer = "Bearer " + repo.getIdToken();
        vm.loadGroup(bearer, groupId);
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
    private void showAddTaskDialog() {
        View form = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_task, null);

        TextInputEditText etTitle = form.findViewById(R.id.etTaskTitle);
        TextInputEditText etDesc  = form.findViewById(R.id.etTaskDesc);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("New Task")
                .setView(form)
                .setPositiveButton("Create", (d,w) -> {
                    String title = etTitle.getText().toString().trim();
                    String desc  = etDesc.getText().toString().trim();
                    if (title.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "Title cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Task t = new Task();
                    t.title       = title;
                    t.description = desc;

                    String bearer = "Bearer " + repo.getIdToken();
                    String groupId = requireArguments().getString("groupId");
                    vm.createTask(bearer, groupId, t);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }
}