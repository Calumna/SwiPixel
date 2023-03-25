package com.uqac.swipixel


import android.net.Uri
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class SwiperAdapter() : RecyclerView.Adapter<SwiperAdapter.ViewHolder>() {

    private val dataList: MutableList<SwiperData> = ArrayList<SwiperData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.swipe_card, parent,false);
        return ViewHolder(itemView);
    }

    override fun getItemCount(): Int {
        return dataList.size;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.imageView.setImageURI(currentItem.image)
    }

    fun addData(data: SwiperData){
        dataList.add(data)
        notifyItemInserted(dataList.lastIndex)
    }

    fun addData(datas : List<SwiperData>){
        val firstInsert = dataList.size
        dataList.addAll(datas)
        notifyItemRangeInserted(firstInsert, dataList.lastIndex)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById<ImageView>(R.id.swipeMedia)
    }
}