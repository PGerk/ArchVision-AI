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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;


import com.example.a_abb_01project.databinding.FragmentSettingsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class for the management of settings
 */
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding settingsBinding;

    int positionOfLLM = 0;
    MainActivity app;
    ArrayList<String> llms;

    public SettingsFragment() {
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
        settingsBinding = FragmentSettingsBinding.inflate(getLayoutInflater());
        View view = settingsBinding.getRoot();

        Spinner dropdown = settingsBinding.spinnerRecommendation;
        EditText api_key = settingsBinding.apiKeyField;
        llms = new ArrayList<>();
        String apiKey = "";

        try {
            String currentModel = (String) app.data.config.get("llmModel");
            JSONArray ja = app.data.config.getJSONArray("all_llm_models");
            for (int i = 0; i < ja.length(); i++) {
                String tmp = ja.getJSONObject(i).get("name").toString();
                if (tmp.equals(currentModel)) {
                    positionOfLLM = i;
                    apiKey = ja.getJSONObject(i).get("apikey").toString();
                }
                llms.add(tmp);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        //String[] items =
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, llms);
        dropdown.setAdapter(adapter);
        try {
            dropdown.setSelection(adapter.getPosition((String) app.data.config.get("llmModel")));
            api_key.setText(apiKey);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject tmp = app.data.config.getJSONArray("all_llm_models").getJSONObject(position);

                    //Set visibility of inference endpoint configuration
                    if (tmp.get("name").toString().equals("Endpoint")) {
                        settingsBinding.endpointTitle.setVisibility(View.VISIBLE);
                        settingsBinding.endpointField.setVisibility(View.VISIBLE);
                        settingsBinding.endpointField.setText(tmp.get("url").toString());
                    } else {
                        settingsBinding.endpointTitle.setVisibility(View.GONE);
                        settingsBinding.endpointField.setVisibility(View.GONE);
                    }
                    api_key.setText(tmp.get("apikey").toString());
                    app.data.config.put("llmModel", tmp.get("name").toString());
                    app.data.saveConfig();
                    positionOfLLM = position;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Speicher-Button
        settingsBinding.buttonSaveSettings.setOnClickListener(v -> {
            String api = String.valueOf(settingsBinding.apiKeyField.getText());
            try {
                app.data.config.getJSONArray("all_llm_models").getJSONObject(positionOfLLM).put("apikey", api);
                app.data.saveConfig();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        return view;
    }
}