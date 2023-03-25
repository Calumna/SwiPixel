package com.uqac.swipixel

import android.content.Context
import android.os.Debug
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import androidx.annotation.Px
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwiperLayout(private val maxUnderCardVisible: Int) : RecyclerView.LayoutManager() {

    private var swipedImageCount: Int = 0

    private val parentTop: Int
        get() = paddingTop

    private val parentBottom: Int
        get() = height - paddingBottom

    private val parentLeft: Int
        get() = paddingLeft

    private val parentRight: Int
        get() = width - paddingRight

    class LayoutParams : RecyclerView.LayoutParams {
        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ViewGroup.MarginLayoutParams) : super(source)
    }

    private fun View.layoutParams(): LayoutParams =
        layoutParams as LayoutParams

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.MATCH_PARENT
        )

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): RecyclerView.LayoutParams {
        val lp = super.generateLayoutParams(lp)
        return LayoutParams(lp)
    }

    override fun generateLayoutParams(
        c: Context?,
        attrs: AttributeSet?
    ): RecyclerView.LayoutParams {
        val lp =  super.generateLayoutParams(c, attrs)
        return LayoutParams(lp)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

        if(childCount > 0){
            var i = 0
            do {

                val top = getChildAt(i)
                top?.let {
                    //Log.i("CHILD", "${top.x} ${top.y} ${top.x + top.width} ${top.y + top.height}" )
                    if((top.x + top.width) < parentLeft || top.x > parentRight
                        || (top.y + top.height) < parentTop  || top.y > parentBottom){
                        removeAndRecycleView(it, recycler)
                        resetCard(it)
                        swipedImageCount++
                    } else {
                        i = childCount
                    }
                }
                i++
            } while(i < childCount)
        }


        detachAndScrapAttachedViews(recycler)
        if (state.itemCount <= 0) return
        fill(recycler, state.itemCount)
    }

    private fun fill(recycler: RecyclerView.Recycler, itemCount: Int) {

        val visibleUnderCardSize: Int = 20

        for(i in swipedImageCount until itemCount){
            val view = recycler.getViewForPosition(i) as CardView

            val index = i - swipedImageCount


            val heightOffset = if(index <  maxUnderCardVisible) index * visibleUnderCardSize else maxUnderCardVisible * visibleUnderCardSize
            view.cardElevation = if( index < maxUnderCardVisible) (maxUnderCardVisible - index) * 15f else 0f
            view.translationX = 0f
            view.translationY = 0f
            addView(view, index)
            measureChildWithMargins(view, 0,maxUnderCardVisible * visibleUnderCardSize)

            //val heightOffset = if(i <=  maxUnderCardVisible) i * visibleUnderCardSize else  maxUnderCardVisible * visibleUnderCardSize

            layoutDecoratedWithMargins(view,
                parentLeft,
                parentTop + heightOffset,
                parentRight,
                parentBottom + heightOffset - (maxUnderCardVisible * visibleUnderCardSize) )
        }
    }

    private fun resetCard(view: View){
    }


}