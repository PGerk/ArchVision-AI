/*
 * MIT License
 *
 * Copyright (c) 2024 RUB-SE-LAB-2024
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.a_abb_01project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a_abb_01project.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Class for the management of projects
 */
public class HomeFragment extends Fragment implements FullScreenDialogFragmentHistoryImage.OnDialogDismissListener {
    MainActivity app;
    UMLAdapter umlAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getActivity() instanceof MainActivity) {
            app = (MainActivity) requireActivity();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.buttonCreateNewUml.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                app.data.resetAppData();
                ((MainActivity) getActivity()).selectNavigationItem(R.id.nav_camera);
            }
        });

        RecyclerView recyclerView = binding.recyclerViewMainMenu;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        umlAdapter = new UMLAdapter();
        recyclerView.setAdapter(umlAdapter);

        //Add History to Gallery
        try {
            if (app.data.history.has("entries")) {
                JSONArray jsonEntries = app.data.history.getJSONArray("entries");
                for (int i = 0; i < jsonEntries.length(); i++) {
                    JSONObject tmp = jsonEntries.getJSONObject(i);
                    umlAdapter.addItem(tmp);
                }
            }
        } catch (JSONException e) {
            Log.e("Fehler:", Objects.requireNonNull(e.getMessage()));
        }
        umlAdapter.setOnItemClickListener(this::openFullScreenOfImage);
        return view;
    }

    /**
     * Opening the full-screen view of a project
     *
     * @param position  Position of the project
     */
    public void openFullScreenOfImage(int position) {
        JSONArray jsonEntries;
        try {
            if (app.data.history.has("entries")) {
                jsonEntries = app.data.history.getJSONArray("entries");
                DialogFragment dialogFragment = new FullScreenDialogFragmentHistoryImage((JSONObject) jsonEntries.get(position), app, this);
                dialogFragment.show(app.getSupportFragmentManager(), "fullScreen");
            }
        } catch (JSONException e) {
            Log.e("Fehler:", Objects.requireNonNull(e.getMessage()));
        }
    }

    /**
     * Update history
     *
     * @param deleted   Image to be removed
     */
    public void onDialogDismiss(int deleted) {
        //Methode damit die Histroy aktualisiert wird, wenn das Dialog-Fenster geschlossen wird
        if (deleted != -1) {
            //Bauen der Anwendung
            umlAdapter.removeItemFromHistoryView(deleted);
        }
    }
}