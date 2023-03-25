package com.uqac.swipixel

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class Swiper(context: Context, attrs: AttributeSet?,
             defStyleAttr: Int) : RecyclerView(context, attrs, defStyleAttr){

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    init {
        layoutManager = SwiperLayout(3)
        addOnItemTouchListener(SwiperTouchListener(SwiperCallbackImpl()))
    }

    class SwiperCallbackImpl : SwiperCallback {
        override fun onCardActionDown(adapterPosition: Int, card: View) {
            TODO("Not yet implemented")
        }

        override fun onCardDrag(adapterPosition: Int, card: View, sideProgress: Float) {
            TODO("Not yet implemented")
        }

        override fun onCardActionUp(adapterPosition: Int, card: View, isCardRemove: Boolean) {
            TODO("Not yet implemented")
        }

        override fun onCardSwipedLeft(adapterPosition: Int, card: View, notify: Boolean) {
            //Carte swiper a gauche
        }

        override fun onCardSwipedRight(adapterPosition: Int, card: View, notify: Boolean) {
            // Carte swiper a droite
        }

        override fun onCardLiked(adapterPosition: Int, card: View) {
            TODO("Not yet implemented")
        }

        override fun onCardDisliked(adapterPosition: Int, card: View) {
            TODO("Not yet implemented")
        }

        override fun onCardLongPress(adapterPosition: Int, card: View) {
            TODO("Not yet implemented")
        }

    }
}