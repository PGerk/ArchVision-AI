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
/**
 * Factory class for llms
 */
public class LLMServiceFactory {
    public static final int GPT35 = 1;
    public static final int Endpoint = 2;

    public static final int GPT4o = 3;

    public static final int Gemini = 4;
    //Add LLMs as desired/needed

    /**
     * Return service
     *
     * @param serviceType   serviceType
     * @param apikey        apikey of llm
     * @param tag           classification tag
     * @return              return llm
     */
    public static ILLMService getService(int serviceType, String apikey, String tag) {
        switch (serviceType) {
            case GPT35:
                return new LLMGPT35(apikey, tag);
            case Endpoint:
                return new LLMBakllava(apikey, tag, "UNSET");
            case GPT4o:
                return new LLMGPT4o(apikey, tag);
            case Gemini:
                return new LLMGemini(
                        apikey, tag);
            default:
                throw new IllegalArgumentException("Invalid LLM service");
        }
    }

    /**
     * Return service
     *
     * @param serviceType   serviceType
     * @param apikey        apikey of llm
     * @param tag           classification tag
     * @param url           url for hugging face
     * @return              return llm
     */
    public static ILLMService getService(int serviceType, String apikey, String tag, String url) {
        switch (serviceType) {
            case GPT35:
                return new LLMGPT35(apikey, tag);
            case Endpoint:
                return new LLMBakllava(apikey, tag, url);
            case GPT4o:
                return new LLMGPT4o(apikey, tag);
            case Gemini:
                return new LLMGemini(apikey, tag);
            default:
                throw new IllegalArgumentException("Invalid LLM service");
        }
    }
}