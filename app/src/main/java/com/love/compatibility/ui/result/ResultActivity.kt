package com.love.compatibility.ui.result

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseActivity
import com.love.compatibility.core.extensions.gone
import com.love.compatibility.core.extensions.handleBackLeftToRight
import com.love.compatibility.core.extensions.hideNavigation
import com.love.compatibility.core.extensions.select
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.extensions.shareImagesPaths
import com.love.compatibility.core.extensions.startIntentRightToLeft
import com.love.compatibility.core.extensions.startIntentWithClearTop
import com.love.compatibility.core.extensions.strings
import com.love.compatibility.core.extensions.visible
import com.love.compatibility.core.helper.AnimationHelper
import com.love.compatibility.core.helper.BitmapHelper
import com.love.compatibility.core.helper.LanguageHelper
import com.love.compatibility.core.helper.MediaHelper
import com.love.compatibility.core.helper.StringHelper
import com.love.compatibility.core.utils.key.IntentKey
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.databinding.ActivityResultBinding
import com.love.compatibility.dialog.ChooseQuantityQuestionDialog
import com.love.compatibility.ui.home.HomeActivity
import com.love.compatibility.ui.home.HomeViewModel
import com.love.compatibility.ui.name_test.NameTestActivity
import com.love.compatibility.ui.question_test.QuestionTestActivity
import com.love.compatibility.ui.show_result_love_test.ShowResultLoveTestActivity
import com.love.compatibility.ui.show_result_question_test.ShowResultQuestionTestActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class ResultActivity : BaseActivity<ActivityResultBinding>() {
    private val viewModel: ResultViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

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
                    if (type != -1){
                        viewModel.setPercentLoveTest()
                        when (type) {
                            ValueKey.TEST_NAME_TYPE -> { setUpNameTestUI() }
                            ValueKey.TEST_DATE_TYPE -> { setUpDateOfBirthTestUI() }
                            ValueKey.TEST_FINGER_TYPE -> { setUpFingerTestUI() }
                            ValueKey.TEST_LOVE_TYPE -> { setUpLoveTestUI() }
                            else -> { setUpQuestionTestUI() }
                        }
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
            btnShowResult.setOnSingleClick { handleShowResult() }
        }
    }

    override fun initActionBar() {
        binding.actionBar.apply {
            btnActionBarLeft.setImageResource(R.drawable.ic_back)
            btnActionBarLeft.visible()

            tvCenter.text = StringHelper.capitalizeWords(strings(R.string.result_question))
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

            tvComment.text = strings(viewModel.textComment)
            heartView.post {
                AnimationHelper.animatePercent(tvPercent, viewModel.percentText, 5000)
                heartView.setPercent(viewModel.percentHeart, animate = true, 5000)
            }
        }
    }

    private fun setUpDateOfBirthTestUI() {
        binding.apply {
            val textTopList = viewModel.dataTopText.split(ValueKey.TYPE_SPIT)
            val (textLeft, textRight) = textTopList.first() to textTopList.last()
            tvLeft.text = textLeft
            tvRight.text = textRight

            lnlTextTop.visible()
            btnShowResult.gone()

            tvComment.text = strings(viewModel.textComment)
            heartView.post {
                AnimationHelper.animatePercent(tvPercent, viewModel.percentText, 5000)
                heartView.setPercent(viewModel.percentHeart, animate = true, 5000)
            }
        }
    }

    private fun setUpFingerTestUI() {
        binding.apply {
            lnlTextTop.gone()
            btnShowResult.gone()

            tvComment.text = strings(viewModel.textComment)
            heartView.post {
                AnimationHelper.animatePercent(tvPercent, viewModel.percentText, 5000)
                heartView.setPercent(viewModel.percentHeart, animate = true, 5000)
            }
        }
    }

    private fun setUpLoveTestUI(){
        binding.apply {
            viewModel.updateTypeShowResult(ValueKey.LOVE_TEST)
            viewModel.updatePercentLoveTest(viewModel.percentText)
            lnlTextTop.gone()
            btnShowResult.visible()

            tvComment.text = strings(viewModel.textComment)
            heartView.post {
                AnimationHelper.animatePercent(tvPercent, viewModel.percentText, 5000)
                heartView.setPercent(viewModel.percentHeart, animate = true, 5000)
            }
        }
    }

    private fun setUpQuestionTestUI(){
        binding.apply {
            viewModel.updateTypeShowResult(ValueKey.QUESTION_TEST)
            viewModel.updatePercentQuestionTest(this@ResultActivity)
            lnlTextTop.gone()
            btnShowResult.visible()

            tvComment.text = strings(viewModel.textComment)
            heartView.post {
                AnimationHelper.animatePercent(tvPercent, viewModel.percentText, 5000)
                heartView.setPercent(viewModel.percentHeart, animate = true, 5000)
            }
        }
    }

    private fun handleTryAgain() {
        binding.apply {
            when(viewModel.typeStart.value){
                ValueKey.TEST_LOVE_TYPE -> {
                    val intent = Intent(this@ResultActivity, NameTestActivity::class.java).putExtra(
                        IntentKey.INTENT_KEY, IntentKey.INTENT_KEY
                    )
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))

                }
                ValueKey.QUESTION_TYPE -> {
                    handleChooseQuestionTest()
                }
                else -> {
                    val intent = Intent()
                    intent.putExtra(IntentKey.INTENT_KEY, IntentKey.INTENT_KEY)
                    setResult(RESULT_OK, intent)
                    handleBackLeftToRight()
                }
            }
        }
    }

    private fun handleShare() {
        lifecycleScope.launch(Dispatchers.IO) {
            showLoading()
            val bit = BitmapHelper.createBimapFromView(binding.ctlExport)
            val file = MediaHelper.saveBitmapToCache(this@ResultActivity, bit)
            dismissLoading(true)
            shareImagesPaths(arrayListOf(file.absolutePath))
        }
    }

    private fun handleShowResult(){
        val intent = if (viewModel.typeShowResult == ValueKey.LOVE_TEST){
            Intent(this, ShowResultLoveTestActivity::class.java)
        }else{
            Intent(this, ShowResultQuestionTestActivity::class.java)
        }

        if (viewModel.typeShowResult == ValueKey.LOVE_TEST){
            intent.putExtra(IntentKey.INTENT_KEY, viewModel.percentLoveTest)
        }

        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent, options.toBundle())
    }

    private fun handleChooseQuestionTest() {
        val dialog = ChooseQuantityQuestionDialog(this)
        LanguageHelper.setLocale(this)
        dialog.show()
        dialog.onDismissClick = {
            dialog.dismiss()
            this.hideNavigation(true)
        }
        dialog.onChooseQuantity = { quantity ->
            dialog.dismiss()
            lifecycleScope.launch {
                showLoading()
                homeViewModel.loadQuestionsList(this@ResultActivity, sharePreference, quantity)
                dismissLoading(true)
                val intent = Intent(this@ResultActivity, QuestionTestActivity::class.java).putExtra(
                    IntentKey.INTENT_KEY, IntentKey.INTENT_KEY
                )
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
    }
}