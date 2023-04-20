package com.uqac.swipixel

interface SwiperCardCallBack {

    fun onAcceptButtonClicked(card: SwiperCard)

    fun onRejectButtonClicked(card: SwiperCard)

    fun onCardStartedSwiping(card: SwiperCard)

    fun onCardSwipingRight(card: SwiperCard, progress: Float,velocity: Float)

    fun onCardSwipingLeft(card: SwiperCard, progress: Float, velocity: Float)

    fun onCardSwipedRight(card: SwiperCard)

    fun onCardSwipedLeft(card: SwiperCard)
}
