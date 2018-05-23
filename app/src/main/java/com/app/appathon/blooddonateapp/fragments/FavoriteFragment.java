package com.app.appathon.blooddonateapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.adapter.FavoriteAdapter;
import com.app.appathon.blooddonateapp.database.FirebaseDatabaseHelper;
import com.app.appathon.blooddonateapp.model.FavoriteModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment implements FirebaseDatabaseHelper.FavoriteInterface {


    private RecyclerView favoriteView;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public static FavoriteFragment newInstance(){
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        favoriteView = (RecyclerView) rootView.findViewById(R.id.favoriteList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        favoriteView.setLayoutManager(layoutManager);

        FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper(getActivity(), this);
        databaseHelper.getFavorites();
        return rootView;
    }

    @Override
    public void onData(ArrayList<FavoriteModel> favorites) {
        FavoriteAdapter favoriteAdapter = new FavoriteAdapter(favorites, getActivity());
        favoriteView.setAdapter(favoriteAdapter);
    }

    @Override
    public void onError(String error) {

    }
}
