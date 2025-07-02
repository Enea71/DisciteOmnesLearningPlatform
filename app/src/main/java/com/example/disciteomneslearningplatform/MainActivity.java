package com.example.disciteomneslearningplatform;

import static androidx.appcompat.app.AlertDialog.*;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.disciteomneslearningplatform.databinding.ActivityMainBinding;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // Get the current user’s UID


    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;

        binding.appBarMain.rightButton.setOnClickListener(view ->{
        //    showOverlayDialog();
        });

        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // adds the four lines
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //populateNavHeader(navigationView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            String[] options = {"Profile", "Notifications", "Logout"};

            Builder builder = new Builder(this);
            builder.setTitle("Settings Options")
                    .setItems(options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {
                            // Handle item click
                            switch (i) {
                                case 0:
                                    // Open Profile
                                    break;
                                case 1:
                                    // Open Notifications
                                    break;
                                case 2:
                                    // Perform Logout
                                    break;
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    /*
    private void showOverlayDialog() {
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

        // On Submit, write the form into /users/{uid}
        saveBtn.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String desc  = descInput.getText().toString().trim();
            if (title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this,
                        "Please fill in both fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String,Object> data = new HashMap<>();
            data.put("title", title);
            data.put("description", desc);
            data.put("timestamp",
                    FieldValue.serverTimestamp());

            db.collection("users")
                    .document(uid)
                    .set(data)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this,
                                "Saved!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this,
                                "Save failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        });

        dialog.show();
    }
    private void populateNavHeader(NavigationView navView) {
        // 1) fetch the header container
        View header = navView.getHeaderView(0);

        // 2) find the views inside that header
        TextView tvUsername = header.findViewById(R.id.username);
        TextView tvEmail    = header.findViewById(R.id.email_address);

        // 3) get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DocumentReference profileRef = db
                    .collection("Username")
                    .document(uid);
            profileRef.get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String username = snapshot.getString("username");
                            tvUsername.setText(username);
                        } else {
                            Toast.makeText(this,
                                    "Error loading profile: ",
                                    Toast.LENGTH_LONG).show();                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this,
                                "Error loading profile: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });

            tvEmail   .setText(user.getEmail());
        }
    }

*/
}