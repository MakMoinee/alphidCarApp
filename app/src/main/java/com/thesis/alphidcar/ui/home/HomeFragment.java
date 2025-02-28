package com.thesis.alphidcar.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.thesis.alphidcar.databinding.FragmentHomeBinding;
import com.thesis.alphidcar.interfaces.HomeListener;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeListener listener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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