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

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a_abb_01project.databinding.FragmentCameraBinding;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for camera management
 */
public class CameraFragment extends Fragment {

    FragmentCameraBinding camerabinding;

    //Liste mit allen markierten Bilder die gelöscht werden sollen => nur positionen im Recycler werden gespeichert
    List<Uri> markedPicturesForDelete = new ArrayList<>();
    //Adapter-Klasse für die Recycler-View
    PhotoAdapter adapter;

    MainActivity app;

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() instanceof MainActivity) {
            app = (MainActivity) requireActivity();

        }
        // Inflate the layout for this fragment
        if (savedInstanceState != null) {
            app.data.list_imageUri = savedInstanceState.getParcelableArrayList("imageUriList");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        camerabinding = FragmentCameraBinding.inflate(getLayoutInflater());
        View rootView = camerabinding.getRoot();

        //RecyclerView zur Anzeige der mit der Camera gemachte Bilder
        RecyclerView recyclerView = camerabinding.recyclerViewPhoto;
        //Anzahl der Spalten bestimmen => 2/1 abwägen was bessser sichtbar ist

        //RecyclerView benötigt einen LayoutManager, da es dynamisch verändert werden kann
        GridLayoutManager gridLayoutManager = new GridLayoutManager(app, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //Adapter für den RecylcerV
        adapter = new PhotoAdapter(app.data.list_imageUri);
        recyclerView.setAdapter(adapter);
        app.lifeCycleManagerFragment.setSettings(app, adapter);

        //Bindings der Buttons
        camerabinding.buttonTakePicture.setOnClickListener(view -> app.lifeCycleManagerFragment.checkPermissionAndOpenCamera());

        camerabinding.buttonUploadImage.setOnClickListener(view -> {
            if (!app.lifeCycleManagerFragment.isOverloadOfImages()) {
                app.lifeCycleManagerFragment.openMediastore();
            }
        });

        camerabinding.buttonChangeToMicroFragment.setOnClickListener(view -> app.replaceFragment(new AudioFragment()));

        camerabinding.buttonCreateNewUml.setOnClickListener(view -> {

            if (!markedPicturesForDelete.isEmpty()) {
                // Erstelle den AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(app);
                builder.setMessage("Möchtest du markierte Bilder sicher löschen?");
                // Hinzufügen der Ja-Option
                builder.setPositiveButton("Ja", (dialog, id) -> {
                    // Aktionen ausführen, wenn "Ja" ausgewählt wird
                    adapter.removeItemListFromPhotoList(markedPicturesForDelete);
                    // Entferne die Elemente aus der Original-Liste
                    for (Uri uriToRemove : markedPicturesForDelete) {
                        app.data.list_imageUri.remove(uriToRemove);
                    }
                    markedPicturesForDelete.clear();
                    camerabinding.buttonCreateNewUml.setImageResource(R.drawable.arrow_square_right_svgrepo_com);
                    camerabinding.buttonCreateNewUml.setBackgroundTintList(ContextCompat.getColorStateList(app, R.color.rot_hell));

                });
                // Hinzufügen der Nein-Option
                builder.setNegativeButton("Nein", (dialog, id) -> {
                    // Aktionen ausführen, wenn "Nein" ausgewählt wird
                });

                // AlertDialog anzeigen
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                //Weiterleitung nach der Classificaiton
                //Überprüfung, ob es Daten zum Weiterleiten gibt
                if (app.data.audioStrings.isEmpty() && app.data.list_imageUri.isEmpty()) {
                    Toast.makeText(app, "Bild oder Text zur Erstellung benötigt!!", Toast.LENGTH_SHORT).show();
                } else {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).selectNavigationItem(R.id.nav_recommendation);
                    }
                    app.replaceFragment(new TagClassification(1));
                }
            }
        });

        //WEITER: Hier hat man auf die Bilder geklickt
        // Anzeige der Bilder, etc. möglich
        adapter.setOnItemClickListener(this::openFullScreenOfImage);
        //Beim Langen halten
        adapter.setOnItemLongClickListener(position -> {
            if (markedPicturesForDelete.isEmpty()) {
                camerabinding.buttonCreateNewUml.setImageResource(R.drawable.trash_bin_trash_svgrepo_com);
            }

            if (!markedPicturesForDelete.contains(app.data.list_imageUri.get(position))) {
                markedPicturesForDelete.add(app.data.list_imageUri.get(position));
            } else {
                markedPicturesForDelete.remove(app.data.list_imageUri.get(position));
                if (markedPicturesForDelete.isEmpty()) {
                    camerabinding.buttonCreateNewUml.setImageResource(R.drawable.arrow_square_right_svgrepo_com);
                }
            }
        });
        return rootView;
    }

    /**
     * Open the full-screen view of an image
     *
     * @param position  Position of the image in the selection
     */
    public void openFullScreenOfImage(int position) {
        DialogFragment dialogFragment = new FullScreenDialogFragmentGalleryImage(app.data.list_imageUri.get(position), app, this);
        dialogFragment.show(app.getSupportFragmentManager(), "FullScreenDialogFragmentGalleryImage");

    }

    /**
     * Deleting an image from the overview
     *
     * @param uriToDelete   Uri of the image to be deleted
     */
    public void deleteSingleImage(Uri uriToDelete) {
        adapter.notifyItemRemoved(app.data.list_imageUri.indexOf(uriToDelete));
        app.data.list_imageUri.remove(uriToDelete);
        //Falls markedForDelete
        if (markedPicturesForDelete.contains(uriToDelete)) {
            markedPicturesForDelete.remove(uriToDelete);
            if (markedPicturesForDelete.isEmpty()) {
                camerabinding.buttonCreateNewUml.setImageResource(R.drawable.arrow_square_right_svgrepo_com);
            }
        }
    }

    /**
     * Saving the images in the smartphone memory
     *
     * @param drawable   BitmapDrawable of the selected image
     */
    public void saveImage(BitmapDrawable drawable) {
        Bitmap bitmap = drawable.getBitmap();

        ContentResolver contentResolver = app.getContentResolver();
        ContentValues values = new ContentValues();
        String filename = String.format("%d.jpg", System.currentTimeMillis());

        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/SoftwareArchitectur");

        Uri uriConent = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uriConent == null) {
            Toast.makeText(app, "Failed to create Uri.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //Output-Stream Creation based on uriContent
            OutputStream outputStream = contentResolver.openOutputStream(uriConent);
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            }
            MediaScannerConnection.scanFile(app, new String[]{uriConent.getPath()}, null, (path, uri) -> Log.i("GalleryUtils", "Scanned " + path + ":"));

            Toast.makeText(app, "Successfully saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(app, "Failed to OutputStream.", Toast.LENGTH_SHORT).show();
        }
    }

}