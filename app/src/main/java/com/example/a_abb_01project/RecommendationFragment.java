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
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.a_abb_01project.LLM.ILLMService;
import com.example.a_abb_01project.LLM.ILLMServiceCallback;
import com.example.a_abb_01project.LLM.LLMServiceFactory;
import com.example.a_abb_01project.databinding.FragmentRecommendationBinding;
import com.example.a_abb_01project.diagram.PlantUML;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Class for managing results and recommendations
 */
public class RecommendationFragment extends Fragment implements PlantUML.OnImageRenderedCallback {
    private View view;
    private int typeOfCall;
    private boolean successful_call = false;
    private int oldIndexOfCurrent = 0;
    MainActivity app;
    FragmentRecommendationBinding recommendationBinding;

    public RecommendationFragment(int typeOfCall) {
        this.typeOfCall = typeOfCall;
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
        recommendationBinding = FragmentRecommendationBinding.inflate(getLayoutInflater());
        view = recommendationBinding.getRoot();


        EditText editText_adjustment = view.findViewById(R.id.editText_adjustment);
        TextView textView_plantUML = view.findViewById(R.id.textView_plantUML);

        recommendationBinding.buttonOptimize.setOnClickListener(v -> {
            changeButtonForImages();
            // Use the Builder class for convenient dialog construction.
            String[] choices = {"Sustainability and energy-efficiency", "Clarity and comprehensibility", "Modularity and abstraction"};
            final int[] selectedChoice = {0};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Optimise with a focus one of the following criteria?").setSingleChoiceItems(choices, 0, (dialog, id) -> selectedChoice[0] = id).setPositiveButton("Optimize", (dialog, id) -> {
                if (successful_call) {
                    String focus;
                    String focusEng;
                    switch (selectedChoice[0]) {
                        default:
                        case 0: //Sustainability and energy-efficiency
                            focus = "Nachhaltigkeit und Energieeffizienz";
                            focusEng = "Sustainability and energy-efficiency";
                            break;
                        case 1: //Clarity and comprehensibility
                            focus = "Klarheit und Verständlichkeit";
                            focusEng = "Clarity and comprehensibility";
                            break;
                        case 2: //Modularity and abstraction
                            focus = "Modularität und Abstraktion";
                            focusEng = "Modularity and abstraction";
                            break;
                    }
                    //Neuer Eintrag in der Ansicht
                    UMLDiagram current = app.data.umlDiagrams.get(app.data.indexOfCurrent);
                    UMLDiagram neu = new UMLDiagram();
                    neu.type = current.type;
                    neu.appliedAdjustment = new ArrayList<>(current.appliedAdjustment);
                    neu.appliedAdjustment.add(("(Optimise) ") + focusEng);
                    app.data.umlDiagrams.add(neu);
                    app.data.indexOfCurrent = app.data.umlDiagrams.size() - 1;

                    sendTextDataToLLMForOptimization(focus);
                    changeButtonForImages();
                    setAdjustment();

                } else {
                    Toast.makeText(getActivity(), id + "You need to make a request before an optimization request!", Toast.LENGTH_LONG).show();
                }
            }).setNegativeButton("Cancel", (dialog, id) -> {
                // User cancels the dialog.
            });
            builder.create().show();
        });

        recommendationBinding.buttonSaveUml.setOnClickListener(v -> {
            if (!successful_call) {
                Toast.makeText(getActivity(), "No Image to Save", Toast.LENGTH_SHORT).show();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            final String[] saveName = new String[1];
            final EditText inputSaveName = new EditText(getContext());
            inputSaveName.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(35);
            inputSaveName.setFilters(filterArray);
            inputSaveName.setHint("Name");

            final String[] description = new String[1];
            final EditText inputDescriptionName = new EditText(getContext());
            inputDescriptionName.setHint("Description (optional)");
            InputFilter[] filterArray2 = new InputFilter[1];
            filterArray2[0] = new InputFilter.LengthFilter(500);
            inputDescriptionName.setFilters(filterArray2);
            inputDescriptionName.setMaxLines(25);

            LinearLayout lila = new LinearLayout(getContext());
            lila.setOrientation(LinearLayout.VERTICAL);
            lila.addView(inputSaveName);
            lila.addView(inputDescriptionName);
            builder.setTitle("Save current image").setMessage("Insert name for saving and a description").setView(lila).setPositiveButton("Confirm", null).setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                saveName[0] = inputSaveName.getText().toString();
                description[0] = inputDescriptionName.getText().toString();
                if (saveName[0].trim().isEmpty()) {
                    inputSaveName.setBackgroundTintList(ContextCompat.getColorStateList(app, R.color.rot_hell));
                    Toast.makeText(getActivity(), "Please add a save name!", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    Uri uri = saveImage(app.data.umlDiagrams.get(app.data.indexOfCurrent).bitmapPlantUML);
                    app.data.writeToHistory(saveName[0], uri, (String) app.data.umlDiagrams.get(app.data.indexOfCurrent).plant_uml_code, description[0]);
                } catch (RuntimeException e) {
                    Toast.makeText(app, "SaveError!", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
            });
        });
        recommendationBinding.checkBox.setOnClickListener(v -> {
            if (recommendationBinding.checkBox.isChecked()) {
                DiffMatchPatch dmp = new DiffMatchPatch();
                LinkedList<DiffMatchPatch.Diff> diffs = dmp.diffMain(app.data.umlDiagrams.get(app.data.indexOfCurrent).plant_uml_code, app.data.umlDiagrams.get(oldIndexOfCurrent).plant_uml_code);
                dmp.diffCleanupSemantic(diffs);
                for (DiffMatchPatch.Diff diff : diffs) {
                    if (diff.operation == DiffMatchPatch.Operation.INSERT || diff.operation == DiffMatchPatch.Operation.DELETE) {
                        setHighLightedText(textView_plantUML, diff.text);
                    }
                }
                // Highlight entfernen
            } else
                textView_plantUML.setText(app.data.umlDiagrams.get(app.data.indexOfCurrent).plant_uml_code);
        });
        recommendationBinding.buttonSendToLlm.setOnClickListener(v -> {

            editText_adjustment.clearFocus();
            if (successful_call) {
                //Neues Objekt erstellen für changes
                UMLDiagram current = app.data.umlDiagrams.get(app.data.indexOfCurrent);
                current.recommendationString = editText_adjustment.getText().toString();
                UMLDiagram neu = new UMLDiagram();
                neu.type = current.type;
                neu.appliedAdjustment = new ArrayList<>(current.appliedAdjustment);
                neu.appliedAdjustment.add(editText_adjustment.getText().toString());
                app.data.umlDiagrams.add(neu);
                app.data.indexOfCurrent = app.data.umlDiagrams.size() - 1;

                String input = "Zu diesem PlantUML: " + current.plant_uml_code + "\n" + editText_adjustment.getText().toString() + ".\nGib mir nur das neue PlantUML zurück!";
                sendTextDataToLLM(input);
                changeButtonForImages();
                setAdjustment();
                editText_adjustment.setText("");
            } else {
                Toast.makeText(getActivity(), "You need to make a request before an adjustment request!", Toast.LENGTH_LONG).show();
            }

        });

        recommendationBinding.cardviewButtonShowPlant.setOnClickListener(v -> {
            ImageView imageButton = view.findViewById(R.id.imageView);
            editText_adjustment.clearFocus();

            if (textView_plantUML.getVisibility() == View.GONE) {
                textView_plantUML.setVisibility(View.VISIBLE);
                imageButton.setRotationX(180);
                // Highlight of changes anzeigen
                if (app.data.umlDiagrams.size() > 1)
                    recommendationBinding.checkBox.setVisibility(View.VISIBLE);
                else recommendationBinding.checkBox.setVisibility(View.GONE);
                textView_plantUML.setText(app.data.umlDiagrams.get(app.data.indexOfCurrent).plant_uml_code);
            } else {
                textView_plantUML.setVisibility(View.GONE);
                recommendationBinding.checkBox.setVisibility(View.GONE);
                recommendationBinding.checkBox.setChecked(false);
                imageButton.setRotationX(0);
            }

        });

        recommendationBinding.cardviewButtonShowAdjustments.setOnClickListener(v -> {
            ImageView imageButton = view.findViewById(R.id.imageView_2);
            if (editText_adjustment.getVisibility() == View.GONE) {
                recommendationBinding.textViewAppliedAdjustments.setVisibility(View.VISIBLE);
                setAdjustment();
                editText_adjustment.setVisibility(View.VISIBLE);
                editText_adjustment.setText(app.data.umlDiagrams.get(app.data.indexOfCurrent).recommendationString);
                imageButton.setRotationX(180);
            } else {
                app.data.umlDiagrams.get(app.data.indexOfCurrent).recommendationString = editText_adjustment.getText().toString();
                editText_adjustment.setVisibility(View.GONE);
                recommendationBinding.textViewAppliedAdjustments.setVisibility(View.GONE);
                imageButton.setRotationX(0);
            }
        });

        recommendationBinding.cardviewButtonShowResponse.setOnClickListener(v -> {
            TextView textView = view.findViewById(R.id.text_response_llm);
            ImageView imageButton = view.findViewById(R.id.imageView_3);
            if (textView.getVisibility() == View.GONE) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(app.data.umlDiagrams.get(app.data.indexOfCurrent).llmResponseString);
                imageButton.setRotationX(180);
            } else {
                textView.setVisibility(View.GONE);
                imageButton.setRotationX(0);
            }
        });

        recommendationBinding.buttonViewLeft.setOnClickListener(v -> {
            if (app.data.indexOfCurrent != 0) {
                oldIndexOfCurrent = app.data.indexOfCurrent;
                app.data.indexOfCurrent--;

            }
            recommendationBinding.imageViewResult.setImageBitmap(app.data.umlDiagrams.get(app.data.indexOfCurrent).bitmapPlantUML);
            // PlantUML neu schreiben, wenn geöffnet
            if (editText_adjustment.getVisibility() == View.VISIBLE) {
                editText_adjustment.setText(app.data.umlDiagrams.get(app.data.indexOfCurrent).recommendationString);
                setAdjustment();
            }


            if (textView_plantUML.getVisibility() == View.VISIBLE)
                textView_plantUML.setText(app.data.umlDiagrams.get(app.data.indexOfCurrent).plant_uml_code);
            if (recommendationBinding.textResponseLlm.getVisibility() == View.VISIBLE)
                recommendationBinding.textResponseLlm.setText(app.data.umlDiagrams.get(app.data.indexOfCurrent).llmResponseString);
            recommendationBinding.checkBox.setChecked(false);
            changeButtonForImages();
        });

        recommendationBinding.buttonViewRight.setOnClickListener(v -> {
            if (app.data.indexOfCurrent + 1 != app.data.umlDiagrams.size()) {
                //app.data.umlDiagrams.get(app.data.indexOfCurrent).recommendationString = editText_adjustment.getText().toString();
                oldIndexOfCurrent = app.data.indexOfCurrent;
                app.data.indexOfCurrent++;
            }
            recommendationBinding.imageViewResult.setImageBitmap(app.data.umlDiagrams.get(app.data.indexOfCurrent).bitmapPlantUML);
            // PlantUML neu schreiben, wenn geöffnet
            if (editText_adjustment.getVisibility() == View.VISIBLE) {
                editText_adjustment.setText(app.data.umlDiagrams.get(app.data.indexOfCurrent).recommendationString);
                setAdjustment();
            }
            if (textView_plantUML.getVisibility() == View.VISIBLE)
                textView_plantUML.setText(app.data.umlDiagrams.get(app.data.indexOfCurrent).plant_uml_code);
            if (recommendationBinding.textResponseLlm.getVisibility() == View.VISIBLE)
                recommendationBinding.textResponseLlm.setText(app.data.umlDiagrams.get(app.data.indexOfCurrent).llmResponseString);
            recommendationBinding.checkBox.setChecked(false);
            changeButtonForImages();
        });

        ImageView image = view.findViewById(R.id.imageView_result);
        image.setImageBitmap(app.data.umlDiagrams.get(app.data.indexOfCurrent).bitmapPlantUML);

        recommendationBinding.imageViewResult.setOnClickListener(view -> {
            DialogFragment dialogFragment = new ImageDialogFragment(image.getDrawable(), app);
            dialogFragment.show(app.getSupportFragmentManager(), "imageDialogFragment");
        });

        //Damit Änderungen nach Seitenwechsel noch möglich
        // Sucessfull-Call weiter outsourcen nach Appdata (maybe)
        if (app.data.calledByGallery) {
            successful_call = true;
        }

        //TestMethdoe();
        if (typeOfCall == 1) {
            sendImageDataToLLM();
        } else if (typeOfCall == 2) {
            sendTextDataToLLM("");
        } else {
            changeVisibility();
            changeButtonForImages();
        }
        return view;
    }

    /**
     * Change the visibility of left/right button
     */
    private void changeButtonForImages() {

        int index = app.data.indexOfCurrent;
        if (index == 0) {
            recommendationBinding.buttonViewLeft.setVisibility(View.INVISIBLE);
        } else {
            recommendationBinding.buttonViewLeft.setVisibility(View.VISIBLE);
        }
        if (index == app.data.umlDiagrams.size() - 1) {
            recommendationBinding.buttonViewRight.setVisibility(View.INVISIBLE);
        } else {
            recommendationBinding.buttonViewRight.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Sending the images to the LLM and handling the response
     */
    public void sendImageDataToLLM() {
        ArrayList<Bitmap> input = new ArrayList<>();

        // Kein Bild ausgewählt
        if (app.data.list_imageUri.isEmpty()) {
            Toast.makeText(app, "No picture selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            for (Uri u : app.data.list_imageUri) {
                input.add(getBitmapFromUri(u));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ILLMService llm = getLLMWithApiKey();
        llm.analyze(input, new ILLMServiceCallback() {
            @Override
            public void onSuccess(String result) {
                app.runOnUiThread(() -> {
                    String codeFromResult = getCodeFromResponse(result);
                    TextView plantUML = view.findViewById(R.id.textView_plantUML);
                    plantUML.setText(codeFromResult);
                    app.data.umlDiagrams.get(app.data.indexOfCurrent).plant_uml_code = codeFromResult;
                    app.data.umlDiagrams.get(app.data.indexOfCurrent).llmResponseString = result;

                    PlantUML plantUMLHandler = new PlantUML(RecommendationFragment.this);
                    String encodedText = "~1" + plantUMLHandler.compress(codeFromResult);
                    try {
                        plantUMLHandler.render(encodedText);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    typeOfCall = 0;
                    successful_call = true;
                });
            }

            @Override
            public void onFailure(Exception e) {
                app.runOnUiThread(() -> {
                    changeVisibility();
                    TextView plantUML = view.findViewById(R.id.editText_adjustment);
                    plantUML.setText(e.getMessage());
                    typeOfCall = 0;
                    app.data.umlDiagrams.get(app.data.indexOfCurrent).llmResponseString = e.getMessage();
                });
            }
        });
    }

    /**
     * Sending improvement categories as text to the LLM
     *
     * @param focus Selected improvement string
     */
    public void sendTextDataToLLMForOptimization(String focus) {
        TextView plantUML = view.findViewById(R.id.textView_plantUML);
        String input = "Du bist ein Softwarearchitekt-Experte und dir liegt eine Softwarearchitektur in Form eines PlantUMLs vor. Du musst die Architektur effizienter mit Hinsicht auf " + focus + " gestalten. Gib mir nur das reine PlantUML zurück ohne Erklärungen.\n" + "PlantUML: \n" + plantUML.getText().toString();
        sendTextDataToLLM(input);
    }

    /**
     * Sending the text to the LLM and handling the response
     *
     * @param input Text for the LLM
     */
    public void sendTextDataToLLM(String input) {
        if (input.isEmpty()) {
            if (!app.data.umlDiagrams.get(app.data.indexOfCurrent).audioString.isEmpty()) {
                input = "Erstelle aus diesem Text PlantUML Code und gib mir nur diesen zurück: " + app.data.umlDiagrams.get(app.data.indexOfCurrent).audioString;
            } else {
                Toast.makeText(app, "No text input!", Toast.LENGTH_SHORT).show();
                return;
            }
        } else changeVisibility();

        ILLMService llm = getLLMWithApiKey();
        llm.analyze(input, new ILLMServiceCallback() {
            @Override
            public void onSuccess(String result) {
                app.runOnUiThread(() -> {

                    // Nur Code aus der Antwort rausschneiden
                    String codeFromResult = getCodeFromResponse(result);
                    TextView plantUML = view.findViewById(R.id.textView_plantUML);
                    plantUML.setText(codeFromResult);
                    app.data.umlDiagrams.get(app.data.indexOfCurrent).plant_uml_code = codeFromResult;
                    app.data.umlDiagrams.get(app.data.indexOfCurrent).llmResponseString = result;

                    PlantUML renderer = new PlantUML(RecommendationFragment.this);
                    String encodedText = "~1" + renderer.compress(codeFromResult);
                    try {
                        renderer.render(encodedText);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // Wenn Ergebnis kommt und vorher Audio Call gemacht wurde
                    if (typeOfCall == 2) successful_call = true;
                    typeOfCall = 0;
                });
            }

            @Override
            public void onFailure(Exception e) {
                app.runOnUiThread(() -> {
                    changeVisibility();
                    app.data.umlDiagrams.get(app.data.indexOfCurrent).llmResponseString = e.getMessage();
                    typeOfCall = 0;
                });
            }
        });
    }

    @Override
    public void onImageRendered(Bitmap bitmap) {
        app.runOnUiThread(() -> {
            ImageView imageView = view.findViewById(R.id.imageView_result);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            app.data.umlDiagrams.get(app.data.indexOfCurrent).bitmapPlantUML = bitmap;
            changeVisibility();
        });
    }

    /**
     * Select the right LLM
     *
     * @return Class that implements the LLM
     */
    private ILLMService getLLMWithApiKey() {
        ILLMService llm;
        try {
//            input = getBitmapFromUri(selectedImageUri);
            String type = app.data.umlDiagrams.get(app.data.indexOfCurrent).type;
            String apiKey = "";
            String url = "UNSET";
            if (app.data.config.has("llmModel")) {
                String currentModel = (String) app.data.config.get("llmModel");
                JSONArray ja = app.data.config.getJSONArray("all_llm_models");

                for (int i = 0; i < ja.length(); i++) {
                    String tmp = ja.getJSONObject(i).get("name").toString();
                    if (tmp.equals(currentModel)) {
                        apiKey = ja.getJSONObject(i).get("apikey").toString();
                        if (currentModel.equals("Endpoint")) {
                            url = ja.getJSONObject(i).get("url").toString();
                        }
                    }
                }
                switch (app.data.config.get("llmModel").toString()) {
                    case ("ChatGPT_3.5 (Text-based)"):
                        llm = LLMServiceFactory.getService(LLMServiceFactory.GPT35, apiKey, type);
                        break;
                    case ("ChatGPT_4.o (Recommended)"):
                        llm = LLMServiceFactory.getService(LLMServiceFactory.GPT4o, apiKey, type);
                        break;
                    case ("Gemini (Recommended)"):
                        llm = LLMServiceFactory.getService(LLMServiceFactory.Gemini, apiKey, type);
                        break;
                    default:
                        llm = LLMServiceFactory.getService(LLMServiceFactory.Endpoint, apiKey, type, url);
                }
            } else {
                llm = LLMServiceFactory.getService(LLMServiceFactory.Endpoint, apiKey, type, url);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return llm;
    }

    /**
     * Cutting the PlantUML code from the result
     *
     * @param result Response from LLM
     * @return Extracted PlantUML code
     */
    private String getCodeFromResponse(String result) {
        result = result.substring(result.indexOf("@startuml"), result.indexOf("@enduml") + 7);
        result = result.replace("\\n", System.lineSeparator());
        result = result.replace("\\", "");
        return result;
    }

    /**
     * Changing the visibility of the loading symbol and result elements
     */
    private void changeVisibility() {
        FrameLayout progressBar = view.findViewById(R.id.progress_layout);
        LinearLayout contentLayout = view.findViewById(R.id.layout_result);
        if (progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Converting a URI into a bitmap
     *
     * @param selectedImageUri Uri of the selected image
     * @return Bitmap of the selected image
     * @throws IOException Exception
     */
    private Bitmap getBitmapFromUri(Uri selectedImageUri) throws IOException {
        InputStream inputStream = requireActivity().getApplicationContext().getContentResolver().openInputStream(selectedImageUri);
        return BitmapFactory.decodeStream(inputStream);
    }

    /**
     * Saving the images in the smartphone memory
     *
     * @param bitmap Bitmap of the selected image
     * @return Uri of the image
     */
    public Uri saveImage(Bitmap bitmap) {

        ContentResolver contentResolver = app.getContentResolver();
        ContentValues values = new ContentValues();
        String filename = String.format("%d.jpg", System.currentTimeMillis());

        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/SoftwareArchitectur");

        Uri uriConent = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uriConent == null) {
            Toast.makeText(app, "Failed to create Uri.", Toast.LENGTH_SHORT).show();
            return null;
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
        return uriConent;
    }


    /**
     * Highlight a text in TextView
     *
     * @param tv              TextView or Edittext or Button (or derived from TextView)
     * @param textToHighlight Text to highlight
     */
    public void setHighLightedText(TextView tv, String textToHighlight) {
        String tvt = tv.getText().toString();
        int ofe = tvt.indexOf(textToHighlight, 0);
        Spannable wordToSpan = new SpannableString(tv.getText());
        for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe + 1) {
            ofe = tvt.indexOf(textToHighlight, ofs);
            if (ofe == -1) break;
            else {
                // set color here
                wordToSpan.setSpan(new BackgroundColorSpan(0xFFFFFF00), ofe, ofe + textToHighlight.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE);
            }
        }
    }

    private void setAdjustment() {
        ArrayList<String> tmp = app.data.umlDiagrams.get(app.data.indexOfCurrent).appliedAdjustment;
        StringBuilder result = new StringBuilder("Applied Adjustments:\n");
        for(String adjustment: tmp){
            result.append(" - ").append(adjustment).append(" \n");
        }
        recommendationBinding.textViewAppliedAdjustments.setText(result);
    }
}