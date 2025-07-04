package com.example.disciteomneslearningplatform;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.disciteomneslearningplatform.data.model.AuthRepository;
import com.example.disciteomneslearningplatform.data.model.GroupRepository;
import com.example.disciteomneslearningplatform.ui.groupManagement.GroupsViewModel;
import com.example.disciteomneslearningplatform.ui.home.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.disciteomneslearningplatform.databinding.ActivityMainBinding;


import API.ApiClient;
import API.ApiService;
import API.GroupAPI;


public class MainActivity extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    NavigationView navigationView;
    private AuthRepository repo;
    private GroupRepository groupRepo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Retrofit and ApiService
        ApiService api = ApiClient.getApiClient().create(ApiService.class);
        repo = AuthRepository.getInstance(api, MainActivity.this);
        groupRepo = new GroupRepository(repo);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;

        binding.appBarMain.rightButton.setOnClickListener(view ->{
            createNewGroupOverlay();
        });

         navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // adds the four lines
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_groups, R.id.nav_slideshow,R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        // Set up UI
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        // Hide FABs on Settings menu
        hideFab(navController);
        // Observer for username
        repo.getUsernameLiveData().observe(this, username -> {
            View header = navigationView.getHeaderView(0);
            TextView tvUsername = header.findViewById(R.id.username);
            TextView tvEmail = header.findViewById(R.id.email_address);
            tvUsername.setText(username);
            tvEmail.setText(repo.getEmail());

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.sign_out){
            doLogout();
            return true;
        }
        if(item.getItemId() == R.id.notifications) {
            View overlay = getLayoutInflater()
                    .inflate(R.layout.notifications, null);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(overlay)
                    .setCancelable(true)
                    .create();


            return true;

            }
        return super.onOptionsItemSelected(item);
    }

    public void doLogout(){
        Toast.makeText(this, "Goodbye, " +  repo.getUsername() , Toast.LENGTH_LONG).show();
        repo.logout();
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    private void createNewGroupOverlay() {
        // Inflate the form layout
        View overlay = getLayoutInflater()
                .inflate(R.layout.overlay, null);

        // Find the fields & button
        EditText titleInput = overlay.findViewById(R.id.overlayTitleInput);
        EditText descInput  = overlay.findViewById(R.id.overlayDescInput);
        Button saveBtn     = overlay.findViewById(R.id.overlaySaveButton);

        // Build & show the dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(overlay)
                .setCancelable(true)
                .create();
        dialog.show();

        // On Submit, send the REST POST to /groups/create
        saveBtn.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String desc  = descInput.getText().toString().trim();
            if (title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this,
                        "Please fill in both fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable the button to prevent double-taps
            saveBtn.setEnabled(false);

            groupRepo.createGroup(title, desc, new GroupRepository.ResultCallback<GroupAPI.GroupResponse>() {
                @Override
                public void onSuccess(GroupAPI.GroupResponse group) {
                    // e.g. group.id, group.name, group.description are available
                    Toast.makeText(MainActivity.this,
                            "Group “" + group.name + "” created!",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(MainActivity.this,
                            "Failed to create group: " + message,
                            Toast.LENGTH_LONG).show();
                    Log.d("ERR", "Failed:" + message);
                    saveBtn.setEnabled(true);
                }
            });
        });
    }

    private void hideFab(NavController navController){
        FloatingActionButton rightFab = findViewById(R.id.rightButton);
        FloatingActionButton leftFab  = findViewById(R.id.leftButton);

        navController.addOnDestinationChangedListener((controller, destination, args) -> {
            boolean onSettings = destination.getId() == R.id.nav_settings;
            if (onSettings) {
                rightFab.hide();
                leftFab .hide();
            } else {
                rightFab.show();
                leftFab .show();
            }
        });
    }
}