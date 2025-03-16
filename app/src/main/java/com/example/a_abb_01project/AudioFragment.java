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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.a_abb_01project.databinding.FragmentAudioBinding;

/**
 * Class for audio management
 */
public class AudioFragment extends Fragment implements AudioResultCallback {
    FragmentAudioBinding audioBinding;
    MainActivity app;

    public AudioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof MainActivity) {
            app = (MainActivity) requireActivity();

        }
        app.lifeCycleManagerFragment.setAudioResultCallback(result -> {
            app.data.audioStrings.add(result + "\n");
            audioBinding.textTv.setText(getAudioStringFromData());
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        audioBinding = FragmentAudioBinding.inflate(getLayoutInflater());
        View rootView = audioBinding.getRoot();
        audioBinding.buttonSwitchToCamera.setOnClickListener(view -> app.replaceFragment(new CameraFragment()));

        audioBinding.voiceBtn.setOnClickListener(view -> app.lifeCycleManagerFragment.openAudio());

        audioBinding.buttonCreateNewUml.setOnClickListener(view -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectNavigationItem(R.id.nav_recommendation);
            }
            app.data.umlDiagrams.get(app.data.indexOfCurrent).audioString = audioBinding.textTv.getText().toString();
            app.replaceFragment(new TagClassification(2));
        });

        audioBinding.textTv.setText(getAudioStringFromData());

        return rootView;
    }

    @Override
    public void onAudioResult(String result) {
        Toast.makeText(app, result, Toast.LENGTH_SHORT).show();
    }

    /**
     * Merge all audio strings
     *
     * @return  Full audio string
     */
    private String getAudioStringFromData() {
        StringBuilder audioString = new StringBuilder();
        for (String content : app.data.audioStrings) {
            audioString.append(content);
        }
        return audioString.toString();
    }
}