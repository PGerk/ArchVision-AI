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

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
 * Class for management of LLM GPT 3.5
 */
public class LLMGPT35 implements ILLMService {
    private static String API_KEY = "";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final int MAX_RETRIES = 5;
    private final OkHttpClient client;
    private final String classification;


    public LLMGPT35(String apikey, String tag) {
        this.client = new OkHttpClient();
        this.API_KEY = apikey;
        this.classification = tag;
    }

    @Override
    public void analyze(String input, ILLMServiceCallback callback) {
        try {
            // Generate request JSON
            JSONObject json = createRequestJson(input);
            sendMessage(json, callback, 0);
        } catch (JSONException e) {
            callback.onFailure(e);
        }

    }

    public void analyze(ArrayList<Bitmap> input, ILLMServiceCallback callback) {

    }

    /**
     * Send message to llm
     *
     * @param message       JSONObject message
     * @param callback      callback function
     * @param retryCount    retry counter
     */
    private void sendMessage(JSONObject message, ILLMServiceCallback callback, int retryCount) {

        //Generate request body
        RequestBody body = RequestBody.create(message.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (retryCount < MAX_RETRIES) {
                    int backoff = (int) Math.pow(2, retryCount) * 1000;
                    try {
                        Thread.sleep(backoff);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        callback.onFailure(ex);
                        return;
                    }
                    sendMessage(message, callback, retryCount + 1);
                } else {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String result = response.body().string();
                        callback.onSuccess(result);
                    } else if (response.code() == 429 && retryCount < MAX_RETRIES) {
                        //Exponential backoff
                        int backoff = (int) Math.pow(2, retryCount) * 1000;
                        try {
                            Thread.sleep(backoff);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            callback.onFailure(e);
                            return;
                        }
                        sendMessage(message, callback, retryCount + 1);
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
     * Create JSON request of string
     *
     * @param input             text input
     * @return                  JSONObject
     * @throws JSONException    Exception
     */
    private JSONObject createRequestJson(String input) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("model", "gpt-3.5-turbo");
        json.put("messages", new JSONArray()
                .put(new JSONObject().put("role", "system").put("content", "You are an expert in software architecture and UML diagrams."))
                .put(new JSONObject().put("role", "user").put("content", "Analyze the following UML diagram for its architecture, efficiency, and correctness:\n\n" + input))
        );
        return json;
    }
}
