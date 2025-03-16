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

package com.example.a_abb_01project.LLM;

import android.graphics.Bitmap;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.example.a_abb_01project.MainActivity;
import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * Class for management of LLM GPT-4o
 */
public class LLMGPT4o implements ILLMService {

    private static String API_KEY = "";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client;
    private final String classification;

    public LLMGPT4o(String apikey, String tag) {
        this.client = new OkHttpClient();
        API_KEY = apikey;
        this.classification = tag;
    }

    @Override
    public void analyze(String input, ILLMServiceCallback callback) {
        try {
            JSONObject json = createRequestJson(input);
            sendMessage(json, callback);
        } catch (JSONException e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void analyze(ArrayList<Bitmap> input, ILLMServiceCallback callback) {
        try {
            String tmp = "Wandel das " + classification + "-Diagramm in dem Bild in PlantUML um. Gib mir als Antwort nur den PlantUML Code. Wenn in dem Bild kein passendes Diagram erkannt wird, gib 'No fitting Image' als Antwort. ";
            JSONObject json = createRequestJsonWithImage(tmp, input);
            sendMessage(json, callback);
        } catch (JSONException e) {
            callback.onFailure(e);
        }
    }

    /**
     * Create JSON request of string
     *
     * @param input             text input
     * @return                  JSONObject
     * @throws JSONException    Exception
     */
    private JSONObject createRequestJson(String input) throws JSONException {
        // Erstellen des JSON-Objekts
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-4o");

        // Erstellen des Nachrichten-Array
        JSONArray messagesArray = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");

        // Erstellen des Inhalts-Array
        JSONArray contentArray = new JSONArray();

        // Zweiter Inhalt (Text)
        JSONObject textContent = new JSONObject();
        textContent.put("type", "text");
        textContent.put("text", input);
        contentArray.put(textContent);

        // Hinzufügen des Inhalts-Array zur Benutzernachricht
        userMessage.put("content", contentArray);
        messagesArray.put(userMessage);

        // Hinzufügen des Nachrichten-Array zum Haupt-JSON-Objekt
        requestBody.put("messages", messagesArray);

        // Hinzufügen der anderen Parameter
        requestBody.put("temperature", 1);
        requestBody.put("max_tokens", 512);
        requestBody.put("top_p", 1);
        requestBody.put("frequency_penalty", 0);
        requestBody.put("presence_penalty", 0);

        return requestBody;
    }

    /**
     * Create JSON request with images
     *
     * @param input             text input
     * @param bitmaps           bitmap array
     * @return                  JSONObject
     * @throws JSONException    Exception
     */
    private JSONObject createRequestJsonWithImage(String input, ArrayList<Bitmap> bitmaps) throws JSONException {

        // Erstellen des JSON-Objekts
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-4o");

        // Erstellen des Nachrichten-Array
        JSONArray messagesArray = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");

        // Erstellen des Inhalts-Array
        JSONArray contentArray = new JSONArray();

        // Alle Bilder hinzufügen
        for (Bitmap b : bitmaps) {
            // Erster Inhalt (Bild-URL)
            JSONObject imageUrlContent = new JSONObject();
            imageUrlContent.put("type", "image_url");
            JSONObject imageUrl = new JSONObject();
            String base64 = encodeImageToBase64(b);
            imageUrl.put("url", "data:image/jpeg;base64," + base64);
            imageUrlContent.put("image_url", imageUrl);
            contentArray.put(imageUrlContent);
        }

        // Zweiter Inhalt (Text)
        JSONObject textContent = new JSONObject();
        textContent.put("type", "text");
        textContent.put("text", input);
        contentArray.put(textContent);

        // Hinzufügen des Inhalts-Array zur Benutzernachricht
        userMessage.put("content", contentArray);
        messagesArray.put(userMessage);

        // Hinzufügen des Nachrichten-Array zum Haupt-JSON-Objekt
        requestBody.put("messages", messagesArray);

        // Hinzufügen der anderen Parameter
        requestBody.put("temperature", 1);
        requestBody.put("max_tokens", 256);
        requestBody.put("top_p", 1);
        requestBody.put("frequency_penalty", 0);
        requestBody.put("presence_penalty", 0);

        return requestBody;
    }

    /**
     * Send message to llm
     *
     * @param message   message to be send
     * @param callback  Callback function
     */
    private void sendMessage(JSONObject message, ILLMServiceCallback callback) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(message.toString(), JSON);
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String result = response.body().string();

//                        JSONObject jsonObject = new JSONObject(result);
//                        if (jsonObject.has("choice"))
                        if (result.contains("@startuml") && result.contains("@enduml")) {
                            callback.onSuccess(result);
                        } else {
                            callback.onFailure(new IOException("No useful PlantUML response: " + result));
                        }


                    } else {
                        assert response.body() != null;
                        callback.onFailure(new IOException("Unexpected response code: " + response.code() + " " + response.message() + " " + response.body().string()));
                    }
                } finally {
                    response.close();
                }
            }
        });
    }

    /**
     * Encode bitmap image to base64 string
     * @param bitmap    bitmap of image
     * @return          base64 ping
     */
    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


}

