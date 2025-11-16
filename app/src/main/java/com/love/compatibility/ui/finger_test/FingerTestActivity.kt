package com.love.compatibility.ui.finger_test

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseActivity
import com.love.compatibility.core.extensions.dLog
import com.love.compatibility.core.extensions.handleBackLeftToRight
import com.love.compatibility.core.extensions.select
import com.love.compatibility.core.extensions.strings
import com.love.compatibility.core.extensions.visible
import com.love.compatibility.core.helper.StringHelper
import com.love.compatibility.core.utils.key.IntentKey
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.databinding.ActivityFingerTestBinding
import com.love.compatibility.ui.age_test.DateOfBirthActivity
import com.love.compatibility.ui.name_test.NameTestViewModel
import com.love.compatibility.ui.result.ResultActivity

class FingerTestActivity : BaseActivity<ActivityFingerTestBinding>() {
    private val viewModel: FingerTestViewModel by viewModels()

    override fun setViewBinding(): ActivityFingerTestBinding {
        return ActivityFingerTestBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        viewModel.updateIsLoveTest(intent.getBooleanExtra(IntentKey.INTENT_KEY, false))
        initAnim()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun viewListener() {
        binding.apply {
            actionBar.btnActionBarLeft.setOnClickListener { handleBackLeftToRight() }

            imvLeftFinger.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        viewModel.updateIsLeftHold(true)
                        updateScanState()
                        true
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        viewModel.updateIsLeftHold(false)
                        updateScanState()
                        true
                    }

                    else -> false
                }
            }

            imvRightFinger.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        viewModel.updateIsRightHold(true)
                        updateScanState()
                        true
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        viewModel.updateIsRightHold(false)
                        updateScanState()
                        true
                    }

                    else -> false
                }
            }

        }
    }

    override fun initActionBar() {
        binding.actionBar.apply {
            btnActionBarLeft.setImageResource(R.drawable.ic_back)
            btnActionBarLeft.visible()

            tvCenter.text = StringHelper.capitalizeWords(strings(R.string.fingerprint_test))
            tvCenter.visible()
            tvCenter.select()
        }
    }

    private fun initAnim() {
        binding.vLineLeft.post {
            val maxY = (binding.vLineLeft.parent as View).height - binding.vLineLeft.height
            viewModel.leftAnimator =
                ObjectAnimator.ofFloat(binding.vLineLeft, "translationY", 0f, maxY.toFloat()).apply {
                    duration = 1500L
                    repeatMode = ObjectAnimator.REVERSE
                    repeatCount = ObjectAnimator.INFINITE
                    interpolator = LinearInterpolator()
                    start()
                }
            viewModel.pauseLeftScan()
        }

        binding.vLineRight.post {
            val maxY = (binding.vLineRight.parent as View).height - binding.vLineRight.height
            viewModel.rightAnimator =
                ObjectAnimator.ofFloat(binding.vLineRight, "translationY", 0f, maxY.toFloat()).apply {
                    duration = 1500L
                    repeatMode = ObjectAnimator.REVERSE
                    repeatCount = ObjectAnimator.INFINITE
                    interpolator = LinearInterpolator()
                    start()
                }
            viewModel.pauseRightScan()
        }
    }

    fun updateScanState() {
        binding.apply {
            val isHolding = viewModel.isLeftHold && viewModel.isRightHold
            // Animation và scan
            if (isHolding) {
                heartView.resumeLevelAnimation()
                heartView.setPercentWithML(3000f, animate = true, 3000) {
                    isEnableScan(false)
                    heartView.pauseLevelAnimation()
                    viewModel.pauseScan()
                    handleResult()
                }
                viewModel.resumeScan()
            } else {
                heartView.pauseLevelAnimation()
                viewModel.pauseScan()
            }

            updateColor(isHolding)
        }

    }

    private fun updateColor(isHolding: Boolean) {
        binding.apply {
            // Màu và background
            val color = ColorStateList.valueOf(getColor(if (isHolding) R.color.pink_ff else R.color.pink_fe))
            val res = if (isHolding) R.drawable.bg_12_solid_pink_ff00 else R.drawable.bg_12_solid_pink_fe

            imvLeftFinger.imageTintList = color
            imvRightFinger.imageTintList = color
            vLineLeft.setBackgroundResource(res)
            vLineRight.setBackgroundResource(res)
        }
    }

    private fun isEnableScan(status: Boolean) {
        binding.apply {
            if (!status) {
                imvLeftFinger.isEnabled = false
                imvRightFinger.isEnabled = false
            } else {
                imvLeftFinger.isEnabled = true
                imvRightFinger.isEnabled = true
            }
        }
    }

    private fun handleResult() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(IntentKey.TYPE_KEY, if (viewModel.isLoveTest) ValueKey.TEST_LOVE_TYPE else ValueKey.TEST_FINGER_TYPE)
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent, options.toBundle())
    }

    override fun onRestart() {
        super.onRestart()
        binding.heartView.resetLevel()
        isEnableScan(true)
        viewModel.pauseScan()
        updateColor(false)
    }
}