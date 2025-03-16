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
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class for storing data and configurations
 */
public class AppData {
    List<Uri> list_imageUri;
    JSONObject config;
    JSONObject history;
    MainActivity app;
    ArrayList<String> audioStrings;
    Boolean calledByGallery = false;

    ArrayList<UMLDiagram> umlDiagrams = new ArrayList<>();

    String audioTextfield = "";
    int indexOfCurrent = 0;

    public AppData(MainActivity app) {
        this.app = app;
        list_imageUri = new ArrayList<>();
        audioStrings = new ArrayList<>();

        //Erste Klasse hinzufügen
        UMLDiagram current = new UMLDiagram();
        umlDiagrams.add(current);

        checkForJson("config.json");
        checkForJson("history.json");
        readData("config.json");
        readData("history.json");
    }

    /**
     * Check whether JSON file exists or needs to be recreated
     *
     * @param filename  Filename of the JSON file
     */
    public void checkForJson(String filename) {
        File file = new File(app.getFilesDir(), filename);

        if (file.exists()) {
            // JSON-Datei existiert, sie kann geladen werden
            try {
                FileInputStream fis = app.openFileInput(filename);
                // Hier JSON-Datei laden und verarbeiten (siehe Schritt 2)
                fis.close();
            } catch (IOException e) {
                Log.e("Fehler:", Objects.requireNonNull(e.getMessage()));
            }
        } else {
            // JSON-Datei existiert nicht, sie muss erstellt und gespeichert werden
            try {
                // Beispielinhalt für die JSON-Datei
                JSONObject jsonContent = new JSONObject();
                //Default setzen der Daten
                if (filename.equals("history.json")) {
                    jsonContent.put("entries", new JSONArray());
                }
                if (filename.equals("config.json")) {
                    jsonContent.put("llmModel", "ChatGPT_4.o (Recommended)");
                    JSONArray ja = new JSONArray();
                    JSONObject jo1 = new JSONObject();
                    jo1.put("name", "ChatGPT_4.o (Recommended)");
                    jo1.put("apikey", "");
                    jo1.put("numberOfImages", "2");
                    ja.put(0, jo1);

                    JSONObject jo4 = new JSONObject();
                    jo4.put("name", "Gemini (Recommended)");
                    jo4.put("apikey", "");
                    jo4.put("numberOfImages", "2");
                    ja.put(1, jo4);

                    JSONObject jo2 = new JSONObject();
                    jo2.put("name", "ChatGPT_3.5 (Text-based)");
                    jo2.put("apikey", "");
                    jo2.put("numberOfImages", "0");
                    ja.put(2, jo2);
                    JSONObject jo3 = new JSONObject();
                    jo3.put("name", "Endpoint");
                    jo3.put("apikey", "");
                    //Added endpoint URL
                    jo3.put("url", "URL");
                    jo3.put("numberOfImages", "2");
                    ja.put(3, jo3);

                    jsonContent.put("all_llm_models", ja);
                }

                // JSON-Datei speichern
                FileOutputStream fos = app.openFileOutput(filename, Context.MODE_PRIVATE);
                fos.write(jsonContent.toString().getBytes());
                fos.close();
            } catch (JSONException | IOException e) {
                Log.e("Fehler:", Objects.requireNonNull(e.getMessage()));
            }
        }
    }

    /**
     * Reading in a JSON file
     *
     * @param filename  Filename of the JSON file
     */
    public void readData(String filename) {
        try {
            FileInputStream fis = app.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            fis.close();
            // JSON-String aus der Datei lesen
            String jsonString = stringBuilder.toString();
            // JSON-Objekt erstellen
            JSONObject jsonObject = new JSONObject(jsonString);
            switch (filename) {
                case "config.json":
                    config = jsonObject;
                    break;
                case "history.json":
                    history = jsonObject;
                    break;
            }
        } catch (IOException | JSONException e) {
            Log.e("Fehler:", Objects.requireNonNull(e.getMessage()));
        }
    }

    /**
     * Saving the config
     */
    public void saveConfig() {
        try {
            FileOutputStream fos = app.openFileOutput("config.json", Context.MODE_PRIVATE);
            fos.write(config.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e("Fehler:", Objects.requireNonNull(e.getMessage()));
        }
    }

    /**
     * Saving the history
     */
    public void saveHistory() {
        try {
            FileOutputStream fos = app.openFileOutput("history.json", Context.MODE_PRIVATE);
            fos.write(history.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e("Fehler:", Objects.requireNonNull(e.getMessage()));
        }
    }

    /**
     * Write history data
     *
     * @param name          Name of the project
     * @param uri           Uri of the UML image
     * @param plantuml      PlantUML code for the image
     * @param description   Description of the project
     */
    public void writeToHistory(String name, Uri uri, String plantuml, String description) {
        try {
            JSONArray entriesArray;
            if (history.has("entries")) {
                entriesArray = history.getJSONArray("entries");
            } else {
                entriesArray = new JSONArray();
            }

            // Neues JSON-Objekt für den Eintrag erstellen
            JSONObject entry = new JSONObject();
            entry.put("name", name);
            entry.put("uri", uri.toString());
            entry.put("plantuml", plantuml);
            entry.put("description", description);

            // Neuen Eintrag zum Array hinzufügen
            entriesArray.put(entry);

            // Aktualisierte JSON-Daten speichern
            history.put("entries", entriesArray);
            FileOutputStream fos = app.openFileOutput("history.json", Context.MODE_PRIVATE);
            fos.write(history.toString().getBytes());
            fos.close();
        } catch (IOException | JSONException e) {
            Log.e("Fehler:", Objects.requireNonNull(e.getMessage()));
        }
    }

    /**
     * Delete JSON file
     *
     * @param filename  File name of the file to be deleted
     */
    public void deleteFile(String filename) {
        File file = new File(app.getFilesDir(), filename);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                Log.i("AppData", "File deleted successfully: " + filename);
            } else {
                Log.e("AppData", "Failed to delete file: " + filename);
            }
        } else {
            Log.i("AppData", "File does not exist: " + filename);
        }
    }

    /**
     * Reset AppData for a new project
     */
    public void resetAppData() {
        list_imageUri = new ArrayList<>();
        audioStrings = new ArrayList<>();
        umlDiagrams = new ArrayList<>();
        UMLDiagram current = new UMLDiagram();
        umlDiagrams.add(current);
        audioTextfield = "";
        indexOfCurrent = 0;
        calledByGallery = false;
    }
}