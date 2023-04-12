package com.uqac.swipixel

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.setMargins

class SwiperCard(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : CardView(context, attrs, defStyleAttr) {

    var acceptButton: ImageButton
    var  rejectButton: ImageButton
    private val picture: ImageView

    var pictureUri: Uri? = null
        set(value) {
            field = value
            picture.setImageURI(field)
        }


    var swiperCardCallBack: SwiperCardCallBack? = null

    private var velocityTracker: VelocityTracker? = null

    init {
        picture = ImageView(context)
        acceptButton = ImageButton(context)
        rejectButton = ImageButton(context)

        radius = 25f

        // TODO : Je sais pas pq generateDefaultLayoutParam ne fonctionne pas
        val lp = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        lp.setMargins(50)
        layoutParams = lp

        createChildsIds()
        setChildrenLayoutParams()
        setImagesConstraint();
        setImagesButtonsCallback()
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        val lp = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        lp.setMargins(50)
        return lp
    }

    private fun setImagesButtonsCallback() {
        acceptButton.setOnClickListener {
            swiperCardCallBack?.onAcceptButtonClicked(this)
        }

        rejectButton.setOnClickListener {
            swiperCardCallBack?.onRejectButtonClicked(this)
        }
    }

    private fun setChildrenLayoutParams(){
        val pictureLayoutParams = LayoutParams(0,0)
        picture.layoutParams = pictureLayoutParams

        val acceptButtonLayoutParams = LayoutParams(150 , 150)
        acceptButton.scaleType = ImageView.ScaleType.FIT_CENTER
        acceptButton.layoutParams = acceptButtonLayoutParams

        val rejectButtonLayoutParams = LayoutParams(150, 150)
        rejectButton.scaleType = ImageView.ScaleType.FIT_CENTER
        rejectButton.layoutParams = rejectButtonLayoutParams

    }

    private fun createChildsIds(){
        // Creation of the ids of the child views
        picture.id = View.generateViewId()
        acceptButton.id = View.generateViewId()
        rejectButton.id = View.generateViewId()
    }

    private fun setImagesConstraint(){
        val layout = ConstraintLayout(context)

        layout.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )

        val constraintSet = ConstraintSet()

        layout.addView(picture)
        layout.addView(rejectButton)
        layout.addView(acceptButton)

        constraintSet.clone(layout)

        // Constraint for the picture
        constraintSet.connect(picture.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 32 )
        constraintSet.connect(picture.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 32 )
        constraintSet.connect(picture.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 32 )
        constraintSet.connect(picture.id, ConstraintSet.BOTTOM, acceptButton.id, ConstraintSet.TOP, 32 )

        // Constraints for acceptButton
        constraintSet.connect(rejectButton.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        constraintSet.connect(rejectButton.id, ConstraintSet.END, acceptButton.id, ConstraintSet.START, 0 )
        constraintSet.connect(rejectButton.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 32 )

        // Constraints for rejectButton
        constraintSet.connect(acceptButton.id, ConstraintSet.START, rejectButton.id, ConstraintSet.END, 0 )
        constraintSet.connect(acceptButton.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0 )
        constraintSet.connect(acceptButton.id, ConstraintSet.TOP, rejectButton.id, ConstraintSet.TOP, 0 )
        constraintSet.connect(acceptButton.id, ConstraintSet.BOTTOM, rejectButton.id, ConstraintSet.BOTTOM, 0 )

        constraintSet.createHorizontalChain(
            ConstraintSet.PARENT_ID,
            ConstraintSet.LEFT,
            ConstraintSet.PARENT_ID,
            ConstraintSet.RIGHT,
            intArrayOf(rejectButton.id, acceptButton.id),
            null,
            ConstraintSet.CHAIN_SPREAD
        )

        constraintSet.applyTo(layout)
        addView(layout)
    }

    // TODO : Clearer et rendre plus propre
    fun animateSwipe(to: Float) {
        val animaton = ObjectAnimator.ofFloat(this, "translationX", translationX + to).apply {
            duration = 1000
            start()
        }

        animaton.addListener(object : AnimatorListener{
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}

            override fun onAnimationEnd(p0: Animator) {
                swiperCardCallBack?.onEndCardAnimation(this@SwiperCard)
            }

        })
    }

    // TODO : rendre plus propre
    fun revertAnim(from: Float){
        this.translationY = from
        ObjectAnimator.ofFloat(this, "translationY", 0f).apply {
            duration = 300
            start()
        }
    }

    //fun animateSwipeLeft(from: Float, to: Float)

    private val cardGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        // TODO : A modifier pour que ce soit plus propre
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1.x > e2.x ) {
                swiperCardCallBack?.onCardSwipedLeft(this@SwiperCard)
                Log.d("val", "SwipedLeft")
                return true
            } else if (e1.x < e2.x) {
                swiperCardCallBack?.onCardSwipedRight(this@SwiperCard)
                Log.d("val", "SwipedRight")
                return true
            }
            return false
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            // Le doigt touche l'ecran
            MotionEvent.ACTION_DOWN -> {
                velocityTracker?.clear()
                velocityTracker = velocityTracker ?: VelocityTracker.obtain()
                swiperCardCallBack?.onCardActionDown(this)
            }

            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.let {
                    val pointerId = e.getPointerId(e.actionIndex)
                    it.addMovement(e)
                    it.computeCurrentVelocity(50)
                    swiperCardCallBack?.onCardActionMove(this, it.getXVelocity(pointerId))
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                velocityTracker?.recycle()
                velocityTracker = null
                swiperCardCallBack?.onCardActionUp(this)
            }
            else -> super.onTouchEvent(e)
        }
        return true
    }


}

