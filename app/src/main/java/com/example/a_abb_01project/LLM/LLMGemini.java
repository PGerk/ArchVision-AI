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

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Class for management of LLM Gemini
 */
public class LLMGemini implements ILLMService {

    private static String API_KEY = "";
    private final String classification;

    GenerativeModel gm;
    GenerativeModelFutures model;


    public LLMGemini(String apikey, String tag) {
        API_KEY = apikey;
        this.classification = tag;
        gm = new GenerativeModel("gemini-1.5-flash", API_KEY);
        model = GenerativeModelFutures.from(gm);
    }

    @Override
    public void analyze(String input, ILLMServiceCallback callback) {
        Content content = new Content.Builder().addText(input).build();
        sendMessage(content, callback);
    }

    @Override
    public void analyze(ArrayList<Bitmap> input, ILLMServiceCallback callback) {
        Content.Builder builder = new Content.Builder().addText("Wandel das " + classification + "-Diagramm in dem Bild in PlantUML um. Gib mir als Antwort nur den PlantUML Code. PlantUML Code startet mit @startuml und endet mit @enduml.");
        for (Bitmap b : input) {
            builder.addImage(b);
        }
        Content content = builder.build();
        sendMessage(content, callback);
    }

    /**
     * Send message to llm
     *
     * @param content   Content
     * @param callback  Callback
     */
    private void sendMessage(Content content, ILLMServiceCallback callback) {
        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                if (resultText.contains("@startuml") && resultText.contains("@enduml")) {
                    callback.onSuccess(resultText);
                } else {
                    callback.onFailure(new IOException("No useful PlantUML response: " + resultText));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Exception e = new Exception(t);
                callback.onFailure(e);
            }
        }, executor);

    }

}
