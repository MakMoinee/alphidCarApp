package com.thesis.alphidcar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.github.MakMoinee.library.services.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.thesis.alphidcar.databinding.ActivityMainFormBinding;
import com.thesis.alphidcar.interfaces.HomeListener;
import com.thesis.alphidcar.preference.LocalSharedPref;

public class MainFormActivity extends AppCompatActivity implements HomeListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainFormBinding binding;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMainForm.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_alphid_car, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(MainFormActivity.this, R.id.nav_host_fragment_content_main_form);
        Utils.setUpNavigation(MainFormActivity.this, binding.navView, navController, mAppBarConfiguration);
        setNavViewText();
    }

    private void setNavViewText() {
        String firstName = new LocalSharedPref(MainFormActivity.this).getStringItem("firstName");
        String lastName = new LocalSharedPref(MainFormActivity.this).getStringItem("lastName");
        String username = new LocalSharedPref(MainFormActivity.this).getStringItem("username");

        View mView = Utils.getNavView(binding.navView);
        TextView txtFullName = mView.findViewById(R.id.txtFullName);
        TextView txtUsername = mView.findViewById(R.id.txtUsername);
        txtFullName.setText(String.format("%s %s", firstName, lastName));
        txtUsername.setText(username);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_form);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onLogout() {
        new LocalSharedPref(MainFormActivity.this).clearLogin();
        startActivity(new Intent(MainFormActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onCancelLogout() {
        navController.navigate(R.id.nav_home);
    }

    @Override
    public void accessFullSizeController() {
        startActivity(new Intent(MainFormActivity.this, AlphidCarFullSizeActivity.class));
    }

}