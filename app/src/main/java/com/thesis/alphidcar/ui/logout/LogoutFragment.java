package com.thesis.alphidcar.ui.logout;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.thesis.alphidcar.databinding.FragmentHomeBinding;
import com.thesis.alphidcar.interfaces.HomeListener;

public class LogoutFragment extends Fragment {

    FragmentHomeBinding binding;
    HomeListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(requireContext());
        DialogInterface.OnClickListener dListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_NEGATIVE -> {
                    listener.onLogout();
                }
                default -> {
                    listener.onCancelLogout();
                    dialog.dismiss();
                }
            }
        };
        AlertDialog alertDialog = mBuilder.setMessage("Are You Sure You Want Logout?")
                .setNegativeButton("Yes", dListener)
                .setPositiveButton("Cancel", dListener)
                .setCancelable(false)
                .show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        TextView messageView = alertDialog.findViewById(android.R.id.message);
        if (messageView != null) {
            messageView.setTextColor(Color.WHITE); // Set to white or any color you prefer
        }

        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (negativeButton != null) {
            negativeButton.setTextColor(Color.WHITE); // Change to your preferred color
        }

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (positiveButton != null) {
            positiveButton.setTextColor(Color.WHITE); // Change to your preferred color
        }
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof HomeListener) {
            listener = (HomeListener) context;

        } else {
            throw new ClassCastException(context.toString() + " must implement HomeListener");
        }
    }
}
