package com.uqac.swipixel

interface SwiperCardCallBack {

    fun onCardActionDown(card: SwiperCard)

    fun onCardActionMove(card: SwiperCard, velocityX: Float)

    fun onCardActionUp(card: SwiperCard)

    fun onAcceptButtonClicked(card: SwiperCard)

    fun onRejectButtonClicked(card: SwiperCard)

    fun onCardSwipedRight(card: SwiperCard)

    fun onCardSwipedLeft(card: SwiperCard)

}
