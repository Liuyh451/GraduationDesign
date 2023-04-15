package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdminEBooksFragment extends Fragment {

    public AdminEBooksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin__books, container, false);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rv_ebooks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // TODO: Load ebook data and set up adapter
        // For example: EBooksAdapter ebooksAdapter = new EBooksAdapter(ebooksList, this::navigateToEBookEdit);
        // recyclerView.setAdapter(ebooksAdapter);

        return view;
    }

    private void navigateToEBookEdit(int ebookId) {
        Intent intent = new Intent(getActivity(), AdminEBookEditActivity.class);
        intent.putExtra("ebook_id", ebookId);
        startActivity(intent);
    }
}