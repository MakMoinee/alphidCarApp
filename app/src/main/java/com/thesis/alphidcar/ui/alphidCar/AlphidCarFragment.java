package com.thesis.alphidcar.ui.alphidCar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thesis.alphidcar.databinding.FragmentAlphidCarBinding;
import com.thesis.alphidcar.interfaces.HomeListener;

public class AlphidCarFragment extends Fragment {

    FragmentAlphidCarBinding binding;
    HomeListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlphidCarBinding.inflate(inflater, container, false);
        setListener();
        return binding.getRoot();
    }

    private void setListener() {
        binding.btnStart.setOnClickListener(v -> listener.accessFullSizeController());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
