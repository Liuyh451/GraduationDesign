package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdminUsersFragment extends Fragment {

    public AdminUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin__users, container, false);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rv_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // TODO: Load user data and set up adapter
        // For example: UsersAdapter usersAdapter = new UsersAdapter(usersList, this::navigateToUserEdit);
        // recyclerView.setAdapter(usersAdapter);

        return view;
    }

    private void navigateToUserEdit(int userId) {
        Intent intent = new Intent(getActivity(), AdminUserEditActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }
}