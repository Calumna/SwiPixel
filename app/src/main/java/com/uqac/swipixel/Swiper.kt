package com.uqac.swipixel

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.core.view.setMargins

class Swiper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), SwiperCardCallBack {

    private var activesCards: ArrayList<SwiperCard> = ArrayList()
    private  var recycledCard: ArrayList<SwiperCard> = ArrayList()

    private var deck: ArrayList<SwiperData> = ArrayList()

    private var isCardsCreated: Boolean = false

    //private val adapter = ArrayAdapter<SwiperData>(context, 0)

    var currentIndex: Int = 0
    var maxLoadedCard: Int = 10

    // TODO : placer l'offset (faire attention a la suppression des vue)
    var maxVisibleCard: Int = 4
    var cardOffset: Int = 20

    init {
        setBackgroundColor(Color.parseColor("#ffffff"))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.d("SWIPER", "onLayout")
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        val lp =  LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
        )
        lp.gravity = Gravity.CENTER
        return lp
    }

    private fun bindCard(card: SwiperCard, data: SwiperData){
        card.translationX = 0f
        card.pictureUri = data.image
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    private fun createCard() : SwiperCard{
        val card = SwiperCard(context)
        card.acceptButton.setImageResource(R.drawable.round_favorite_24)
        card.rejectButton.setImageResource(R.drawable.round_close_24)
        card.swiperCardCallBack = this
        return card
    }

    private fun placeCards(){
        val activeCardBeforeRender = activesCards.size
        Log.d("SWIPER", "activeCardBeforeRender : $activeCardBeforeRender")
        // Placement des nouvelles cartes
        var i = 0
        while(activesCards.size < maxLoadedCard && currentIndex + activeCardBeforeRender + i < deck.size){
            val card: SwiperCard = if(recycledCard.isEmpty()){
                Log.d("SWIPER", "Card creation")
                createCard()
            } else {
                Log.d("SWIPER", "Use of recycled Card")
                recycledCard.removeFirst()
            }
            bindCard(card, deck[currentIndex + activeCardBeforeRender + i])
            addView(card)
            activesCards.add(card)
            i++
            card.revertAnim(y - card.height)
        }
    }

    fun addData(swiperData: SwiperData){
        deck.add(swiperData)
        placeCards()
    }

    fun addData(swiperDatas: List<SwiperData>){
        deck.addAll(swiperDatas)
        placeCards()
    }

    fun getCurrentData(): SwiperData = deck[currentIndex]

    // TODO : clearer et rendre propre
    fun revertSwipedCard(){
        if(currentIndex > 0){
            currentIndex--
            placeCards()
        }
    }

    override fun onCardActionDown(card: SwiperCard) {
    }

    override fun onCardActionMove(card: SwiperCard, velocityX: Float) {
        card.translationX += velocityX
    }
    override fun onCardActionUp(card: SwiperCard) {
        // TODO : utiliser left, right, etc. au lieu de width et height
        if(card.x + (card.width/2) > width){
            card.animateSwipe(width + 10f)
            currentIndex++
        } else if (card.x + (card.width/2) < x ){
            card.animateSwipe(x - card.width - 60f)
            currentIndex++
        }
        else {
            card.animateSwipe(-card.translationX)
        }
    }

    override fun onAcceptButtonClicked(card: SwiperCard) {
        // TODO : Refaire les calculs
        card.animateSwipe(width + 10f)
        currentIndex++
    }

    override fun onRejectButtonClicked(card: SwiperCard) {
        // TODO : Corriger les valeurs hardcodé  (prendre en compte les Margin)
        card.animateSwipe(x - card.width - 60f)
        currentIndex++
    }

    override fun onCardSwipedRight(card: SwiperCard) {
        if(card.x + (card.width/2) > width){
            card.animateSwipe(width + 10f)
            currentIndex ++
        }
    }

    override fun onCardSwipedLeft(card: SwiperCard) {
        if(card.x + (card.width/2) < x){
            card.animateSwipe(x - 10f)
            currentIndex++
        }
    }

    // TODO : Si une carte termine son animation et que la suivante effectue la sienne (verifier si le requestLayout fais pas le bordel)
    override fun onEndCardAnimation(card: SwiperCard) {
        if(card.x > width || card.x < x) {
            removeView(card)
            Log.d("SWIPER", "A view was recycled : ${activesCards.remove(card)}")
            recycledCard.add(card)
        }
    }


}