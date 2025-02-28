package com.thesis.alphidcar;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.MakMoinee.library.interfaces.DefaultBaseListener;
import com.thesis.alphidcar.databinding.ActivityCreateAccountBinding;
import com.thesis.alphidcar.models.Users;
import com.thesis.alphidcar.services.UserServices;

public class CreateAccountActivity extends AppCompatActivity {

    ActivityCreateAccountBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.btnCreateAccount.setOnClickListener(v -> {
            String firstName = binding.editFN.getText().toString().trim();
            String middleName = binding.editMN.getText().toString().trim();
            String lastName = binding.editLN.getText().toString().trim();
            String username = binding.editUsername.getText().toString().trim();
            String password = binding.editPass.getText().toString().trim();
            String confirmPass = binding.editConfirmPass.getText().toString().trim();


            if (firstName.equals("") || lastName.equals("") || username.equals("") || password.equals("") || confirmPass.equals("")) {
                Toast.makeText(CreateAccountActivity.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {
                if (password.equals(confirmPass)) {
                    Users users = new Users.UserBuilder()
                            .setFirstName(firstName)
                            .setMiddleName(middleName)
                            .setLastName(lastName)
                            .setUsername(username)
                            .setPassword(password)
                            .build();
                    new UserServices(CreateAccountActivity.this).insertUserOnly(users, new DefaultBaseListener() {
                        @Override
                        public <T> void onSuccess(T any) {
                            Toast.makeText(CreateAccountActivity.this, "Successfully Created An Account", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onError(Error error) {
                            if (error != null && error.getLocalizedMessage() != null) {
                                Log.e("login_err", error.getLocalizedMessage());
                            }
                            Toast.makeText(CreateAccountActivity.this, "Failed To Create An Account, Please Try Again Later", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(CreateAccountActivity.this, "Password Doesn't Match", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}
