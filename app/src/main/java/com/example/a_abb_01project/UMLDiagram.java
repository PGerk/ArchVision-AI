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

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;

/**
 * Model class for one project
 */
public class UMLDiagram {
    String type = "";
    Bitmap bitmapPlantUML;
    ArrayList<Uri> uri_imagelist = new ArrayList<>();
    String plant_uml_code = "No Code";
    String recommendationString = "";

    ArrayList<String> appliedAdjustment = new ArrayList<>();


    String llmResponseString = "No Response";
    String audioString = "";

    public UMLDiagram() {
        bitmapPlantUML = Bitmap.createBitmap(200, 200, Bitmap.Config.ALPHA_8);
    }
}
