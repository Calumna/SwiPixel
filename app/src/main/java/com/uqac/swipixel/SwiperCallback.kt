package com.uqac.swipixel

import android.view.View

interface SwiperCallback {

    fun onTopCardLiked(data: SwiperData)

    fun onTopCardRejected(data: SwiperData)

    fun onBackwardLikedImage(data: SwiperData)

    fun onBackwardRejectedImage(data: SwiperData)

}