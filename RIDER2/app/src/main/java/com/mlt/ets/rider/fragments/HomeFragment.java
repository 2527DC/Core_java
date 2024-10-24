package com.mlt.ets.rider.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class HomeFragment extends Fragment {

    private com.mlt.ets.rider.databinding.FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new androidx.lifecycle.ViewModelProvider(this).get(HomeViewModel.class);

        binding = com.mlt.ets.rider.databinding.FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final android.widget.TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
