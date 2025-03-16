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

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Class for the management of photo view
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private final List<Uri> photoList;
    private OnItemClickListener onItemClickListener; // Interface zur Behandlung von Klicks
    private OnItemLongClickListener onItemLongClickListener;

    public PhotoAdapter(List<Uri> photoList) {
        this.photoList = photoList;
    }

    // Schnittstelle zur Behandlung von Klicks
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    /**
     * Set long click listener
     *
     * @param listener  listener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    /**
     * Add an item to the list
     *
     * @param uri   Uri of item
     */
    public void addItem(Uri uri){
        photoList.add(uri);
        notifyItemInserted(photoList.size());
    }

    /**
     * Remove item from the list
     *
     * @param listToBeRemoved   list with items to be removed
     */
    public void removeItemListFromPhotoList(List<Uri> listToBeRemoved){
        //CounterOffSet, da die Liste immer kleiner wird
        for(Uri uri: listToBeRemoved){
            notifyItemRemoved(photoList.indexOf(uri));
            photoList.remove(uri);
            Log.d(String.valueOf(uri), "Position to be removed");
        }
    }

    /**
     * Set on item click listener
     *
     * @param listener  listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_gallery_item, parent, false);
        PhotoViewHolder viewHolder = new PhotoViewHolder(view);
        // Standardwert für das benutzerdefinierte Attribut setzen
        viewHolder.imageView.setTag(R.attr.customAttribute, "true");
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Uri imageUri = photoList.get(position);
        holder.imageView.setImageURI(imageUri);
        // Setze den Standardwert für das benutzerdefinierte Attribut
        holder.imageView.setTag(R.attr.customAttribute, "true");
        holder.resetColorFilter(); // Setze die Farbe zurück
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    // Innere ViewHolder-Klasse, in der die Klickbehandlung implementiert wird
    class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CardView cardView;

        PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_photo);
            cardView = itemView.findViewById(R.id.card_view_photo);

            // Klicklistener für die CardView einrichten
            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && PhotoAdapter.this.onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            });
            cardView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION && PhotoAdapter.this.onItemLongClickListener != null){
                    onItemLongClickListener.onItemLongClick(position);
                    //imageView.setBackgroundColor(itemView.getResources().getColor(android.R.color.holo_red_light));
                    ColorMatrix matrix = new ColorMatrix();
                    Object tag = imageView.getTag(R.attr.customAttribute);
                    if(tag != null && tag.equals("true")){
                        imageView.setTag(R.attr.customAttribute,"false");
                        matrix.setSaturation(0);
                    }else{
                        imageView.setTag(R.attr.customAttribute,"true");
                        matrix.setSaturation(1);
                    }
                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                    imageView.setColorFilter(filter);
                    return true;
                }
                return false;
            });
        }
        private void resetColorFilter() {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(1); // Volle Sättigung
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            imageView.setColorFilter(filter);
        }
    }
}
