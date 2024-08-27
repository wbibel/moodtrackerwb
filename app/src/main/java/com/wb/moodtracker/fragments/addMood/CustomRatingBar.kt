package com.wb.moodtracker.fragments.addMood

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import com.wb.moodtracker.R

class CustomRatingBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var rating: Int = 0
    private val ratingImageViews: MutableList<ImageView> = mutableListOf()
    private var ratingClickListener: OnRatingClickListener? = null

    init {
        orientation = HORIZONTAL
        setupRatingImageViews()
    }


    private fun setupRatingImageViews() {
        val iconSize = resources.getDimensionPixelSize(R.dimen.rating_icon_size)

        val layoutParams = LayoutParams(iconSize, iconSize)

        for (i in 1..5) {
            val ratingImageView = ImageView(context)
            ratingImageView.layoutParams = layoutParams
            ratingImageView.setImageResource(getRatingDrawable(0))
            ratingImageView.setOnClickListener { onRatingImageViewClick(i) }
            ratingImageViews.add(ratingImageView)
            addView(ratingImageView)
        }
    }


    fun setRating(rating: Int) {
        this.rating = rating

        for (i in ratingImageViews.indices) {
            val drawable = if (i < rating) {
                getRatingDrawable(i + 1)
            } else {
                getRatingDrawable(0)
            }
            ratingImageViews[i].setImageResource(drawable)
        }
    }

    fun getRating(): Int {
        return rating
    }

    fun setOnRatingClickListener(listener: OnRatingClickListener) {
        ratingClickListener = listener
    }

    private fun onRatingImageViewClick(rating: Int) {
        setRating(rating)
        ratingClickListener?.onRatingClicked(rating)
    }

    private fun getRatingDrawable(rating: Int): Int {
        return when (rating) {
            1 -> R.drawable.ic_rating1
            2 -> R.drawable.ic_rating2
            3 -> R.drawable.ic_rating3
            4 -> R.drawable.ic_rating4
            5 -> R.drawable.ic_rating5
            else -> R.drawable.ic_rating0
        }
    }

    interface OnRatingClickListener {
        fun onRatingClicked(rating: Int)
    }
}

