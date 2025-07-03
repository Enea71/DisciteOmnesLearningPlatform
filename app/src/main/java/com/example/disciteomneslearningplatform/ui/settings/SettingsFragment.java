package com.example.disciteomneslearningplatform.ui.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.disciteomneslearningplatform.LoginActivity;
import com.example.disciteomneslearningplatform.MainActivity;
import com.example.disciteomneslearningplatform.R;
import com.example.disciteomneslearningplatform.data.model.AuthRepository;
import com.example.disciteomneslearningplatform.databinding.FragmentSettingsBinding;
import com.example.disciteomneslearningplatform.ui.AlertDialogButtons;

import API.ApiClient;
import API.ApiService;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private AuthRepository repo;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SettingsViewModel settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        ApiService api = ApiClient.getApiClient().create(ApiService.class);
        repo = AuthRepository.getInstance(api, this.requireContext());

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setting proportionate UI
        container.post(() -> {
            LinearLayout layout = binding.containerLayout;
            int width = layout.getWidth();
            int width70 = (int) ((width-32) * 0.7f);
            int width20 = (int) ((width-32) * 0.2f);
            setViewWidth(binding.inputUsername, width70);
            setViewWidth(binding.inputPassword, width70);
            setViewWidth(binding.buttonUsername, width20);
            setViewWidth(binding.buttonPassword, width20);
            });

        // Adding onclick functions
        binding.buttonPassword.setOnClickListener(v -> changePasswordOnClick(settingsViewModel));
        binding.buttonUsername.setOnClickListener(v -> changeUsernameOnCLick(settingsViewModel));

        binding.buttonDeleteAcc.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setTitle("Delete account")
                    .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                    .setPositiveButton("Delete", (dialogInterface, which) -> {
                        // do account delete
                        deleteUser(settingsViewModel);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button neg = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            // Tinting the buttons from theme attributes
            AlertDialogButtons.formatButton(requireContext(), pos, R.attr.delete_acc_color, com.google.android.material.R.attr.colorOnSecondaryContainer);
            AlertDialogButtons.formatButtonTextColor(requireContext(), neg, com.google.android.material.R.attr.colorOnSecondaryContainer);
        });
        return root;
    }
    public void setViewWidth(View v, int w){
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        lp.width = w;
        v.setLayoutParams(lp);
    }
    public void changeUsernameOnCLick(SettingsViewModel settingsViewModel){
        String newUn = binding.inputUsername.getEditText().getText().toString().trim();

        settingsViewModel.changeUsername(newUn, new AuthRepository.ResultCallback<Void>() {
            @Override public void onSuccess(Void unused) {
                repo.updateUsername(newUn);
                Log.d("NewUser",repo.getUsername());
                Toast.makeText(getContext(), "Username changed!", Toast.LENGTH_SHORT).show();
                binding.inputUsername.getEditText().setText("");
            }
            @Override public void onError(String msg) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void changePasswordOnClick(SettingsViewModel settingsViewModel){
        String newPw = binding.inputPassword.getEditText().getText().toString().trim();
        if (newPw.length() < 6) {
            Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        settingsViewModel.changePassword(newPw, new AuthRepository.ResultCallback<Void>() {
            @Override public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "Password changed!", Toast.LENGTH_SHORT).show();
                binding.inputPassword.getEditText().setText("");
            }
            @Override public void onError(String msg) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void deleteUser(SettingsViewModel settingsViewModel){

        settingsViewModel.deleteUser( new AuthRepository.ResultCallback<Void>() {
            @Override public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "User deleted!", Toast.LENGTH_SHORT).show();
                ((MainActivity) requireActivity()).doLogout();
            }
            @Override public void onError(String msg) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
