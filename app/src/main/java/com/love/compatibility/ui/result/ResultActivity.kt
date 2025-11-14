package com.love.compatibility.ui.result

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseActivity
import com.love.compatibility.core.extensions.gone
import com.love.compatibility.core.extensions.handleBackLeftToRight
import com.love.compatibility.core.extensions.select
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.extensions.shareImagesPaths
import com.love.compatibility.core.extensions.startIntentWithClearTop
import com.love.compatibility.core.extensions.strings
import com.love.compatibility.core.extensions.visible
import com.love.compatibility.core.helper.BitmapHelper
import com.love.compatibility.core.helper.MediaHelper
import com.love.compatibility.core.helper.StringHelper
import com.love.compatibility.core.utils.key.IntentKey
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.databinding.ActivityResultBinding
import com.love.compatibility.ui.name_test.NameTestActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class ResultActivity : BaseActivity<ActivityResultBinding>() {
    private val viewModel: ResultViewModel by viewModels()
    override fun setViewBinding(): ActivityResultBinding {
        return ActivityResultBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        viewModel.updateDataText(intent.getStringExtra(IntentKey.INTENT_KEY) ?: "")
        viewModel.setTypeStart(intent.getIntExtra(IntentKey.TYPE_KEY, ValueKey.TEST_NAME_TYPE))
    }

    override fun dataObservable() {
        binding.apply {
            lifecycleScope.launch {
                viewModel.typeStart.collect { type ->
                    when (type) {
                        ValueKey.TEST_NAME_TYPE -> {
                            setUpNameTestUI()
                        }

                        ValueKey.TEST_DATE_TYPE -> {}
                        ValueKey.TEST_FINGER_TYPE -> {}
                        else -> {}
                    }
                }
            }
        }
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

            tvCenter.visible()
            tvCenter.select()
        }
    }

    private fun setUpNameTestUI() {
        binding.apply {
            val textTopList = viewModel.dataTopText.split(ValueKey.TYPE_SPIT)
            val (textLeft, textRight) = textTopList.first() to textTopList.last()
            tvLeft.text = textLeft
            tvRight.text = textRight

            lnlTextTop.visible()
            btnShowResult.gone()

            actionBar.tvCenter.text = StringHelper.capitalizeWords(strings(R.string.result_question))
        }
    }

    private fun handleTryAgain() {
        binding.apply {
            if (viewModel.typeStart.value != ValueKey.TEST_LOVE_TYPE) {
                val intent = Intent()
                intent.putExtra(IntentKey.INTENT_KEY, IntentKey.INTENT_KEY)
                setResult(RESULT_OK, intent)
                handleBackLeftToRight()
            } else {
                val intent = Intent(this@ResultActivity, NameTestActivity::class.java).putExtra(
                    IntentKey.INTENT_KEY,
                    IntentKey.INTENT_KEY
                )
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
    }

    private fun handleShare() {
        lifecycleScope.launch(Dispatchers.IO) {
            showLoading()
            val bit = BitmapHelper.createBimapFromView(binding.lnlExport)
            val file = MediaHelper.saveBitmapToCache(this@ResultActivity, bit)
            dismissLoading(true)
            shareImagesPaths(arrayListOf(file.absolutePath))
        }
    }
}