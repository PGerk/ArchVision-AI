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

import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.a_abb_01project.databinding.DialogFragmentShowHistoryImageBinding;
import com.example.a_abb_01project.databinding.DialogFragmentShowImageBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Class for managing the full-screen view of the gallery
 */
public class FullScreenDialogFragmentGalleryImage extends DialogFragment {

    Uri imageUri;
    DialogFragmentShowImageBinding binding;
    View view;
    MainActivity activity;
    CameraFragment fragment;

    public FullScreenDialogFragmentGalleryImage(Uri uri, MainActivity activity, CameraFragment fragment) {
        this.imageUri = uri;
        this.activity = activity;
        this.fragment = fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFragmentShowImageBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        ImageView imageView = view.findViewById(R.id.dialog_fragment_individual_gallery_photo);
        ImageButton deleteButton = view.findViewById(R.id.dialog_fragment_delete_image);
        ImageButton saveButton = view.findViewById(R.id.dialog_fragment_save_image);


        Log.d("FullScreenDialogFragment", "ImageView: " + (imageView != null));
        Log.d("FullScreenDialogFragment", "DeleteButton: " + (deleteButton != null));
        Log.d("FullScreenDialogFragment", "SaveButton: " + (saveButton != null));

        imageView.setImageURI(imageUri);

        deleteButton.setOnClickListener(v -> {
            fragment.deleteSingleImage(imageUri);
            dismiss();
        });

        saveButton.setOnClickListener(v -> {
            fragment.saveImage((BitmapDrawable) imageView.getDrawable());
            dismiss();
        });

        //Testen, ob außerhalb der Buttons geklickt wurde
        view.setOnClickListener(v -> {
            if (v != deleteButton && v != saveButton) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            getDialog().getWindow().setLayout(width, height);
        }
    }
}
