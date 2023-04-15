package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Frag_3 extends Fragment {

    public Frag_3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_3, container, false);

        // Find TextViews by id
        TextView tvMyOrders = view.findViewById(R.id.tv_my_orders);
        TextView tvMyRatings = view.findViewById(R.id.tv_my_ratings);
        TextView tvMyFavorites = view.findViewById(R.id.tv_my_favorites);
        TextView tvMyReviews = view.findViewById(R.id.tv_my_reviews);

        // Set click listeners
        tvMyOrders.setOnClickListener(v -> navigateToMyOrders());
        tvMyRatings.setOnClickListener(v -> navigateToMyRatings());
        tvMyFavorites.setOnClickListener(v -> navigateToMyFavorites());
        tvMyReviews.setOnClickListener(v -> navigateToMyReviews());

        return view;
    }

    private void navigateToMyOrders() {
        Intent intent = new Intent(getActivity(), MyOrdersActivity.class);
        startActivity(intent);
    }

    private void navigateToMyRatings() {
        Intent intent = new Intent(getActivity(), MyRatingsActivity.class);
        startActivity(intent);
    }

    private void navigateToMyFavorites() {
        Intent intent = new Intent(getActivity(), MyFavoritesActivity.class);
        startActivity(intent);
    }

    private void navigateToMyReviews() {
        Intent intent = new Intent(getActivity(), MyReviewsActivity.class);
        startActivity(intent);
    }
}