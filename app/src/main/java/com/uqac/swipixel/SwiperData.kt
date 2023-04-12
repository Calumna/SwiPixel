package com.uqac.swipixel

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SwiperData(var image : Uri): Parcelable