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

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UMLAdapter extends RecyclerView.Adapter<UMLAdapter.PhotoViewHolder> {

    List<JSONObject> umlList;
    private OnItemClickListener onItemClickListener; // Interface zur Behandlung von Klicks

    public UMLAdapter() {
        umlList = new ArrayList<>();
    }

    // Schnittstelle zur Behandlung von Klicks
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void addItem(JSONObject json) throws JSONException {
        umlList.add(json);
        notifyItemInserted(umlList.size());
    }

    /**
     * Set click listener
     *
     * @param listener  listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_uml_diagram, parent, false);
        // Standardwert für das benutzerdefinierte Attribut setzen
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        JSONObject jsonObject = umlList.get(position);
        Uri uri;
        String name;
        try {
            uri = Uri.parse((String) jsonObject.get("uri"));
            name = (String) jsonObject.get("name");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        holder.imageView.setImageURI(uri);
        holder.textView_name.setText(name);
    }

    /**
     * Remove an item from the history
     *
     * @param position  position of the item
     */
    public void removeItemFromHistoryView(int position) {
        //Entfernen eines einzelnen Items aus der RecyclerView
        notifyItemRemoved(position);
        umlList.remove(position);
    }

    @Override
    public int getItemCount() {
        return umlList.size();
    }


    // Innere ViewHolder-Klasse, in der die Klickbehandlung implementiert wird
    class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CardView cardView;
        TextView textView_name;

        PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_uml);
            cardView = itemView.findViewById(R.id.cardView_uml);
            textView_name = itemView.findViewById(R.id.textView_name);


            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && UMLAdapter.this.onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            });

            // Klicklistener für die CardView einrichten

        }
    }
}