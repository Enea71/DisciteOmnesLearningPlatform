package com.example.disciteomneslearningplatform.ui.settings;

import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.disciteomneslearningplatform.data.model.AuthRepository;
import com.example.disciteomneslearningplatform.databinding.FragmentSettingsBinding;
import com.google.android.material.textfield.TextInputLayout;

import API.ApiClient;
import API.ApiService;
import API.UserAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private AuthRepository repo;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SettingsViewModel settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        ApiService api = ApiClient.getApiClient().create(ApiService.class);
        repo = new AuthRepository(api, this.requireContext());

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        container.post(() -> {
            LinearLayout layout = binding.containerLayout;
            int width = layout.getWidth();
            int width70 = (int) ((width-32) * 0.7f);
            int width30 = (int) ((width-32) * 0.2f);
            setW(binding.inputUsername, width70);
            setW(binding.inputPassword, width70);
            setW(binding.buttonUsername, width30);
            setW(binding.buttonPassword, width30);
            });

        // 2) Wire the button click to call VM.changePassword(...)
        binding.buttonPassword.setOnClickListener(v -> {
            String newPw = binding.inputPassword.getEditText().getText().toString().trim();
            if (newPw.length() < 6) {
                Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            settingsViewModel.changePassword(newPw, new AuthRepository.ResultCallback<Void>() {
                @Override public void onSuccess(Void unused) {
                    Toast.makeText(getContext(), "Password changed!", Toast.LENGTH_SHORT).show();
                }
                @Override public void onError(String msg) {
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        });
        return root;
    }
    public void setW(View v,int w){
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        lp.width = w;
        v.setLayoutParams(lp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
