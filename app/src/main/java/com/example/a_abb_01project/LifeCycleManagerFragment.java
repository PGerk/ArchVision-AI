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

import static android.media.MediaRecorder.VideoSource.CAMERA;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import org.json.JSONException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Class for the management of camera/audio
 */
public class LifeCycleManagerFragment implements DefaultLifecycleObserver {
    private final ActivityResultRegistry cameraRegistry;
    private ActivityResultLauncher<PickVisualMediaRequest> pickVisualMediaRequest;
    private PhotoAdapter adapter;
    private MainActivity app;
    private AudioResultCallback audioResultCallback;

    private int maxImages = 2;

    Uri imageUri;
    ActivityResultLauncher<Uri> takePictureLauncher;

    ActivityResultLauncher<Intent> openAudioLauncher;

    LifeCycleManagerFragment(@NonNull ActivityResultRegistry registry) {
        cameraRegistry = registry;
    }

    public void onCreate(@NonNull LifecycleOwner owner) {
        pickVisualMediaRequest = cameraRegistry.register("PickVisualMediaRequest", owner,
                new ActivityResultContracts.PickVisualMedia(),
                result -> {
                    if (result != null) {
                        try {
                            // Hier wird nur das erste (und einzige) ausgewählte Bild verwendet
                            Uri imageUri = result;
                            adapter.addItem(imageUri);
                        } catch (Exception exception) {
                            Log.e("Fehler:", Objects.requireNonNull(exception.getMessage()));
                            Toast.makeText(app, "Error getting selected file", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(app, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        takePictureLauncher = cameraRegistry.register("TakePicture", owner,
                new ActivityResultContracts.TakePicture(),
                result -> {
                    try {
                        if (result) {
                            //Informiere adapter über Änderungen der Daten => Update der Recycler-View
                            adapter.notifyItemInserted(app.data.list_imageUri.size());
                        } else {
                            app.data.list_imageUri.remove(app.data.list_imageUri.size() - 1);
                        }
                    } catch (Exception exception) {
                        exception.getStackTrace();
                    }
                }
        );


        openAudioLauncher = cameraRegistry.register("OpenAudio", owner,
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        ArrayList<String> speechResult = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (speechResult != null && !speechResult.isEmpty()) {
                            // Ensure TextTv is properly initialized
                            audioResultCallback.onAudioResult(speechResult.get(0));
                        }
                    }
                }
        );

        //Ende Methode onCreate
    }

    /**
     * Check authorisation and activate and open camera
     */
    public void checkPermissionAndOpenCamera() {
        if (ActivityCompat.checkSelfPermission(app,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(app,
                    new String[]{Manifest.permission.CAMERA}, CAMERA);
        } else {
            if (!isOverloadOfImages()) {
                imageUri = createUri();
                //provisorisches hinzufügen, falls in der Kamera-Ansicht das Format geändert wird
                app.data.list_imageUri.add(imageUri);

                //Maybe, umbauen
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        app.data.umlDiagrams.get(app.data.indexOfCurrent).uri_imagelist.add(imageUri);
                    }
                } catch (Exception e) {
                    Log.e("Fehler:", Objects.requireNonNull(e.getMessage()));
                }
                takePictureLauncher.launch(imageUri);
            }
        }
    }
    /**
     * Start Mediastore
     */
    public void openMediastore() {
        pickVisualMediaRequest.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    /**
     * Callback for audioresult
     *
     * @param audioResultCallback   callback
     */
    public void setAudioResultCallback(AudioResultCallback audioResultCallback) {
        this.audioResultCallback = audioResultCallback;
    }

    /**
     * Set number of images
     *
     * @param app       MainActivity
     * @param adapter   Adapter
     */
    public void setSettings(MainActivity app, PhotoAdapter adapter) {
        this.adapter = adapter;
        this.app = app;
        try {
            this.maxImages = Integer.parseInt((String) app.currentModel.get("numberOfImages"));
        } catch (JSONException e) {
            Log.e("Fehler:", Objects.requireNonNull(e.getMessage()));
        }

    }

    /**
     * Check for maximum images
     *
     * @return  true if to much images; false otherwise
     */
    public boolean isOverloadOfImages() {
        if (app.data.list_imageUri.size() >= maxImages) {
            Toast.makeText(app, "Maximum number of pictures reached!", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Create Uri to save more than one image
     *
     * @return  Uri to image
     */
    private Uri createUri() {
        //Erstellen der URI für eindeutige Bilder, damit mehrere gespeichert werden könenn
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "camera_photo" + timeStamp + ".jpg";
        File imageFile = new File(app.getApplicationContext().getFilesDir(), fileName);
        return FileProvider.getUriForFile(
                app.getApplicationContext(),
                "com.example.a_abb_01project.fileProvider",
                imageFile
        );
    }

    /**
     * Open audio input
     */
    public void openAudio() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak");
        openAudioLauncher.launch(intent);
    }

}
