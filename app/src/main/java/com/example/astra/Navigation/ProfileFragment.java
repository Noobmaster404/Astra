package com.example.astra.Navigation;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.astra.Auth.LoginActivity;
import com.example.astra.R;
import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> signOut());

        return view;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();

        // Переход на LoginActivity
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finish();
    }
    }

