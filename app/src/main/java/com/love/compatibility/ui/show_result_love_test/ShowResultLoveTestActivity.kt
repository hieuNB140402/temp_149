package com.love.compatibility.ui.show_result_love_test

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseActivity
import com.love.compatibility.core.extensions.handleBackLeftToRight
import com.love.compatibility.core.extensions.select
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.extensions.shareImagesPaths
import com.love.compatibility.core.extensions.strings
import com.love.compatibility.core.extensions.visible
import com.love.compatibility.core.helper.AnimationHelper
import com.love.compatibility.core.helper.BitmapHelper
import com.love.compatibility.core.helper.MediaHelper
import com.love.compatibility.core.helper.StringHelper
import com.love.compatibility.core.utils.key.IntentKey
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.databinding.ActivityShowResultLoveTestBinding
import com.love.compatibility.ui.name_test.NameTestActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowResultLoveTestActivity : BaseActivity<ActivityShowResultLoveTestBinding>() {
    private val viewModel: ShowResultLoveTestViewModel by viewModels()
    override fun setViewBinding(): ActivityShowResultLoveTestBinding {
        return ActivityShowResultLoveTestBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        viewModel.updatePercent(intent.getStringExtra(IntentKey.INTENT_KEY) ?: "50:50:50")
        loadAnim()
    }

    override fun viewListener() {
        binding.apply {
            actionBar.btnActionBarLeft.setOnClickListener { handleBackLeftToRight() }
            btnTryAgain.setOnSingleClick { handleTryAgain() }
            btnShare.setOnSingleClick { handleShare() }
        }
    }

    override fun initActionBar() {
        binding.actionBar.apply {
            btnActionBarLeft.setImageResource(R.drawable.ic_back)
            btnActionBarLeft.visible()

            tvCenter.text = StringHelper.capitalizeWords(strings(R.string.love_test))
            tvCenter.visible()
            tvCenter.select()
        }
    }

    override fun initText() {
        binding.apply {
            tvShare.select()
            tvTryAgain.select()
        }
    }

    private fun loadAnim(){
        binding.apply {
            val duration = 1000L

            AnimationHelper.animatePercent(tvPercentNameTest, viewModel.percentName, duration)
            AnimationHelper.animatePercent(tvPercentDateTest, viewModel.percentDate, duration)
            AnimationHelper.animatePercent(tvPercentFingerTest, viewModel.percentFinger, duration)

            AnimationHelper.animateWeight(vNameTest, viewModel.percentName.toFloat(), duration)
            AnimationHelper.animateWeight(vDateOfBirthTest, viewModel.percentDate.toFloat(), duration)
            AnimationHelper.animateWeight(vFingerTest, viewModel.percentFinger.toFloat(), duration)
        }
    }

    private fun handleTryAgain() {
        binding.apply {
            val intent = Intent(this@ShowResultLoveTestActivity, NameTestActivity::class.java).putExtra(
                IntentKey.INTENT_KEY, IntentKey.INTENT_KEY
            )
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    private fun handleShare() {
        lifecycleScope.launch(Dispatchers.IO) {
            showLoading()
            val bit = BitmapHelper.createBimapFromView(binding.lnlExport)
            val file = MediaHelper.saveBitmapToCache(this@ShowResultLoveTestActivity, bit)
            dismissLoading(true)
            shareImagesPaths(arrayListOf(file.absolutePath))
        }
    }
}