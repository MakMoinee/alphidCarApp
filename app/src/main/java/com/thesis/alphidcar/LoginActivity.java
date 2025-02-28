package com.thesis.alphidcar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.MakMoinee.library.common.MapForm;
import com.github.MakMoinee.library.interfaces.DefaultBaseListener;
import com.thesis.alphidcar.databinding.ActivityLoginBinding;
import com.thesis.alphidcar.models.Users;
import com.thesis.alphidcar.preference.LocalSharedPref;
import com.thesis.alphidcar.services.UserServices;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        int id = new LocalSharedPref(LoginActivity.this).getIntItem("id");
        if (id != 0) {
            startActivity(new Intent(LoginActivity.this, MainFormActivity.class));
            finish();
        }
    }

    private void setListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            String username = binding.editUsername.getText().toString().trim();
            String password = binding.editPassword.getText().toString().trim();

            if (username.equals("") || password.equals("")) {
                Toast.makeText(LoginActivity.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {
                new UserServices(LoginActivity.this).findUserByLogin(username, password, new DefaultBaseListener() {
                    @Override
                    public <T> void onSuccess(T any) {
                        if (any instanceof Users) {
                            Users newUser = (Users) any;
                            if (newUser != null) {
                                new LocalSharedPref(LoginActivity.this).storeLogin(MapForm.convertObjectToMap(newUser));
                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainFormActivity.class));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        if (error != null && error.getLocalizedMessage() != null) {
                            Log.e("login_err", error.getLocalizedMessage());
                        }
                        Toast.makeText(LoginActivity.this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        binding.txtCreateAccount.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class)));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            binding.editUsername.setText("");
            binding.editPassword.setText("");
        }
    }
}
