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

package com.example.a_abb_01project.diagram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.Deflater;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class PlantUML implements DiagramRenderer {

    private static final String API_URL = "https://www.plantuml.com/plantuml/png/";
    private final OkHttpClient client;
    private final OnImageRenderedCallback callback;

    public PlantUML(OnImageRenderedCallback callback) {
        this.client = new OkHttpClient();
        this.callback = callback;
    }

    @Override
    public void render(String plantUmlString) throws IOException, InterruptedException {
        Request request = new Request.Builder()
                .url(API_URL + plantUmlString)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Fehler:", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("Error:", "Unexpected code " + response);
                    return;
                }

                assert response.body() != null;
                byte[] imageData = response.body().bytes();
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                callback.onImageRendered(bitmap);
            }
        });

    }

    public interface OnImageRenderedCallback {
        void onImageRendered(Bitmap bitmap);
    }

    public String compress(String s) {
        // UTF-8 Encoding
        s = encodeUTF8(s);
        try {
            // Deflate Compression
            byte[] compressedBytes = deflate(s);
            // Base64 Encoding
            return encode64(compressedBytes);
        } catch (IOException e) {
            Log.e("Fehler:", Objects.requireNonNull(e.getMessage()));
            return null;
        }
    }

    private static String encodeUTF8(String s) {
        return new String(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

    private static byte[] deflate(String s) throws IOException {
        byte[] inputBytes = s.getBytes(StandardCharsets.UTF_8);
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(inputBytes);
        deflater.finish();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(inputBytes.length);

        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            byteArrayOutputStream.write(buffer, 0, count);
        }
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    private static String encode64(byte[] data) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < data.length; i += 3) {
            if (i + 2 == data.length) {
                r.append(append3bytes(data[i], data[i + 1], (byte) 0));
            } else if (i + 1 == data.length) {
                r.append(append3bytes(data[i], (byte) 0, (byte) 0));
            } else {
                r.append(append3bytes(data[i], data[i + 1], data[i + 2]));
            }
        }
        return r.toString();
    }

    private static String append3bytes(byte b1, byte b2, byte b3) {
        int c1 = (b1 >> 2) & 0x3F;
        int c2 = ((b1 & 0x3) << 4) | ((b2 >> 4) & 0xF);
        int c3 = ((b2 & 0xF) << 2) | ((b3 >> 6) & 0x3);
        int c4 = b3 & 0x3F;

        return "" + encode6bit(c1) + encode6bit(c2) + encode6bit(c3) + encode6bit(c4);
    }

    private static char encode6bit(int b) {
        if (b < 10) {
            return (char) (48 + b);
        }
        b -= 10;
        if (b < 26) {
            return (char) (65 + b);
        }
        b -= 26;
        if (b < 26) {
            return (char) (97 + b);
        }
        b -= 26;
        if (b == 0) {
            return '-';
        }
        if (b == 1) {
            return '_';
        }
        return '?';
    }
}
