package com.love.compatibility.core.helper

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.love.compatibility.core.extensions.gone
import com.love.compatibility.core.extensions.visible

object AnimationHelper{
    // Animate thay đổi margin bottom
    fun animateLift(layout: View, liftBy: Int, duration: Long = 100) {
        val layoutParams = layout.layoutParams as ViewGroup.MarginLayoutParams
        val originalMarginBottom = layoutParams.bottomMargin

        val animator = ValueAnimator.ofInt(originalMarginBottom, originalMarginBottom + liftBy)
        animator.addUpdateListener { valueAnimator ->
            layoutParams.bottomMargin = valueAnimator.animatedValue as Int
            layout.layoutParams = layoutParams
        }
        animator.duration = duration
        animator.start()
    }

    // Reset margin về ban đầu
    fun resetPosition(layout: View, context: Context) {
        val layoutParams = layout.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = 0
        layout.layoutParams = layoutParams
    }

    // Slide in LinearLayout từ trái
    fun slideInFromLeft(view: LinearLayout, layoutParent: ConstraintLayout) {
        val animate = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        animate.duration = 300
        view.visibility = View.VISIBLE
        layoutParent.visible()
        view.startAnimation(animate)
    }

    // Slide out LinearLayout sang trái
    fun slideOutToLeft(view: LinearLayout, layoutParent: ConstraintLayout) {
        val animate = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        animate.duration = 300
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                view.visibility = View.GONE
                layoutParent.gone()
            }
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
        })
        view.startAnimation(animate)
    }

    // Fragment slide in từ phải
    fun startFragmentSlideInFromRight(view: FrameLayout) {
        val animate = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        animate.duration = 300
        view.startAnimation(animate)
    }

    // Fragment back slide in từ trái
    fun backFragmentSlideInFromLeft(view: FrameLayout, onFinish: (() -> Unit)) {
        val animate = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        animate.duration = 300
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                onFinish.invoke()
            }
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
        })
        view.startAnimation(animate)
    }
    fun animatePercent(tv: TextView, target: Int, duration: Long = 5000L) {
        ValueAnimator.ofInt(0, target).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                tv.text = "$value%"
            }
            start()
        }
    }

    fun animateWeight(view: View, targetWeight: Float, duration: Long = 600L) {
        val startWeight = 1f
        val animator = ValueAnimator.ofFloat(startWeight, targetWeight).apply {
            this.duration = duration
            addUpdateListener {
                val animatedWeight = it.animatedValue as Float
                val params = view.layoutParams as LinearLayout.LayoutParams
                params.weight = animatedWeight
                view.layoutParams = params
            }
        }
        animator.start()
    }

}
