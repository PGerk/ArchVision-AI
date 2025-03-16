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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.a_abb_01project.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class for the main activity
 */
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainbinding;
    LifeCycleManagerFragment lifeCycleManagerFragment;
    AppData data;

    JSONObject currentModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new AppData(this);

        lifeCycleManagerFragment = new LifeCycleManagerFragment(this.getActivityResultRegistry());
        getLifecycle().addObserver(lifeCycleManagerFragment);

        try {
            String tmp = (String) data.config.get("llmModel");
            JSONArray ja = data.config.getJSONArray("all_llm_models");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject js = ja.getJSONObject(i);
                String tmpName = js.get("name").toString();
                if (tmpName.equals(tmp)) {
                    currentModel = js;
                }
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        mainbinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainbinding.getRoot());
        mainbinding.bottomNavigation.setOnItemSelectedListener(item -> {
            int tmp = item.getItemId();
            //Switch-Case funktioniert nicht richtig
            if (tmp == R.id.nav_home) {
                replaceFragment(new HomeFragment());
            }
            if (tmp == R.id.nav_camera) {
                CameraFragment cameraFragment = new CameraFragment();
                replaceFragment(cameraFragment);
            }
            if (tmp == R.id.nav_recommendation) {
                replaceFragment(new RecommendationFragment(0));
            }
            if (tmp == R.id.nav_settings) {
                replaceFragment(new SettingsFragment());
            }
            return true;
        });
        replaceFragment(new HomeFragment());
    }

    /**
     * Change the currently visible fragment
     *
     * @param fragment  Fragment to be displayed next
     */
    public void replaceFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main_element, fragment);
        transaction.commit();
    }

    /**
     * Selection from the navigation menu
     *
     * @param itemId    id of the navigation item
     */
    public void selectNavigationItem(int itemId) {
        mainbinding.bottomNavigation.setSelectedItemId(itemId);
    }
}


















