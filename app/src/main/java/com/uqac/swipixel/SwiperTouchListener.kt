package com.uqac.swipixel

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class SwiperTouchListener(private val callback: SwiperCallback) : RecyclerView.OnItemTouchListener{

    private var activePointerId: Int = 0
    private var initialCardPositionY: Float = 0f
    private var initialCardPositionX: Float = 0f

    private var initialTouchX: Float = 0f
    private var initialTouchY: Float = 0f
    private var velocityTracker: VelocityTracker? = null

    private var topCard: View? = null

    private val cardGestureListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1.x > e2.x ) {
                //animateOffScreenLeft(DEFAULT_OFF_SCREEN_FLING_ANIMATION_DURATION, true, false)
                return true
            } else if (e1.x < e2.x) {
                //animateOffScreenRight(DEFAULT_OFF_SCREEN_FLING_ANIMATION_DURATION, true, false)
                return true
            }
            return false
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            //callback.onCardSingleTap(adapterPosition, cardView)
            return super.onSingleTapConfirmed(e)
        }

        override fun onLongPress(e: MotionEvent) {
            //cardCallback.onCardLongPress(adapterPosition, cardView)
            super.onLongPress(e)
        }
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        topCard = rv.getChildAt(0)

        topCard?.let {
            if (e.x > it.left && e.x < it.right
                && e.y > it.top && e.y < it.bottom){
                return true
            }
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

        topCard?.let { card ->
            when (e.actionMasked) {
                // Le doigt touche l'ecran
                MotionEvent.ACTION_DOWN -> {
                    if (velocityTracker == null) {
                        velocityTracker = VelocityTracker.obtain()
                    } else {
                        velocityTracker?.clear()
                    }

                    initialCardPositionX = card.x
                    initialCardPositionY = card.y

                    val pointerIndex = e.actionIndex
                    initialTouchX = e.getX(pointerIndex)
                    initialTouchY = e.getY(pointerIndex)
                    activePointerId = e.getPointerId(0)

                }

                MotionEvent.ACTION_MOVE -> {
                    velocityTracker?.addMovement(e)

                    val pointerId = e.getPointerId(e.actionIndex)
                    velocityTracker?.computeCurrentVelocity(pointerId, 40f)
                    val pointerIndex = e.findPointerIndex(activePointerId)

                    if (pointerIndex != -1) {
                        val dx = e.getX(pointerIndex) - initialTouchX
                        val dy = e.getY(pointerIndex) - initialTouchY

                        velocityTracker?.let {
                            val posX = (card.x + dx) + abs(it.getXVelocity(pointerId))
                            val posY = (card.y + dy) + abs(it.getYVelocity(pointerId))
                            card.x = posX
                            card.y = posY
                        }
                    }

                    // Use to redraw only
                    rv.invalidate()
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    activePointerId = MotionEvent.INVALID_POINTER_ID

                    // use tou Lay out the view
                    rv.requestLayout()
                }
            }
        }
        }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) { }
}