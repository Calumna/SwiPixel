package com.uqac.swipixel

import android.view.View

interface SwiperCallback {

    fun onCardActionDown(adapterPosition: Int, card: View)

    fun onCardDrag(adapterPosition: Int, card: View, sideProgress: Float)

    fun onCardActionUp(adapterPosition: Int, card: View, isCardRemove: Boolean)

    fun onCardSwipedLeft(adapterPosition: Int, card: View, notify: Boolean)

    fun onCardSwipedRight(adapterPosition: Int, card: View, notify: Boolean)

    fun onCardLiked(adapterPosition: Int, card: View)

    fun onCardDisliked(adapterPosition: Int, card: View)

    fun onCardLongPress(adapterPosition: Int, card: View)
}