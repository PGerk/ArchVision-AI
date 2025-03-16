package com.example.a_abb_01project;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ortiz.touchview.TouchImageView;

public class ImageDialogFragment extends DialogFragment {

    Drawable drawable;
    MainActivity activity;

    public ImageDialogFragment(Drawable drawable, MainActivity activity) {
        this.drawable = drawable;
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_zoomable_image, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TouchImageView touchImageView = view.findViewById(R.id.zoomable_image);
        touchImageView.setMaxZoom(10);

        touchImageView.setImageDrawable(drawable);

        //Testen, ob außerhalb der Buttons geklickt wurde
        view.setOnClickListener(v -> {
            if (v != touchImageView) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = getResources().getDisplayMetrics().widthPixels;
            params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.75); // 75% der Bildschirmhöhe
            getDialog().getWindow().setAttributes(params);
        }
    }
}
