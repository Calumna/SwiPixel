package com.uqac.swipixel


import android.net.Uri
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class SwiperAdapter() : RecyclerView.Adapter<SwiperAdapter.ViewHolder>(), SwiperCardCallBack {

    private val dataList: MutableList<SwiperData> = ArrayList<SwiperData>()
    private var attachedRecyclerView: RecyclerView? = null;

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if(attachedRecyclerView == null) attachedRecyclerView = recyclerView
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = SwiperCard(parent.context);
        itemView.acceptButton.setImageResource(R.drawable.round_favorite_24)
        itemView.rejectButton.setImageResource(R.drawable.round_close_24)
        itemView.swiperCardCallBack = this
        return ViewHolder(itemView);
    }

    override fun getItemCount(): Int {
        return dataList.size;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.picture.setImageURI(currentItem.image)
        holder.picture.invalidate()
    }

    fun addData(datas : List<SwiperData>){
        val firstInsert = dataList.size
        dataList.addAll(datas)
        notifyItemRangeInserted(firstInsert, dataList.lastIndex)
    }

    class ViewHolder(itemView: SwiperCard) : RecyclerView.ViewHolder(itemView) {
        val picture: ImageView = itemView.picture
    }

    override fun onCardActionDown(card: SwiperCard) {
        TODO("Not yet implemented")
    }

    override fun onCardActionMove(card: SwiperCard, velocityX: Float) {
        TODO("Not yet implemented")
    }

    override fun onCardActionUp(card: SwiperCard) {
        TODO("Not yet implemented")
    }

    override fun onAcceptButtonClicked(card: SwiperCard) {
        attachedRecyclerView?.apply {
            card.animateSwipe(card.x, width.toFloat() + 10)
            this.requestLayout()
        }
    }

    override fun onRejectButtonClicked(card: SwiperCard) {
        attachedRecyclerView?.apply {
            card.animateSwipe(card.x, -10f - card.width)
            this.requestLayout()
        }
    }

    override fun onCardSwipedRight(card: SwiperCard) {
        attachedRecyclerView?.apply {
            this.requestLayout()
        }
    }

    override fun onCardSwipedLeft(card: SwiperCard) {
        TODO("Not yet implemented")
    }
}