package com.uqac.swipixel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart

class Swiper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), SwiperCardCallBack {

    private var activesCards: ArrayList<SwiperCard> = ArrayList()
    private var recycledCard: ArrayList<SwiperCard> = ArrayList()
    private var dyingCards: Int = 0

    private var deck: ArrayList<SwipedData> = ArrayList()
    private val animatorMap: HashMap<SwiperCard, Animator> = HashMap()
    // var deletedImages: ArrayList<SwiperData>  = ArrayList()

    var currentIndex: Int = 0
    var maxLoadedCard: Int = 10

    // TODO : placer l'offset (faire attention a la suppression des vue)
    var maxVisibleCard: Int = 4
    var cardOffset: Int = 20

    /**
     * Génération de paramatres par défaut
     */
    override fun generateDefaultLayoutParams(): LayoutParams {
        val lp =  LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
        )
        lp.gravity = Gravity.CENTER
        return lp
    }

    /**
     * Méthodes publiques
     */

    fun addData(swiperData: SwiperData){
        deck.add(SwipedData(swiperData, UNDEFINED))
        checkLoadedCard()
    }

    fun addData(swiperDatas: List<SwiperData>){
        deck.addAll(swiperDatas.map {
            SwipedData(it, UNDEFINED)
        })
        checkLoadedCard()
    }
    fun getCurrentData(): SwiperData = deck[currentIndex].data

    fun getAllFlaggedData(flag: Int = -1): List<SwiperData> {
        return when(flag){
            LIKED, REJECTED -> {
                deck.subList(0, currentIndex).filter { it.swipe == flag }.map { it.data }
            }
            UNDEFINED -> {
                deck.subList(currentIndex, deck.size).map { it.data }
            }

            else -> deck.map { it.data }
        }
    }

    fun swipeRightCurrentCard(){
        startSlideAnimator(activesCards[dyingCards], right + 10f)
    }

    fun swipeLeftCurrentCard(){
        startSlideAnimator(activesCards[dyingCards], -(right + 10f))
    }

    fun backwardPreviousCard(){
        if(currentIndex > 0) {
            Log.d("SWIPER", "Backward a card")
            currentIndex--
            if (dyingCards > 0) {
                Log.d("SWIPER", "\t--> Use of dying Card")
                animatorMap[activesCards[dyingCards - 1]]?.let {
                    it.cancel()
                    startComeBackAnimator(activesCards[dyingCards - 1])
                    dyingCards--
                }

            } else {
                val card = if (recycledCard.size > 0) {
                    Log.d("SWIPER", "\t--> Use of Recycled Card")
                    recycledCard.removeFirst().let {
                        addView(it, childCount)
                        it
                    }
                }
                else {
                    Log.d("SWIPER", "\t--> Use of last Active Card")
                    activesCards.removeLast().let {
                        it.bringToFront()
                        it
                    }
                }
                bindCard(card, deck[currentIndex].data)
                when (deck[currentIndex].swipe) {
                    LIKED -> {
                        card.translationX = 150f
                    }

                    REJECTED -> {
                        card.translationX = -150f
                    }
                }
                activesCards.add(0, card)
                startComeBackAnimator(card)
            }
        }
        deck[currentIndex].swipe = UNDEFINED
    }

    /**
     * Methodes de recyclage
     */
    private fun removeAndRecycleView(card: SwiperCard){
        activesCards.remove(card)
        removeView(card)
        recycledCard.add(card)
        resetCard(card)
        Log.d("SWIPER","Remove and Recyle of a card : ${activesCards.size} ${recycledCard.size}")
    }

    private fun resetCard(card: SwiperCard){
        card.translationX = 0f
    }

    private fun bindCard(card: SwiperCard, data: SwiperData){
        card.pictureUri = data.image
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    private fun createCard() : SwiperCard{
        val card = SwiperCard(context)
        card.acceptButton.setImageResource(R.drawable.favorite)
        card.acceptButton.setBackgroundResource(android.R.color.transparent)

        card.rejectButton.setImageResource(R.drawable.cancel)
        card.rejectButton.setBackgroundResource(android.R.color.transparent)

        card.swiperCardCallBack = this
        return card
    }

    /**
     * Definition des animation de cartes
     */
    private fun startSlideAnimator(card: SwiperCard, finalDest: Float){

        val anim = ObjectAnimator.ofFloat(card, "translationX", finalDest)
        anim.duration = 300

        anim.addListener(object : AnimatorListenerAdapter() {
            var onCancel = false
            override fun onAnimationStart(animation: Animator) {
                card.isEnabled = false
                animatorMap[card] = animation
                dyingCards++
                currentIndex++
                Log.d("SWIPER", "A anitmator was started")
            }
            override fun onAnimationEnd(animation: Animator) {
                Log.d("SWIPER", "A anitmator has ended")
                if(!onCancel){
                    removeAndRecycleView(card)
                    dyingCards--
                    checkLoadedCard()
                }
                animatorMap.remove(card)
                card.isEnabled = true
            }

            override fun onAnimationCancel(animation: Animator) {
                onCancel = true
                Log.d("SWIPER", "A anitmator was cancelled")
            }
        })

        anim.start()
    }

    private fun startComeBackAnimator(card: SwiperCard){
        val anim = ObjectAnimator.ofFloat(card, "translationX", 0f)
        anim.duration = 300
        anim.doOnStart { card.isEnabled = false }
        anim.doOnEnd { card.isEnabled = true }
        anim.start()
    }

    /**
     *  Verification des et chargement de cartes si nécessaire
     */
    private fun checkLoadedCard() {
        var card = loadNextCard()
        while(card != null){
            activesCards.add(card)
            addView(card, 0)
            card = loadNextCard()
        }
    }

    private fun loadNextCard(): SwiperCard? {
        return if ((currentIndex + (activesCards.size - dyingCards) < deck.size) && (activesCards.size < maxLoadedCard)) {
            Log.d("SWIPER", "A Card is Loaded")
            val card = if (recycledCard.size > 0) {
                Log.d("SWIPER", "\t--> A recyled card is used")
                recycledCard.removeFirst()
            }else {
                Log.d("SWIPER", "\t--> A card is created")
                createCard()
            }
            bindCard(card, deck[currentIndex + (activesCards.size - dyingCards)].data)
            card
        } else null
    }


    /*
     * Implmentation des callback de cartes
     */
    override fun onAcceptButtonClicked(card: SwiperCard) {
        deck[currentIndex].swipe = LIKED
        startSlideAnimator(card, right + 10f)
    }

    override fun onRejectButtonClicked(card: SwiperCard) {
        deck[currentIndex].swipe = REJECTED
        startSlideAnimator(card, -(right + 10f))
    }

    override fun onCardStartedSwiping(card: SwiperCard) {
        // A voir pour le future
    }

    override fun onCardSwipingRight(card: SwiperCard, progress: Float, velocity: Float) {
        card.translationX += progress + velocity
    }

    override fun onCardSwipingLeft(card: SwiperCard, progress: Float, velocity: Float) {
        card.translationX += progress + velocity
    }


    override fun onCardSwipedRight(card: SwiperCard) {
        if(card.x + (card.width/2) > right){
            deck[currentIndex].swipe = LIKED
            startSlideAnimator(card, right + 10f)
        } else {
            startComeBackAnimator(card)
        }
    }

    override fun onCardSwipedLeft(card: SwiperCard) {
        if(card.x + (card.width/2) < left){
            deck[currentIndex].swipe = REJECTED
            startSlideAnimator(card, -(right + 10f))
        } else {
            startComeBackAnimator(card)
        }
    }

    /**
     * Wrapped Data Card contenant le resultat d'un swipe
     */
    private data class SwipedData(val data: SwiperData, var swipe: Int)


    /**
     * Définition des constantes
     */
    companion object{
        const val UNDEFINED: Int = 0
        const val LIKED: Int = 1
        const val REJECTED: Int = 2
    }

}