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

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.example.a_abb_01project.databinding.DialogFragmentShowHistoryImageBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Class for managing the full-screen view of the history
 */
public class FullScreenDialogFragmentHistoryImage extends DialogFragment {
    //Ersetzen durch JSON-Array mit den Daten zu einem Bild
    MainActivity app;
    private View view;
    DialogFragmentShowHistoryImageBinding binding;
    JSONObject object;
    int deletedEntry = -1;

    public interface OnDialogDismissListener {
        void onDialogDismiss(int deleted);
    }

    private final OnDialogDismissListener listener;


    public FullScreenDialogFragmentHistoryImage(JSONObject json, MainActivity activity, OnDialogDismissListener listener) {
        this.object = json;
        this.listener = listener;
        this.app = activity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DialogFragmentShowHistoryImageBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        ImageButton shareButton = binding.buttonShareWithTeam;
        ImageButton deleteButton = binding.buttonDeleteImage;
        ImageView imageView = binding.viewHistoryDialog;
        TextView name = binding.textNameOfDiagram;
        TextView detailed = binding.textDetailedHistoryText;


        try {
            imageView.setImageURI(Uri.parse((String) object.get("uri")));
            name.setText((String) object.get("name"));
            detailed.setText((String) object.get("description"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        shareButton.setOnClickListener(v -> {
            Drawable drawable = imageView.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            try {
                String timestamp = new SimpleDateFormat("yyyyMMddHHmmss'.txt'", Locale.GERMAN)
                        .format(new Date());
                File file = new File(app.getApplicationContext().getExternalCacheDir(),
                        File.separator + "shareImage" + timestamp + ".png");
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                file.setReadable(true, false);
                Uri photoUri = FileProvider.getUriForFile(app.getApplicationContext(),
                        "com.example.a_abb_01project.fileProvider",
                        file);

                android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, photoUri);
                shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                String shareText = "PlantUML: \n" + object.get("plantuml");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                shareIntent.setType("image/*");
                startActivity(android.content.Intent.createChooser(shareIntent, null));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        imageView.setOnClickListener(v -> {
            DialogFragment dialogFragment = new ImageDialogFragment(imageView.getDrawable(), app);
            dialogFragment.show(app.getSupportFragmentManager(), "imageDialogFragment");
        });

        view.setOnClickListener(v -> {
            //Testen, ob außerhalb der Buttons geklickt wurde
            if (v != deleteButton && v != shareButton && v != imageView) {
                dismiss();
            }
        });

        binding.cardviewButtonShowPlant.setOnClickListener(v -> {
            TextView textView = view.findViewById(R.id.textView_plantUML);
            ImageView imageRot = view.findViewById(R.id.imageView);
            if (textView.getVisibility() == View.GONE) {
                textView.setVisibility(View.VISIBLE);
                imageRot.setRotationX(180);
                try {
                    textView.setText((CharSequence) object.get("plantuml"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                textView.setVisibility(View.GONE);
                imageRot.setRotationX(0);
            }

        });

        binding.buttonDeleteImage.setOnClickListener(v -> {
            try {
                JSONArray jsonEntries = app.data.history.getJSONArray("entries");
                for (int i = 0; i < jsonEntries.length(); i++) {
                    if (jsonEntries.getJSONObject(i).equals(object)) {
                        jsonEntries.remove(i);
                        app.data.history.put("entries", jsonEntries);
                        app.data.saveHistory();
                        deletedEntry = i;
                        dismiss();
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(app, "Error deleting Image from history!", Toast.LENGTH_SHORT).show();
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
            Objects.requireNonNull(getDialog().getWindow()).setLayout(width, height);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        if (listener != null) {
            listener.onDialogDismiss(deletedEntry);
        }
    }


}
