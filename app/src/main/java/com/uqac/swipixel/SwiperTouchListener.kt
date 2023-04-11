package com.uqac.swipixel

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class SwiperTouchListener(private val callback: SwiperCallback) : RecyclerView.OnItemTouchListener{

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return true
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) { }
}