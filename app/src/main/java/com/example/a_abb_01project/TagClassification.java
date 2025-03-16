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
import androidx.fragment.app.Fragment;

import com.example.a_abb_01project.databinding.FragmentTagClassificationBinding;

import java.util.Objects;

/**
 * Class for the management of the tag classification
 */
public class TagClassification extends Fragment {

    private MainActivity app;

    private final int typeOfCall;

    public TagClassification(int typeOfCall) {
        this.typeOfCall = typeOfCall;
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.app = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        com.example.a_abb_01project.databinding.FragmentTagClassificationBinding tagClassificationItemBinding = FragmentTagClassificationBinding.inflate(getLayoutInflater());
        View rootView = tagClassificationItemBinding.getRoot();
        tagClassificationItemBinding.cardViewClass.setOnClickListener(v -> openRecommendation("Klassen"));
        tagClassificationItemBinding.cardViewActivity.setOnClickListener(v -> openRecommendation("Aktivitäts"));
        tagClassificationItemBinding.cardViewComp.setOnClickListener(v -> openRecommendation("Komponenten"));
        tagClassificationItemBinding.cardViewUsecase.setOnClickListener(v -> openRecommendation("UseCase"));
        tagClassificationItemBinding.cardViewSequenz.setOnClickListener(v -> openRecommendation("Sequenz"));
        tagClassificationItemBinding.cardViewAdditional.setOnClickListener(v -> openRecommendation("UML"));
        return rootView;
    }

    /**
     * Change Fragment to recommendation
     *
     * @param type  Type of call
     */
    private void openRecommendation(String type) {
        try {
            app.data.umlDiagrams.get(app.data.indexOfCurrent).type = type;
            RecommendationFragment rf = new RecommendationFragment(typeOfCall);
            app.data.calledByGallery = true;
            app.replaceFragment(rf);


        } catch (Exception e) {
            Log.e("Tag", Objects.requireNonNull(e.getMessage()));
        }


    }
}