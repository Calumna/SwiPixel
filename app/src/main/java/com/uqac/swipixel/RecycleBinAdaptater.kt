package com.uqac.swipixel

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class RecycleBinAdaptater(private val deleteImages : List<SwiperData>) : RecyclerView.Adapter<RecycleBinAdaptater.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.image_item_bin, parent,false);
        return ViewHolder(itemView);
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = deleteImages[position]
        holder.imageView.setImageURI(currentItem.image)
    }

    override fun getItemCount(): Int {
        return deleteImages.size
    }

 /*   fun addData(datas : List<SwiperData>){
        val firstInsert = deleteImages.size
        deleteImages.addAll(datas)
        notifyItemRangeInserted(firstInsert, deleteImages.lastIndex)
    }*/

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById<ImageView>(R.id.image_bin)
    }
}